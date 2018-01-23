package de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.resource.ResourceSet;

import de.tu.darmstadt.es.KappaRules.KappaRuleContainer;
import de.tu.darmstadt.es.biochemicalSimulationFramework.internal.geolibrary.NoneGeoLibrary;
import de.tu.darmstadt.es.biochemicalSimulationFramework.internal.utils.FrameworkFactory;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.geolibrary.GeoLibrary;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.modification.Modification;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.patternmatchingengine.PatternMatchingEngine;

public abstract class PatternMatchingController {
	
	protected GeoLibrary geoLib;
	
	protected Map<String, Modification> modifications;
	
	protected PatternMatchingEngine patternMatchingEngine;
	
	private String packageName;
	
	protected ResourceSet resourceSet;
	
	public PatternMatchingController(String packageName, ResourceSet resourceSet, Class<? extends PatternMatchingEngine> engineType) {
		this(packageName,resourceSet, engineType, null);
	}
	
	public PatternMatchingController(String packageName, ResourceSet resourceSet, Class<? extends PatternMatchingEngine> engineType, Class<? extends GeoLibrary> geoLibType) {
		this.packageName = packageName;
		this.resourceSet = resourceSet;
		if(engineType != null) {
			createPatternMatchingEngine(engineType);
		}
		
		if(geoLibType == null) {
			geoLib = new NoneGeoLibrary();
		}
		
	}
	
	private void convertKappaRuleContainerToModifications(KappaRuleContainer krc) {
		
	}
	
	private void createPatternMatchingEngine (Class<? extends PatternMatchingEngine> clazz) {
		patternMatchingEngine = FrameworkFactory.instance().createPatternMatchingEngine(packageName, resourceSet, clazz);
	}

	public GeoLibrary getGeoLib() {
		return geoLib;
	}

	public Collection<Modification> getModifications() {
		return modifications.values();
	}

	public PatternMatchingEngine getPatternMatchingEngine() {
		return patternMatchingEngine;
	}	
	
}
