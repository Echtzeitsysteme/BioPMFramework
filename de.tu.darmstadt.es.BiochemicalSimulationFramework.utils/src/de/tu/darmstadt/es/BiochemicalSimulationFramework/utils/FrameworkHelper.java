package de.tu.darmstadt.es.BiochemicalSimulationFramework.utils;

import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import de.tu.darmstadt.es.PatternMatchingEngine.viatra.VIATRAEngine;
import de.tu.darmstadt.es.biochemicalSimulationFramework.SimulatorConfigurator;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.NaivePatternMatchingController;


public class FrameworkHelper {
	
	private static FrameworkHelper instance;
	
	private FrameworkHelper() {
		
	}
	
	public static FrameworkHelper instance() {
		if(instance == null)
			instance = new FrameworkHelper();
		return instance;
	}
	
	public SimulatorConfigurator getDefaultConfig(String packageName) {
		return new SimulatorConfigurator(packageName, new ResourceSetImpl(), NaivePatternMatchingController.class, VIATRAEngine.class);
	}

	

}
