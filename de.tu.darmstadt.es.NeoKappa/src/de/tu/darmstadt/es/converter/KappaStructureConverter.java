package de.tu.darmstadt.es.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.tu.darmstadt.es.KappaStructure.IModelConverter;
import de.tu.darmstadt.es.neoKappa.NKAAgent;
import de.tu.darmstadt.es.neoKappa.NKAAgentDescription;
import de.tu.darmstadt.es.neoKappa.NKAConnection;
import de.tu.darmstadt.es.neoKappa.NKADescriptionContainer;
import de.tu.darmstadt.es.neoKappa.NKAFile;
import de.tu.darmstadt.es.neoKappa.NKARule;
import de.tu.darmstadt.es.neoKappa.NKASite;
import de.tu.darmstadt.es.neoKappa.NKASiteDescription;
import de.tu.darmstadt.es.neoKappa.NKAState;
import de.tu.darmstadt.es.neoKappa.NKAStateDescription;
import de.tu.darmstadt.es.utils.NeoKappaUtil;
import de.tu.darmstadt.es.kappaStructure.Agent;
import de.tu.darmstadt.es.kappaStructure.InternalState;
import de.tu.darmstadt.es.kappaStructure.KappaContainer;
import de.tu.darmstadt.es.kappaStructure.KappaStructureFactory;
import de.tu.darmstadt.es.kappaStructure.Site;

public class KappaStructureConverter implements IModelConverter<NKAFile>{

	@Override
	public KappaContainer convert(NKAFile file) {
		KappaContainer kappaContainer = KappaStructureFactory.eINSTANCE.createKappaContainer();
		List<NKARule> rules = NeoKappaUtil.getInstance().unsortedMapToSubType(file.getElements(), NKARule.class);
		List<List<NKAAgentDescription>> internalAgentDeses = rules.parallelStream().filter(r -> r.getLhs() != null).map(rule -> rule.getLhs().getAgents()).collect(Collectors.toList());
		internalAgentDeses.addAll(rules.parallelStream().filter(r -> r.getRhs() != null).map(rule -> rule.getRhs().getAgents()).collect(Collectors.toList()));
		List<List<Agent>> internalAgents = internalAgentDeses.stream().map(agents -> agents.stream().map(this::convertToAgent).collect(Collectors.toList())).collect(Collectors.toList());
		for(int index = 0; index < internalAgentDeses.size(); ++index) {
			setConnections(internalAgentDeses.get(index) , internalAgents.get(index));
		}
		kappaContainer.getAgents().addAll(internalAgents.parallelStream().flatMap(agents -> agents.stream()).collect(Collectors.toList()));
		return kappaContainer;
	}
	
	public KappaContainer convertToKappaContainer(NKADescriptionContainer desContainer){
		KappaContainer kappaContainer = KappaStructureFactory.eINSTANCE.createKappaContainer();
		List<NKAAgentDescription> agentDeses = desContainer.getAgents();
		List<Agent> agents = agentDeses.stream().map(this::convertToAgent).collect(Collectors.toList());
		setConnections(agentDeses, agents);
		kappaContainer.getAgents().addAll(agents);
		return kappaContainer;
	}
	
	
	public void setConnections(List<NKAAgentDescription> agentDeses, List<Agent> agents) {
		List<NKAAgentDescription> tmp = new ArrayList<>(agentDeses);
		for(int index = 0; index < agents.size(); ++index) {
			NKAAgentDescription agentDes = tmp.remove(0);
			Agent agent = agents.get(index);
			for(int siteIndex = 0; siteIndex < agent.getSites().size(); ++siteIndex) {
				NKASiteDescription siteDes = agentDes.getSites().get(siteIndex);
				Site site = agent.getSites().get(siteIndex);
				NKAConnection connection = siteDes.getConnection();
				if(connection != null) {
					List<NKAAgentDescription> others = tmp.stream().filter(kliAgentDes -> kliAgentDes.getSites().stream().filter(s -> s.getConnection() != null).anyMatch(kliSite -> kliSite.getConnection().getConnectionNumber() == connection.getConnectionNumber())).collect(Collectors.toList());
					for(NKAAgentDescription kliAgentDes : others) {
						int agentPos = agentDeses.indexOf(kliAgentDes);
						Agent otherAgent = agents.get(agentPos);
						NKASiteDescription otherNKASiteDes = kliAgentDes.getSites().stream().filter(kliSiteDes -> kliSiteDes.getConnection().getConnectionNumber() == connection.getConnectionNumber()).findFirst().get();
						int sitePos = kliAgentDes.getSites().indexOf(otherNKASiteDes);
						Site otherSite = otherAgent.getSites().get(sitePos);
						site.getConnectedWith().add(otherSite);
					}
				}
			}
		}
	}
	
	public Agent convertToAgent(NKAAgentDescription kliAgentDes) {
		Agent agent = KappaStructureFactory.eINSTANCE.createAgent();
		NKAAgent kliAgent = kliAgentDes.getAgent();
		agent.setName(kliAgent.getName());
		agent.getSites().addAll(kliAgentDes.getSites().stream().map(this::convertToSite).collect(Collectors.toList()));
		return agent;
	}
	
	public Site convertToSite(NKASiteDescription siteDes) {
		Site site = KappaStructureFactory.eINSTANCE.createSite();
		NKASite kliSite = siteDes.getSite();
		site.setName(kliSite.getName());
		site.getStates().addAll(siteDes.getInternalStates().stream().map(this::convertToInternalState).collect(Collectors.toList()));
		return site;
	}
	
	public InternalState convertToInternalState(NKAStateDescription kliStateDes) {
		InternalState iState = KappaStructureFactory.eINSTANCE.createInternalState();
		NKAState kliState = kliStateDes.getState();
		iState.setName(kliState.getName());
		return iState;
	}
	
}
