package de.tu.darmstadt.es.PatternMatchingEngine.viatra.template;

import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.patternmatchingengine.MainClassTemplate;

public abstract class AbstractVIATRAMainTemplate  extends MainClassTemplate{
	
	protected String packageName;
	protected String patternPackageString;
	
	
	public AbstractVIATRAMainTemplate(String packageName) {
		this.packageName = packageName;
		patternPackageString = packageName + ".patterns";
	}
	
	protected String getPackageBinaryPath() {
		return "bin/" + packageName + "/patterns/";
	}
	
	protected abstract String getImports();
	protected abstract String getStaticVariables();
	protected abstract String getPackageDesciption();
	protected abstract String getClassDescription();
	protected abstract String getClassBody();
	protected abstract String getMethods();

}
