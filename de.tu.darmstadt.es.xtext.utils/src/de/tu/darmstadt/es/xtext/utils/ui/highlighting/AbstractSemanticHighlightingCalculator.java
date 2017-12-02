package de.tu.darmstadt.es.xtext.utils.ui.highlighting;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ide.editor.syntaxcoloring.DefaultSemanticHighlightingCalculator;
import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;

import de.tu.darmstadt.es.xtext.utils.ui.highlighting.rules.AbstractHighlightingRule;

public abstract class AbstractSemanticHighlightingCalculator extends DefaultSemanticHighlightingCalculator {
   
	private static AbstractHighlightProviderController controller;
	
	static void setController (AbstractHighlightProviderController _controller) {
		controller = _controller;
	}
   
	@Override
	protected void doProvideHighlightingFor(XtextResource resource, IHighlightedPositionAcceptor acceptor,
			CancelIndicator cancelIndicator) {

		if (resource == null || resource.getParseResult() == null)
			return;
		INode rootNode = resource.getParseResult().getRootNode();
		Collection<AbstractHighlightingRule> rules = controller.getHighlightRules();
		for (INode node : rootNode.getLeafNodes()) {
			findHighlightingRuleForNode(node, rules, acceptor);
		}
		super.doProvideHighlightingFor(resource, acceptor, cancelIndicator);

	}
	
	private void findHighlightingRuleForNode(INode node, Collection<AbstractHighlightingRule> rules, IHighlightedPositionAcceptor acceptor){
		EObject moslObject = NodeModelUtils.findActualSemanticObjectFor(node);
		for (AbstractHighlightingRule rule : rules){
			if(rule.canProvideHighlighting(moslObject, node, acceptor))
				return;
		}
	}
}
