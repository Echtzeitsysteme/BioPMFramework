package de.tu.darmstadt.es.biochemicalSimulationFramework;

import org.eclipse.emf.ecore.resource.ResourceSet;

import de.tu.darmstadt.es.biochemicalSimulationFramework.internal.splitter.HybridSplitter;
import de.tu.darmstadt.es.biochemicalSimulationFramework.internal.splitter.NoneSplitter;
import de.tu.darmstadt.es.biochemicalSimulationFramework.internal.utils.FrameworkFactory;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.HybridPatternMatchingController;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.PatternMatchingController;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.geolibrary.GeoLibrary;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.patternmatchingengine.PatternMatchingEngine;
import de.tu.darmstadt.es.biochemicalSimulationFramework.persistence.Persistence;
import de.tu.darmstadt.es.biochemicalSimulationFramework.splitter.Splitter;

public class SimulatorConfigurator {
	
	public SimulatorConfigurator (String packageName, ResourceSet resourceSet, Splitter splitter, Class<? extends PatternMatchingController> pmControllerType, Class<? extends PatternMatchingEngine> engineType) {
		this(packageName, resourceSet, splitter, pmControllerType, engineType, null);
	}
	
	public SimulatorConfigurator (String packageName, ResourceSet resourceSet, Class<? extends PatternMatchingController> pmControllerType, Class<? extends PatternMatchingEngine> engineType) {
		this(packageName, resourceSet, null, pmControllerType, engineType, null);
	}
	
	public SimulatorConfigurator (String packageName, ResourceSet resourceSet, Class<? extends PatternMatchingController> pmControllerType, Class<? extends PatternMatchingEngine> engineType, Class<? extends GeoLibrary> geoLibType) {
		this(packageName, resourceSet, null, pmControllerType, engineType, geoLibType);
	}
	
	public SimulatorConfigurator (String packageName, ResourceSet resourceSet, Splitter splitter,Class<? extends PatternMatchingController> pmControllerType, Class<? extends PatternMatchingEngine> engineType, Class<? extends GeoLibrary> geoLibType) {
		Splitter currentSplitter;
		if(splitter == null) {
			currentSplitter = new NoneSplitter();
		}else {
			currentSplitter = splitter;
		}
		
		if(HybridPatternMatchingController.class.isAssignableFrom(pmControllerType)) {
			this.splitter = new HybridSplitter(currentSplitter);
		}
		else {
			this.splitter = currentSplitter;
		}
		
		patternMatchingController = FrameworkFactory.instance().createPatternMatchingController(packageName, resourceSet, engineType, geoLibType, pmControllerType);
	}
	
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
