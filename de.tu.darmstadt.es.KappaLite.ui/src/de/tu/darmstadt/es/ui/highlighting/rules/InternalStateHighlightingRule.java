package de.tu.darmstadt.es.ui.highlighting.rules;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

import de.tu.darmstadt.es.kappaLite.KLIState;
import de.tu.darmstadt.es.kappaLite.KLIStateDescription;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightProviderController;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.rules.AbstractHighlightingRule;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.utils.XtextColor;

public class InternalStateHighlightingRule extends AbstractHighlightingRule {

	public InternalStateHighlightingRule(AbstractHighlightProviderController controller) {
		super("States", "internal states", controller);
	}

	@Override
	protected TextStyle getTextStyle() {
		TextStyle ts = new TextStyle();
		ts.setStyle(SWT.ITALIC);
		ts.setColor(controller.getColorManager().getColor(XtextColor.DARK_BLUE));
		return ts;
	}

	@Override
	protected boolean getHighlightingConditions(EObject moslObject, INode node) {
		KLIState state = null;
		if(moslObject instanceof KLIState)
			state = KLIState.class.cast(moslObject);
		else if(moslObject instanceof KLIStateDescription)
			state = KLIStateDescription.class.cast(moslObject).getState();
		return state != null && node.getText().equals(state.getName());
	}

}
