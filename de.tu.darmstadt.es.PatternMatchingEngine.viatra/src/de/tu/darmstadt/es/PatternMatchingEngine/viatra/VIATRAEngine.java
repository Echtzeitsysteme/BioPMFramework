package de.tu.darmstadt.es.PatternMatchingEngine.viatra;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel;

import de.tu.darmstadt.es.KappaRules.KappaRuleContainer;
import de.tu.darmstadt.es.PatternMatchingEngine.viatra.modelconverter.RuleToPatternConverter;
import de.tu.darmstadt.es.PatternMatchingEngine.viatra.runtime.IRuleHandler;
import de.tu.darmstadt.es.PatternMatchingEngine.viatra.runtime.RuleHandler;
import de.tu.darmstadt.es.PatternMatchingEngine.viatra.runtime.ViatraRuntime;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.patternmatchingengine.PatternMatchingEngine;

public class VIATRAEngine extends PatternMatchingEngine{
	
	private RuleToPatternConverter ruleToPatternConverter;
	private PatternModel patternModel;
	private RuleHandler ruleHandler;
	private ViatraRuntime runtime;
	private boolean loaded = false;
	
	
	public VIATRAEngine(String packageName, ResourceSet resourceSet) {
		super(packageName, resourceSet);
		ruleToPatternConverter = new RuleToPatternConverter(this);
		ruleHandler = new RuleHandler(this.packageName, this);
		runtime = new ViatraRuntime(resourceSet, ruleHandler);
	}

	@Override
	public void convertToPatternModel(KappaRuleContainer container) {
		patternModel = ruleToPatternConverter.createPatternModel(container, this.packageName);
		loaded = false;
	}

	@Override
	public void run() {
		try {
			ruleHandler.loadMatches();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loaded = true;		
	}

	public IRuleHandler getRuleHandler() {
		return this.ruleHandler;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
}
