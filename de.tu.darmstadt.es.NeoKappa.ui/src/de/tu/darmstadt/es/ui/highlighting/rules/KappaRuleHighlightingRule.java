package de.tu.darmstadt.es.ui.highlighting.rules;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

import de.tu.darmstadt.es.neoKappa.NKARule;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightProviderController;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.rules.AbstractHighlightingRule;

public class KappaRuleHighlightingRule extends AbstractHighlightingRule {

	public KappaRuleHighlightingRule(AbstractHighlightProviderController controller) {
		super("KappaRule", "Kappa Rule", controller);
	}

	@Override
	protected TextStyle getTextStyle() {
		TextStyle ts = new TextStyle();
		ts.setStyle(SWT.BOLD);
		return ts;
	}

	@Override
	protected boolean getHighlightingConditions(EObject moslObject, INode node) {
		if(moslObject instanceof NKARule) {
			String text = node.getText();
			return NKARule.class.cast(moslObject).getName().equals(text) || "'".equals(text);
		}
		return false;
	}

}
