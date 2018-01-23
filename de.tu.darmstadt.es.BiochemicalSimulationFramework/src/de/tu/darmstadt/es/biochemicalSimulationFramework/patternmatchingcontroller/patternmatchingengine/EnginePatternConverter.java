package de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.patternmatchingengine;

import de.tu.darmstadt.es.KappaRules.KappaRuleContainer;

public interface EnginePatternConverter {
	void convertToPatternModel(KappaRuleContainer container);
	PatternMatchingEngine getEngine(); 
}
