package de.tu.darmstadt.es.ui.highlighting;

import java.util.List;
import java.util.Arrays;
import com.google.inject.Inject;

import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractTokenMapper;

public class KappaLiteTokenMapper extends AbstractTokenMapper {

	@Inject
	static KappaLiteTokenMapper mapper = new KappaLiteTokenMapper();
	
	private static List<String> kappaDelemeters = Arrays.asList("!");
	
	@Override
	protected String calculateId(String tokenName, int tokenType) {
		String trimmedTokenName = tokenName.replaceAll("'", "");
		String id = super.calculateId(tokenName, tokenType);		
		if(kappaDelemeters.contains(trimmedTokenName)) {
			id = KappaLiteHighlightingConfiguration.KAPPA_DELEMETERS_ID;
		}		
		if("~".equals(trimmedTokenName)){
			id = KappaLiteHighlightingConfiguration.INTERNAL_STATE_ID;
		}	
		if("%agent:".equals(trimmedTokenName)){
			id = KappaLiteHighlightingConfiguration.AGENT_COMMAND_ID;
		}
		
		if("%init:".equals(trimmedTokenName)){
			id = KappaLiteHighlightingConfiguration.INIT_DEF_ID;
		}
		
		mappedTokens.put(trimmedTokenName, id);
		return id;
	}

}
