package de.tu.darmstadt.es.PatternMatchingEngine.viatra;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel;

import de.tu.darmstadt.es.KappaRules.KappaRuleContainer;
import de.tu.darmstadt.es.PatternMatchingEngine.viatra.modelconverter.RuleToPatternConverter;
import de.tu.darmstadt.es.PatternMatchingEngine.viatra.runtime.IRuleHandler;
import de.tu.darmstadt.es.PatternMatchingEngine.viatra.runtime.RuleHandler;
import de.tu.darmstadt.es.PatternMatchingEngine.viatra.runtime.ViatraRuntime;
import de.tu.darmstadt.es.PatternMatchingEngine.viatra.template.VIATRAMainTemplate;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.patternmatchingengine.EnginePatternConverter;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.patternmatchingengine.MainClassTemplate;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.patternmatchingengine.PatternMatchingEngine;

public class VIATRAEngine extends PatternMatchingEngine{
	
	private RuleToPatternConverter ruleToPatternConverter;
	private RuleHandler ruleHandler;
	private ViatraRuntime runtime;
	private VIATRAMainTemplate mainTemplate;
	private boolean loaded = false;
	
	
	public VIATRAEngine(String packageName, ResourceSet resourceSet) {
		super(packageName, resourceSet);		
		ruleHandler = new RuleHandler(this.patternPackageName, this);		
	}
	
	public void setLoaded(boolean load) {
		loaded=load;
	}

	@Override
	public void run(Object... objects) {
		try {
			List<Object> objectsList = Arrays.asList(objects);
			List<Class<?>> classes = objectsList.parallelStream().filter(Class.class::isInstance).map(Class.class::cast).collect(Collectors.toList());
			
			ruleHandler.loadMatches(classes);
			
			getRuntime().initialize();
			getRuntime().createTransformation();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loaded = true;		
	}

	public ViatraRuntime getRuntime() {
		if(runtime == null) {
			runtime = new ViatraRuntime(resourceSet, ruleHandler);
		}
		return this.runtime;
	}
	
	public IRuleHandler getRuleHandler() {
		return this.ruleHandler;
	}
	
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public EnginePatternConverter getConverter() {
		if(ruleToPatternConverter == null)
			createController();
		return ruleToPatternConverter;
	}

	@Override
	public void createController() {
		ruleToPatternConverter = new RuleToPatternConverter(this, packageName, resourceSet);		
	}

	@Override
	public MainClassTemplate getMainClassTemplate() {
		if(mainTemplate == null)
			mainTemplate = new VIATRAMainTemplate(packageName);
		return mainTemplate;
	}
	
}
