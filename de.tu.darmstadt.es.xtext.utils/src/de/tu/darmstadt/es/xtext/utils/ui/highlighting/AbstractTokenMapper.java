package de.tu.darmstadt.es.xtext.utils.ui.highlighting;

import java.util.HashMap;
import java.util.Map;


import org.eclipse.xtext.ide.editor.syntaxcoloring.DefaultAntlrTokenToAttributeIdMapper;
import org.eclipse.xtext.util.Arrays;

import com.google.inject.Inject;

public  abstract class AbstractTokenMapper extends DefaultAntlrTokenToAttributeIdMapper {

	public AbstractTokenMapper(){
		super();
		init();
	}
	
	
	protected abstract <TokenMapper extends AbstractTokenMapper> TokenMapper create();
	
	private void init() {
		mappedTokens.clear();		
	}

	private static Map<String, String> mappedTokens = new HashMap<>();
	
	@Inject
	AbstractTokenMapper mapper = this.create();
	
	private static final String[] delemiters = {":","{","}","(",")"};
	
	@Override
	protected String calculateId(String tokenName, int tokenType) {
		String trimmedTokenName = tokenName.replaceAll("'", "");
		String id = super.calculateId(tokenName, tokenType);		

		if(Arrays.contains(delemiters, trimmedTokenName)){
			id = AbstractHighlightingConfiguration.DEFAULT_ID;
		}
	
		mappedTokens.put(trimmedTokenName, id);
		return id;
	}
	
	public static String getIDFromToken(String token){
		String trimmedTokenName = token.replaceAll("'", "");
		return mappedTokens.get(trimmedTokenName);
	}
}
