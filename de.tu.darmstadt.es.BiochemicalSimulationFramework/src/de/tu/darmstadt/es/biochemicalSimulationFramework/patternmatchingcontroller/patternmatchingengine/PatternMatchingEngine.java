package de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.patternmatchingengine;

import org.eclipse.emf.ecore.resource.ResourceSet;

public abstract class PatternMatchingEngine{
	
	private static final String PACKAGE_SUFFIX = ".patterns";
	
	protected String packageName;
	protected String patternPackageName; 
	protected ResourceSet resourceSet;
	
	public PatternMatchingEngine(String packageName, ResourceSet resourceSet) {
		this.packageName = packageName;
		this.patternPackageName = packageName + PACKAGE_SUFFIX;
		this.resourceSet = resourceSet;
	}
	
	public abstract void createController();
	
    public abstract EnginePatternConverter getConverter();
	
	public abstract void run(Object... objects);
	
	public abstract MainClassTemplate getMainClassTemplate();
	
	public abstract Manipulation getManipulation();
}
