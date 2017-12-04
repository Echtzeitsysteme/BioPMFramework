package de.tu.darmstadt.es.ui.highlighting.rules;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

import de.tu.darmstadt.es.neoKappa.NKAGeoLocation;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightProviderController;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.rules.AbstractHighlightingRule;

public class GEOLocationHighlightingRule extends AbstractHighlightingRule {

	public GEOLocationHighlightingRule(AbstractHighlightProviderController controller) {
		super("geoLocation", "GEO Location", controller);
	}

	@Override
	protected TextStyle getTextStyle() {
		TextStyle ts = new TextStyle();
		ts.setStyle(SWT.ITALIC);
		return ts;
	}

	@Override
	protected boolean getHighlightingConditions(EObject moslObject, INode node) {
		if(moslObject instanceof NKAGeoLocation) {
			return node.getText().equals(NKAGeoLocation.class.cast(moslObject).getName());
		}
		return false;
	}

}
