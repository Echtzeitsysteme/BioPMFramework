package de.tu.darmstadt.es.PatternMatchingEngine.viatra.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.transformation.evm.api.ActivationLifeCycle;
import org.eclipse.viatra.transformation.evm.api.ExecutionSchema;
import org.eclipse.viatra.transformation.evm.specific.Lifecycles;
import org.eclipse.viatra.transformation.evm.specific.crud.CRUDActivationStateEnum;
import org.eclipse.viatra.transformation.runtime.emf.rules.EventDrivenTransformationRuleGroup;
import org.eclipse.viatra.transformation.runtime.emf.rules.eventdriven.EventDrivenTransformationRule;
import org.eclipse.viatra.transformation.runtime.emf.rules.eventdriven.EventDrivenTransformationRuleFactory;
import org.eclipse.viatra.transformation.runtime.emf.transformation.eventdriven.EventDrivenTransformation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;

public class ViatraRuntime {
	/**
	 * Transformation-related extensions
	 */
	@Extension
	private EventDrivenTransformation transformation;

	/**
	 * Transformation rule-related extensions
	 */
	@Extension
	private EventDrivenTransformationRuleFactory eventDrivenTransformationRuleFactory = new EventDrivenTransformationRuleFactory();
	
	private ViatraQueryEngine engine;
	
	private ResourceSet resourceSet;
	
	private IRuleHandler ruleHandler;
	
	@SuppressWarnings("unchecked")
	public ViatraRuntime(ResourceSet resourceSet, IRuleHandler ruleHandler) {
		try {
			this.resourceSet = resourceSet;
			this.ruleHandler = ruleHandler;
			Collections.<Logger>list(LogManager.getCurrentLoggers()).stream().forEach(log -> log.setLevel(Level.OFF));
			LogManager.getRootLogger().setLevel(Level.OFF);			
		} catch (Exception e) {
			Exceptions.sneakyThrow(e);
		}
	}
	
	public EventDrivenTransformation createTransformation() {
		try {
			Collection<Class<? extends IPatternMatch> > classes = ruleHandler.getMatches();
			List<EventDrivenTransformationRule<?, ?>> rules = classes.parallelStream().map(this::createRule).collect(Collectors.toList());
			EventDrivenTransformation.EventDrivenTransformationBuilder forEngine = EventDrivenTransformation.forEngine(this.engine);
			EventDrivenTransformationRuleGroup transformationRuleGroup = createEventDrivenTransformationRuleGroup(rules);
			EventDrivenTransformation.EventDrivenTransformationBuilder addRules = forEngine.addRules(transformationRuleGroup);
			EventDrivenTransformation build = addRules.build();
			return this.transformation = build;
		} catch (Throwable _e) {
			throw Exceptions.sneakyThrow(_e);
		}
	}
	
	public void initialize() {
		try {
			EMFScope scope = new EMFScope(resourceSet);
			engine = ViatraQueryEngine.on(scope);
		} catch (Exception e) {
			Exceptions.sneakyThrow(e);
		}
	}
	
	public void execute() {
		ExecutionSchema executionSchema = this.transformation.getExecutionSchema();
		executionSchema.startUnscheduledExecution();
	}
	
	private EventDrivenTransformationRuleGroup createEventDrivenTransformationRuleGroup(List<EventDrivenTransformationRule<?, ?>> rulesList) {
		EventDrivenTransformationRule<?, ?>[] rules = new EventDrivenTransformationRule<?, ?>[rulesList.size()];
		return new EventDrivenTransformationRuleGroup(rulesList.toArray(rules));
	}
	
	@SuppressWarnings("unchecked")
	private<Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> EventDrivenTransformationRule<Match, Matcher> createRule(Class<Match> clazz) {
		try {
			IQuerySpecification<Matcher> querySpecifiaction = null;
			Package superpackage = clazz.getPackage();
			
			String superpackageName = superpackage.getName();
			
			String ruleName = clazz.getSimpleName().substring(0, clazz.getSimpleName().length()-5);
			
			Class<?> testType = Class.forName(superpackageName+".util."+ruleName+"QuerySpecification");
			
			
			if(IQuerySpecification.class.isAssignableFrom(testType))
				querySpecifiaction=createQuerySpecification((Class<? extends IQuerySpecification<Matcher>>) testType);
			
			
			
			final IMatchProcessor<Match> action1 = it -> this.ruleHandler.addMatch(ruleName, it);
			
			final IMatchProcessor<Match> action2 = it -> this.ruleHandler.removeMatch(ruleName, it);
			
			ActivationLifeCycle defaultlifeCycle = Lifecycles.getDefault(false, true);
			
			EventDrivenTransformationRuleFactory.EventDrivenTransformationRuleBuilder<Match, Matcher> createRule = eventDrivenTransformationRuleFactory.createRule();
			
			createRule.precondition(querySpecifiaction);
			
			createRule.action(CRUDActivationStateEnum.CREATED, action1);
			
			createRule.action(CRUDActivationStateEnum.DELETED, action2);
			
			createRule.addLifeCycle(defaultlifeCycle);
			
			return createRule.build();
		} catch (Exception e) {
			throw Exceptions.sneakyThrow(e);
		}
	}
	
	private <Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> IQuerySpecification<Matcher> createQuerySpecification(	Class<? extends IQuerySpecification<Matcher>> querySpecifiactionClass) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		List<Method> methods = Arrays.asList(querySpecifiactionClass.getMethods());
		Optional<Method> monad = methods.parallelStream().filter(method -> method.getName().equals("instance"))	.findFirst();
		Method instanceMethod = monad.get();
		Object specification = instanceMethod.invoke(null, new Object[0]);
		if (querySpecifiactionClass.isInstance(specification)) {
			return querySpecifiactionClass.cast(specification);
		}

		return null;
	}
	
}
