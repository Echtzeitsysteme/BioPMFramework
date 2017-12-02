package de.tu.darmstadt.es.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;

import com.google.inject.Binder;

import de.tu.darmstadt.es.ui.highlighting.KappaLiteHighlightProviderController;
import de.tu.darmstadt.es.ui.highlighting.KappaLiteHighlightingConfiguration;
import de.tu.darmstadt.es.ui.highlighting.KappaLiteSemanticHighlightingCalculator;
import de.tu.darmstadt.es.ui.highlighting.KappaLiteTokenMapper;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightProviderController;

public abstract class AbstractDefaultKappaLiteUiModule extends AbstractKappaLiteUiModule {

	protected AbstractHighlightProviderController controller;
	
	public AbstractDefaultKappaLiteUiModule(AbstractUIPlugin plugin) {
		super(plugin);
		controller = new KappaLiteHighlightProviderController();
		
	}

	public Class<? extends IHighlightingConfiguration> bindIHighlightingConfiguration()
	{
	   return KappaLiteHighlightingConfiguration.class;
	}
	
	@Override
	public void configure(Binder binder) {
		controller.bind(binder, KappaLiteSemanticHighlightingCalculator.class, KappaLiteTokenMapper.class);
		super.configure(binder);
	}
	
}
