package de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.patternmatchingengine;

import org.eclipse.emf.ecore.resource.ResourceSet;

import de.tu.darmstadt.es.KappaRules.KappaRuleContainer;
import de.tu.darmstadt.es.biochemicalSimulationFramework.utils.FrameworkHelper;

public abstract class PatternMatchingEngine{
	
	private static final String PACKAGE_SUFFIX = ".patterns";
	
	protected String packageName;
	protected ResourceSet resourceSet;
	
	public PatternMatchingEngine(String packageName, ResourceSet resourceSet) {
		this.packageName = packageName + PACKAGE_SUFFIX;
		this.resourceSet = resourceSet;
		FrameworkHelper.instance().addEngine(this);
	}
	
	public abstract void createController();
	
    public abstract EnginePatternConverter getConverter();
	
	public abstract void run(Object... objects);
}
