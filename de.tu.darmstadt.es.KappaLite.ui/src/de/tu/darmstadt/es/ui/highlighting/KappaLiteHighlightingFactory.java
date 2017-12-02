package de.tu.darmstadt.es.ui.highlighting;

import de.tu.darmstadt.es.ui.highlighting.rules.AgentHighlightingRule;
import de.tu.darmstadt.es.ui.highlighting.rules.InternalStateHighlightingRule;
import de.tu.darmstadt.es.ui.highlighting.rules.KappaRuleHighlightingRule;
import de.tu.darmstadt.es.ui.highlighting.rules.SiteHighlightingRule;
import de.tu.darmstadt.es.ui.highlighting.rules.VariableHighlightingRule;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightFactory;

public class KappaLiteHighlightingFactory extends AbstractHighlightFactory {

	@Override
	public void createAllInstances() {
		new AgentHighlightingRule(controller);
		new SiteHighlightingRule(controller);
		new InternalStateHighlightingRule(controller);
		new KappaRuleHighlightingRule(controller);
		new VariableHighlightingRule(controller);
	}

}
