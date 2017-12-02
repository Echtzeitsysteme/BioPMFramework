package de.tu.darmstadt.es.ui.highlighting;

import org.eclipse.swt.SWT;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightingConfiguration;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.utils.XtextColor;

public class KappaLiteHighlightingConfiguration extends AbstractHighlightingConfiguration {

	public static String INTERNAL_STATE_ID = "internalState";
	public static String AGENT_COMMAND_ID = "agent";
	public static String KAPPA_DELEMETERS_ID = "kappaDelemeters";
	public static String INIT_DEF_ID = "INIT_DEF";
	
	@Override
	public void configure(IHighlightingConfigurationAcceptor acceptor) {
		super.configure(acceptor);
		acceptor.acceptDefaultHighlighting(INTERNAL_STATE_ID, "Internal state character", internalStateTextStyle());
		acceptor.acceptDefaultHighlighting(AGENT_COMMAND_ID, "Agent Command", agendCommandTextStyle());
		acceptor.acceptDefaultHighlighting(KAPPA_DELEMETERS_ID, "Kappa delemeters", kappaDelemeterTextStyle());
		acceptor.acceptDefaultHighlighting(INIT_DEF_ID, "Init Definition", initTextStyle());
	}
	
	@Override
	public TextStyle commentTextStyle() {
		TextStyle ts = super.commentTextStyle();
		ts.setStyle(SWT.BOLD);
		ts.setColor(colorManager.getColor(XtextColor.GRAY));
		return ts;
	}
	
	@Override
	public TextStyle numberTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(colorManager.getColor(XtextColor.LIGHT_BLUE));
		return textStyle;
	} 
	
	public TextStyle internalStateTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(colorManager.getColor(XtextColor.DARK_BLUE));
		return textStyle;
	}
	
	public TextStyle kappaDelemeterTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(colorManager.getColor(XtextColor.RED));
		return textStyle;
	}
	
	public TextStyle initTextStyle() {
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(colorManager.getColor(XtextColor.DARK_RED));
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
	}
	
	public TextStyle keywordTextStyle() {
		TextStyle ts = super.keywordTextStyle();
		ts.setStyle(SWT.ITALIC);
		return ts;
	}
	
	public TextStyle agendCommandTextStyle() {
		TextStyle textStyle = keywordTextStyle().copy();
		textStyle.setStyle(SWT.ITALIC);
		textStyle.setColor(colorManager.getColor(XtextColor.DARK_RED));
		return textStyle;
	} 
	
}
