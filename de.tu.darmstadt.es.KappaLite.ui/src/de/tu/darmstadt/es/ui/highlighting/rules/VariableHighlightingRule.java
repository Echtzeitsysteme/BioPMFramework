package de.tu.darmstadt.es.ui.highlighting.rules;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

import de.tu.darmstadt.es.kappaLite.KLIVariable;
import de.tu.darmstadt.es.kappaLite.KLIVariableExpression;
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
		KLIVariable variable = null;
		if(moslObject instanceof KLIVariable)
			variable = KLIVariable.class.cast(moslObject);
		else if(moslObject instanceof KLIVariableExpression)
			variable = KLIVariableExpression.class.cast(moslObject).getValue();
		String text = node.getText();
		return variable != null && variable.getName().equals(text) || "'".equals(text);
	}

}
