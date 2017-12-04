package de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller;

import java.util.List;

import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.geolibrary.GeoLibrary;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.modification.Modification;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.patternmatchingengine.PatternMatchingEngine;

public abstract class PatternMatchingController {
	private GeoLibrary geoLib;
	
	private List<Modification> modifications;
	
	private PatternMatchingEngine patternMatchingEngine;

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
