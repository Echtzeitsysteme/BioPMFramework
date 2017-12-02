package de.tu.darmstadt.es.ui.highlighting;

import com.google.inject.Inject;

import de.tu.darmstadt.es.services.KappaLiteGrammarAccess;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractSemanticHighlightingCalculator;

public class KappaLiteSemanticHighlightingCalculator extends AbstractSemanticHighlightingCalculator {

	@Inject
	KappaLiteGrammarAccess ga;



}
