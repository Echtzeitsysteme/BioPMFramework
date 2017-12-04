package de.tu.darmstadt.es.ui.highlighting.rules;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

import de.tu.darmstadt.es.neoKappa.NKAVariable;
import de.tu.darmstadt.es.neoKappa.NKAVariableExpression;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightProviderController;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.rules.AbstractHighlightingRule;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.utils.XtextColor;

public class VariableHighlightingRule extends AbstractHighlightingRule {

	public VariableHighlightingRule(AbstractHighlightProviderController controller) {
		super("VariableDefinition", "Variable", controller);
	}

	@Override
	protected TextStyle getTextStyle() {
		TextStyle ts = new TextStyle();
		ts.setColor(controller.getColorManager().getColor(XtextColor.BROWN));
		return ts;
	}

	@Override
	protected boolean getHighlightingConditions(EObject moslObject, INode node) {
		NKAVariable variable = null;
		if(moslObject instanceof NKAVariable)
			variable = NKAVariable.class.cast(moslObject);
		else if(moslObject instanceof NKAVariableExpression)
			variable = NKAVariableExpression.class.cast(moslObject).getValue();
		String text = node.getText();
		return variable != null && (text.equals(variable.getName()) || "'".equals(text));
	}

}
