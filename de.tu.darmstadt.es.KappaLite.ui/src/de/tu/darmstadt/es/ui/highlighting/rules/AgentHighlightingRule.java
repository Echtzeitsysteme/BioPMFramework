package de.tu.darmstadt.es.ui.highlighting.rules;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

import de.tu.darmstadt.es.kappaLite.KLIAgent;
import de.tu.darmstadt.es.kappaLite.KLIAgentDescription;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightProviderController;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.rules.AbstractHighlightingRule;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.utils.XtextColor;

public class AgentHighlightingRule extends AbstractHighlightingRule {



	public AgentHighlightingRule(AbstractHighlightProviderController controller) {
		super("Agent", "Agent", controller);
	}

	@Override
	protected TextStyle getTextStyle() {
		TextStyle ts = new TextStyle();
		ts.setStyle(SWT.BOLD);
		ts.setColor(controller.getColorManager().getColor(XtextColor.VIOLETT));
		return ts;
	}

	@Override
	protected boolean getHighlightingConditions(EObject moslObject, INode node) {
		KLIAgent agent = null;
		if(moslObject instanceof KLIAgent)
			agent = KLIAgent.class.cast(moslObject);
		else if(moslObject instanceof KLIAgentDescription)
			agent = KLIAgentDescription.class.cast(moslObject).getAgent();
		
		if(agent != null && node.getText().equals(agent.getName())) {			
			return true;
		}
			
		return false;
	}

}
