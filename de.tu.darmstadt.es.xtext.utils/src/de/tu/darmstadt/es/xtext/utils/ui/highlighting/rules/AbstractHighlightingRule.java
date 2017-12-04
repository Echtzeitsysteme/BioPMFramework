package de.tu.darmstadt.es.xtext.utils.ui.highlighting.rules;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightProviderController;
import de.tu.darmstadt.es.xtext.utils.ui.highlighting.exceptions.IDAlreadyExistException;

public abstract class AbstractHighlightingRule implements IModularConfiguration{

	protected Logger logger;
	
	protected String id;
	
	protected String description;
	
	protected AbstractHighlightProviderController controller;
	
	private int prio=50;
	
	public AbstractHighlightingRule(String id, String description, AbstractHighlightProviderController controller){
		init(id, description, controller);
	}
	
	public AbstractHighlightingRule(String id, String description, AbstractHighlightProviderController controller, int prio){
		this.prio = prio;
		init(id, description, controller);
	}
	
	private void init(String id, String description, AbstractHighlightProviderController controller){
		logger = Logger.getLogger(this.getClass());
		this.id = id;
		this.description = description;
		this.controller = controller;
		try {
			this.controller.addHighlightRule(this);
		} catch (IDAlreadyExistException e) {
			logger.error("ID already exist", e);
		}
	}
	
	protected void setHighlighting(INode node, IHighlightedPositionAcceptor acceptor){
		acceptor.addPosition(node.getOffset(), node.getLength() , id);
	}
	
	protected abstract TextStyle getTextStyle();
	
	public void setHighlightingConfiguration(IHighlightingConfigurationAcceptor acceptor){
		acceptor.
		acceptDefaultHighlighting(id, description, getTextStyle());
	}
	
	public boolean canProvideHighlighting(EObject moslObject, INode node, IHighlightedPositionAcceptor acceptor){
		if(!moslObject.eIsProxy())
			EcoreUtil.resolveAll(moslObject);
		boolean provide = getHighlightingConditions(moslObject, node);
		if(provide){
			setHighlighting(node, acceptor);
		}
		return provide;
	}
	
	public int getPriority(){
		return prio;
	}
	
	protected abstract boolean getHighlightingConditions(EObject moslObject, INode node);
	
	public String getID(){
		return id;
	}
}
