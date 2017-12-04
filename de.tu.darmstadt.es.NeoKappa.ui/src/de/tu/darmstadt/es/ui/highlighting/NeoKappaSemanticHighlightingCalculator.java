package de.tu.darmstadt.es.ui.highlighting;

import com.google.inject.Inject;

import de.tu.darmstadt.es.services.NeoKappaGrammarAccess;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractSemanticHighlightingCalculator;

public class NeoKappaSemanticHighlightingCalculator extends AbstractSemanticHighlightingCalculator {

	@Inject
	NeoKappaGrammarAccess ga;



}
