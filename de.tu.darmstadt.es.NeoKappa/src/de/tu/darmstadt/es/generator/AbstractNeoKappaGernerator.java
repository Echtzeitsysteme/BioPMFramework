package de.tu.darmstadt.es.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.eclipse.xtext.generator.AbstractGenerator;

import de.tu.darmstadt.es.KappaStructure.IModelConverter;
import de.tu.darmstadt.es.neoKappa.NKAAgent;
import de.tu.darmstadt.es.neoKappa.NKAAgentDescription;
import de.tu.darmstadt.es.neoKappa.NKAConnection;
import de.tu.darmstadt.es.neoKappa.NKADecimalLiteralExpression;
import de.tu.darmstadt.es.neoKappa.NKAExpression;
import de.tu.darmstadt.es.neoKappa.NKAFile;
import de.tu.darmstadt.es.neoKappa.NKAIntegerLiteralExpression;
import de.tu.darmstadt.es.neoKappa.NKAMathExpression;
import de.tu.darmstadt.es.neoKappa.NKARule;
import de.tu.darmstadt.es.neoKappa.NKASite;
import de.tu.darmstadt.es.neoKappa.NKASiteDescription;
import de.tu.darmstadt.es.neoKappa.NKAState;
import de.tu.darmstadt.es.neoKappa.NKAStateDescription;
import de.tu.darmstadt.es.neoKappa.NKAVariableExpression;
import de.tu.darmstadt.es.utils.NeoKappaUtil;
import kappaStructure.Agent;
import kappaStructure.InternalState;
import kappaStructure.KappaContainer;
import kappaStructure.KappaStructureFactory;
import kappaStructure.Site;

public abstract class AbstractNeoKappaGernerator extends AbstractGenerator implements IModelConverter<NKAFile>{

	private Map<String, BiFunction<Double, Double, Double>> operations;
	
	public AbstractNeoKappaGernerator()
	{
		super();
		operations = new HashMap<>();
		
		operations.put("+", (a,b) -> a + b);
		operations.put("-", (a,b) -> a - b);
		operations.put("*", (a,b) -> a * b);
		operations.put("/", (a,b) -> a / b);
	}
	
	protected double solveExpression(NKAExpression expression) {
		Stack<NKAExpression> stack = new Stack<>();
		stack.push(expression);
		double tmp=0.0;
		while(stack.size()>0) {
			NKAExpression expr = stack.pop();
			if(expr instanceof NKAIntegerLiteralExpression) {
				tmp = NKAIntegerLiteralExpression.class.cast(expr).getValue();
			}else if (expr instanceof NKADecimalLiteralExpression) {
				tmp = NKADecimalLiteralExpression.class.cast(expr).getValue();
			}else if(expr instanceof NKAVariableExpression) {
				stack.push(NKAVariableExpression.class.cast(expr).getValue().getExpr());
			}else if(expr instanceof NKAMathExpression) {
				NKAMathExpression math = NKAMathExpression.class.cast(expr);
				tmp=operations.get(math.getOperator()).apply(solveExpression(math.getLExpr()), solveExpression(math.getRExpr()));
			}			
		}		
		return tmp;
	}
	
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
	
	private void setConnections(List<NKAAgentDescription> agentDeses, List<Agent> agents) {
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
	
	private Agent convertToAgent(NKAAgentDescription kliAgentDes) {
		Agent agent = KappaStructureFactory.eINSTANCE.createAgent();
		NKAAgent kliAgent = kliAgentDes.getAgent();
		agent.setName(kliAgent.getName());
		agent.getSites().addAll(kliAgentDes.getSites().stream().map(this::convertToSite).collect(Collectors.toList()));
		return agent;
	}
	
	private Site convertToSite(NKASiteDescription siteDes) {
		Site site = KappaStructureFactory.eINSTANCE.createSite();
		NKASite kliSite = siteDes.getSite();
		site.setName(kliSite.getName());
		site.getStates().addAll(siteDes.getInternalStates().stream().map(this::convertToInternalState).collect(Collectors.toList()));
		return site;
	}
	
	private InternalState convertToInternalState(NKAStateDescription kliStateDes) {
		InternalState iState = KappaStructureFactory.eINSTANCE.createInternalState();
		NKAState kliState = kliStateDes.getState();
		iState.setName(kliState.getName());
		return iState;
	}
		
		
		
}
