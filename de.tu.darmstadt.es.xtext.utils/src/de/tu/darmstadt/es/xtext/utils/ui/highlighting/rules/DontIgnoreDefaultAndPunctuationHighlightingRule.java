package de.tu.darmstadt.es.xtext.utils.ui.highlighting.rules;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.nodemodel.INode;

import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightingConfiguration;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractTokenMapper;

public class DontIgnoreDefaultAndPunctuationHighlightingRule extends AbstractIgnoreHighlightingRule {

	public DontIgnoreDefaultAndPunctuationHighlightingRule() {
		super("DontIgnoreDefault", "An Ignore Highlighting Rule");
	}

	@Override
	protected boolean getIgnoreConditions(EObject moslObject, INode node) {
		if(node != null && node.getGrammarElement() instanceof Keyword){
			return !isDefaultOrPunctuation(Keyword.class.cast(node.getGrammarElement()));
		}
		return false;
	}

	private boolean isDefaultOrPunctuation(Keyword keyword){
		String id = AbstractTokenMapper.getIDFromToken(keyword.getValue());
		return id !=null && (id.equals(AbstractHighlightingConfiguration.DEFAULT_ID) || id.equals(AbstractHighlightingConfiguration.PUNCTUATION_ID));
	}


}
