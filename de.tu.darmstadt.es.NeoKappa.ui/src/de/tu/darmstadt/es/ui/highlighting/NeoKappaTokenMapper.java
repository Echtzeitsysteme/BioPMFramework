package de.tu.darmstadt.es.ui.highlighting;

import java.util.List;
import java.util.Arrays;
import com.google.inject.Inject;

import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractTokenMapper;

public class NeoKappaTokenMapper extends AbstractTokenMapper {

	@Inject
	static NeoKappaTokenMapper mapper = new NeoKappaTokenMapper();
	
	private static List<String> kappaDelemeters = Arrays.asList("!");
	
	@Override
	protected String calculateId(String tokenName, int tokenType) {
		String trimmedTokenName = tokenName.replaceAll("'", "");
		String id = super.calculateId(tokenName, tokenType);		
		if(kappaDelemeters.contains(trimmedTokenName)) {
			id = NeoKappaeHighlightingConfiguration.KAPPA_DELEMETERS_ID;
		}		
		if("~".equals(trimmedTokenName)){
			id = NeoKappaeHighlightingConfiguration.INTERNAL_STATE_ID;
		}	
		if("%agent:".equals(trimmedTokenName)){
			id = NeoKappaeHighlightingConfiguration.AGENT_COMMAND_ID;
		}
		
		if("%init:".equals(trimmedTokenName)){
			id = NeoKappaeHighlightingConfiguration.INIT_DEF_ID;
		}
		if("%var:".equals(trimmedTokenName)){
			id = NeoKappaeHighlightingConfiguration.VARIABLE_DEF_ID;
		}
		if("RULE_SHAPE".equals(trimmedTokenName)) {
			id = NeoKappaeHighlightingConfiguration.NUMBER_ID;
		}
		
		mappedTokens.put(trimmedTokenName, id);
		return id;
	}

}
