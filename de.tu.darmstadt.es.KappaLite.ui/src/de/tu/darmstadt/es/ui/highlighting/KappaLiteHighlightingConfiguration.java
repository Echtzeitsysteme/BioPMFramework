package de.tu.darmstadt.es.ui.highlighting;

import org.eclipse.xtext.ui.editor.utils.TextStyle;

import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightingConfiguration;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.utils.XtextColor;

public class KappaLiteHighlightingConfiguration extends AbstractHighlightingConfiguration {


	@Override
	   public TextStyle commentTextStyle(){
		   TextStyle ts = super.commentTextStyle();
		   ts.setColor(colorManager.getColor(XtextColor.GRAY));
		   return ts;
	   }
}
