package de.tu.darmstadt.es.ui.highlighting;

import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightProviderController;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightingConfiguration;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractSemanticHighlightingCalculator;

public class KappaLiteHighlightProviderController extends AbstractHighlightProviderController {

	public KappaLiteHighlightProviderController() {
		super(new KappaLiteHighlightingFactory());
	}

	@Override
	protected AbstractSemanticHighlightingCalculator createtSemanticHighlightingCalculator() {
		return new KappaLiteSemanticHighlightingCalculator();
	}

	@Override
	protected AbstractHighlightingConfiguration createConfig() {
		return new KappaLiteHighlightingConfiguration();
	}

}
