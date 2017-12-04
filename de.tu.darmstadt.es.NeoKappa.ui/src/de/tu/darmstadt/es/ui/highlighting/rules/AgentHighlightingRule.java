package de.tu.darmstadt.es.ui.highlighting.rules;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

import de.tu.darmstadt.es.neoKappa.NKAAgent;
import de.tu.darmstadt.es.neoKappa.NKAAgentDescription;
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
		ts.setColor(controller.getColorManager().getColor(XtextColor.BLUE));
		return ts;
	}

	@Override
	protected boolean getHighlightingConditions(EObject moslObject, INode node) {
		NKAAgent agent = null;
		if(moslObject instanceof NKAAgent)
			agent = NKAAgent.class.cast(moslObject);
		else if(moslObject instanceof NKAAgentDescription)
			agent = NKAAgentDescription.class.cast(moslObject).getAgent();
		
		if(agent != null && node.getText().equals(agent.getName())) {			
			return true;
		}
			
		return false;
	}

}
