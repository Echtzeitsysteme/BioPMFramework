package de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller;

import org.eclipse.emf.ecore.resource.ResourceSet;

import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.geolibrary.GeoLibrary;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.patternmatchingengine.PatternMatchingEngine;

public class HybridPatternMatchingController extends PatternMatchingController {

	public HybridPatternMatchingController(String packageName, ResourceSet resourceSet,
			Class<? extends PatternMatchingEngine> engineType, Class<? extends GeoLibrary> geoLibType) {
		super(packageName, resourceSet, engineType, geoLibType);
	}
	
	public HybridPatternMatchingController(String packageName, ResourceSet resourceSet,
			Class<? extends PatternMatchingEngine> engineType) {
		super(packageName, resourceSet, engineType);
	}

}
