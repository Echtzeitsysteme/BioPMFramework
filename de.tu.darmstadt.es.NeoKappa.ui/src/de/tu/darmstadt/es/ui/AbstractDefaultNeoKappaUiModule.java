package de.tu.darmstadt.es.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;

import com.google.inject.Binder;

import de.tu.darmstadt.es.ui.highlighting.NeoKappaHighlightProviderController;
import de.tu.darmstadt.es.ui.highlighting.NeoKappaeHighlightingConfiguration;
import de.tu.darmstadt.es.ui.highlighting.NeoKappaSemanticHighlightingCalculator;
import de.tu.darmstadt.es.ui.highlighting.NeoKappaTokenMapper;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightProviderController;

public abstract class AbstractDefaultNeoKappaUiModule extends AbstractNeoKappaUiModule {

	protected AbstractHighlightProviderController controller;
	
	public AbstractDefaultNeoKappaUiModule(AbstractUIPlugin plugin) {
		super(plugin);
		controller = new NeoKappaHighlightProviderController();
		
	}

	public Class<? extends IHighlightingConfiguration> bindIHighlightingConfiguration()
	{
	   return NeoKappaeHighlightingConfiguration.class;
	}
	
	@Override
	public void configure(Binder binder) {
		controller.bind(binder, NeoKappaSemanticHighlightingCalculator.class, NeoKappaTokenMapper.class);
		super.configure(binder);
	}
	
}
