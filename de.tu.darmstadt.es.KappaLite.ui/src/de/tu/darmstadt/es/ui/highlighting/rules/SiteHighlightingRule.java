package de.tu.darmstadt.es.ui.highlighting.rules;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

import de.tu.darmstadt.es.kappaLite.KLISite;
import de.tu.darmstadt.es.kappaLite.KLISiteDescription;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightProviderController;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.rules.AbstractHighlightingRule;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.utils.XtextColor;

public class SiteHighlightingRule extends AbstractHighlightingRule {

	public SiteHighlightingRule(AbstractHighlightProviderController controller) {
		super("Site", "Site", controller);
	}

	@Override
	protected TextStyle getTextStyle() {
		TextStyle ts = new TextStyle();
		ts.setStyle(SWT.BOLD);
		ts.setColor(controller.getColorManager().getColor(XtextColor.DARK_GREEN));
		return ts;
	}

	@Override
	protected boolean getHighlightingConditions(EObject moslObject, INode node) {
		KLISite site = null;
		if(moslObject instanceof KLISite)
			site = KLISite.class.cast(moslObject);
		else if (moslObject instanceof KLISiteDescription)
			site = KLISiteDescription.class.cast(moslObject).getSite();	
		String txt = node.getText();
		
		return site != null && txt.equals(site.getName());
	}

}
