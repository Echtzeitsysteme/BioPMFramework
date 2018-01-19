package de.tu.darmstadt.es.biochemicalSimulationFramework.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import de.tu.darmstadt.es.biochemicalSimulationFramework.internal.utils.FrameworkFactory;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.patternmatchingengine.PatternMatchingEngine;

public class FrameworkHelper {
	
	private static FrameworkHelper instance;
	
	private Map<Class<? extends PatternMatchingEngine>,PatternMatchingEngine> engines;
	
	private FrameworkHelper() {
		engines = new HashMap<>();
	}
	
	public static FrameworkHelper instance() {
		if(instance == null)
			instance = new FrameworkHelper();
		return instance;
	}
	
	public void addEngine(PatternMatchingEngine engine) {
		engines.put(engine.getClass(), engine);
	}
	
	public Collection<PatternMatchingEngine> getEngines(){
		return engines.values();
	}

	public <PM extends PatternMatchingEngine> PM getPatternMatcher (Class<PM> clazz) {
		PatternMatchingEngine engine = engines.get(clazz);
		if(engine != null) {
			return clazz.cast(engine);
		}
		return null;
	}
	
	public <PM extends PatternMatchingEngine> PM getOrCreatePatternMatcher (Class<PM> clazz, String packageName) {
		PatternMatchingEngine engine = engines.get(clazz);
		if(engine != null) {
			return clazz.cast(engine);
		}
		return FrameworkFactory.instance().createPatternMatchingEngine(packageName, new ResourceSetImpl(), clazz);
	}
}
