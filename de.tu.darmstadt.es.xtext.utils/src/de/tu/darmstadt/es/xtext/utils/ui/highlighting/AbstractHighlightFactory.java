package de.tu.darmstadt.es.xtext.utils.ui.highlighting;

public abstract class AbstractHighlightFactory {
	protected AbstractHighlightProviderController controller;
	
	/**
	 * In this method must all new HighlightingRules be created. If a Rule is not created it will not be used.
	 */
	public abstract void createAllInstances();
	
	void setController(AbstractHighlightProviderController controller){
		this.controller = controller;
	}
}
