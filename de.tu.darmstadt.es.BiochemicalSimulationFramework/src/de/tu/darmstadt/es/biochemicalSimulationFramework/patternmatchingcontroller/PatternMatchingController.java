package de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller;

import java.util.List;

import org.eclipse.emf.ecore.resource.ResourceSet;

import de.tu.darmstadt.es.biochemicalSimulationFramework.internal.utils.FrameworkFactory;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.geolibrary.GeoLibrary;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.modification.Modification;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.patternmatchingengine.PatternMatchingEngine;

public abstract class PatternMatchingController {
	private GeoLibrary geoLib;
	
	private List<Modification> modifications;
	
	private PatternMatchingEngine patternMatchingEngine;
	
	public void createPatternMatchingEngine (String packageName, ResourceSet resourceSet, Class<? extends PatternMatchingEngine> clazz) {
		patternMatchingEngine = FrameworkFactory.instance().createPatternMatchingEngine(packageName, resourceSet, clazz);
	}

	public GeoLibrary getGeoLib() {
		return geoLib;
	}

	public List<Modification> getModifications() {
		return modifications;
	}

	public PatternMatchingEngine getPatternMatchingEngine() {
		return patternMatchingEngine;
	}	
	
}
