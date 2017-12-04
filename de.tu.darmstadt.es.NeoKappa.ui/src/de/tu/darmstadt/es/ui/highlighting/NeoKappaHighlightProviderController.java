package de.tu.darmstadt.es.ui.highlighting;

import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightProviderController;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightingConfiguration;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractSemanticHighlightingCalculator;

public class NeoKappaHighlightProviderController extends AbstractHighlightProviderController {

	public NeoKappaHighlightProviderController() {
		super(new NeoKappaHighlightingFactory());
	}

	@Override
	protected AbstractSemanticHighlightingCalculator createtSemanticHighlightingCalculator() {
		return new NeoKappaSemanticHighlightingCalculator();
	}

	@Override
	protected AbstractHighlightingConfiguration createConfig() {
		return new NeoKappaeHighlightingConfiguration();
	}

}
