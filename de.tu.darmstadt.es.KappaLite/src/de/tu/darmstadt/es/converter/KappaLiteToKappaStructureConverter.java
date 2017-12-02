package de.tu.darmstadt.es.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.tu.darmstadt.es.KappaStructure.IConverter;
import de.tu.darmstadt.es.kappaLite.KLIAgent;
import de.tu.darmstadt.es.kappaLite.KLIAgentDescription;
import de.tu.darmstadt.es.kappaLite.KLIConnection;
import de.tu.darmstadt.es.kappaLite.KLIFile;
import de.tu.darmstadt.es.kappaLite.KLIRule;
import de.tu.darmstadt.es.kappaLite.KLISite;
import de.tu.darmstadt.es.kappaLite.KLISiteDescription;
import de.tu.darmstadt.es.kappaLite.KLIState;
import de.tu.darmstadt.es.kappaLite.KLIStateDescription;
import de.tu.darmstadt.es.utils.KappaLiteUtil;
import kappaStructure.Agent;
import kappaStructure.InternalState;
import kappaStructure.KappaContainer;
import kappaStructure.KappaStructureFactory;
import kappaStructure.Site;

public class KappaLiteToKappaStructureConverter implements IConverter<KLIFile>{

	@Override
	public KappaContainer convert(KLIFile file) {
		KappaContainer kappaContainer = KappaStructureFactory.eINSTANCE.createKappaContainer();
		List<KLIRule> rules = KappaLiteUtil.getInstance().unsortedMapToSubType(file.getElements(), KLIRule.class);
		List<List<KLIAgentDescription>> internalAgentDeses = rules.parallelStream().filter(r -> r.getLhs() != null).map(rule -> rule.getLhs().getAgents()).collect(Collectors.toList());
		internalAgentDeses.addAll(rules.parallelStream().filter(r -> r.getRhs() != null).map(rule -> rule.getRhs().getAgents()).collect(Collectors.toList()));
		List<List<Agent>> internalAgents = internalAgentDeses.stream().map(agents -> agents.stream().map(this::convertToAgent).collect(Collectors.toList())).collect(Collectors.toList());
		for(int index = 0; index < internalAgentDeses.size(); ++index) {
			setConnections(internalAgentDeses.get(index) , internalAgents.get(index));
		}
		kappaContainer.getAgents().addAll(internalAgents.parallelStream().flatMap(agents -> agents.stream()).collect(Collectors.toList()));
		return kappaContainer;
	}
	
	private void setConnections(List<KLIAgentDescription> agentDeses, List<Agent> agents) {
		List<KLIAgentDescription> tmp = new ArrayList<>(agentDeses);
		for(int index = 0; index < agents.size(); ++index) {
			KLIAgentDescription agentDes = tmp.remove(0);
			Agent agent = agents.get(index);
			for(int siteIndex = 0; siteIndex < agent.getSites().size(); ++siteIndex) {
				KLISiteDescription siteDes = agentDes.getSites().get(siteIndex);
				Site site = agent.getSites().get(siteIndex);
				KLIConnection connection = siteDes.getConnection();
				if(connection != null) {
					List<KLIAgentDescription> others = tmp.stream().filter(kliAgentDes -> kliAgentDes.getSites().stream().filter(s -> s.getConnection() != null).anyMatch(kliSite -> kliSite.getConnection().getConnectionNumber() == connection.getConnectionNumber())).collect(Collectors.toList());
					for(KLIAgentDescription kliAgentDes : others) {
						int agentPos = agentDeses.indexOf(kliAgentDes);
						Agent otherAgent = agents.get(agentPos);
						KLISiteDescription otherKLISiteDes = kliAgentDes.getSites().stream().filter(kliSiteDes -> kliSiteDes.getConnection().getConnectionNumber() == connection.getConnectionNumber()).findFirst().get();
						int sitePos = kliAgentDes.getSites().indexOf(otherKLISiteDes);
						Site otherSite = otherAgent.getSites().get(sitePos);
						site.getConnectedWith().add(otherSite);
					}
				}
			}
		}
	}
	
	private Agent convertToAgent(KLIAgentDescription kliAgentDes) {
		Agent agent = KappaStructureFactory.eINSTANCE.createAgent();
		KLIAgent kliAgent = kliAgentDes.getAgent();
		agent.setName(kliAgent.getName());
		agent.getSites().addAll(kliAgentDes.getSites().stream().map(this::convertToSite).collect(Collectors.toList()));
		return agent;
	}
	
	private Site convertToSite(KLISiteDescription siteDes) {
		Site site = KappaStructureFactory.eINSTANCE.createSite();
		KLISite kliSite = siteDes.getSite();
		site.setName(kliSite.getName());
		site.getStates().addAll(siteDes.getInternalStates().stream().map(this::convertToInternalState).collect(Collectors.toList()));
		return site;
	}
	
	private InternalState convertToInternalState(KLIStateDescription kliStateDes) {
		InternalState iState = KappaStructureFactory.eINSTANCE.createInternalState();
		KLIState kliState = kliStateDes.getState();
		iState.setName(kliState.getName());
		return iState;
	}
	
}
