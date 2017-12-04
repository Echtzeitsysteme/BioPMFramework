package de.tu.darmstadt.es.biochemicalSimulationFramework;

import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.PatternMatchingController;
import de.tu.darmstadt.es.biochemicalSimulationFramework.persistence.Persistence;
import de.tu.darmstadt.es.biochemicalSimulationFramework.splitter.Splitter;

public class SimulatorConfigurator {
	
	private Splitter splitter;
	
	private PatternMatchingController patternMatchingController;
	
	private Persistence persistence;
	
	public PatternMatchingController getPatternMatchingController() {
		return patternMatchingController;
	}

	public Persistence getPersistence() {
		return persistence;
	}

	public Splitter getSplitter() {
		return splitter;
	}
	
	

}
