package de.tu.darmstadt.es.xtext.utils.ui.highlighting;

import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;

import de.tu.darmstadt.es.xtext.utils.ui.highlighting.utils.XtextColorManager;

public abstract class AbstractHighlightingConfiguration extends DefaultHighlightingConfiguration
{
	protected XtextColorManager colorManager;
	
	public AbstractHighlightingConfiguration(){
		super();
		this.colorManager = new XtextColorManager();
		colorManager.setConfig(this);
	}
	
	public XtextColorManager getColorManager() {
		return colorManager;
	}


   
}
