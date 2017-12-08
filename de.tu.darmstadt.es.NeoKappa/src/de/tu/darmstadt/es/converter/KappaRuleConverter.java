package de.tu.darmstadt.es.converter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.tu.darmstadt.es.KappaRules.BackwardRule;
import de.tu.darmstadt.es.KappaRules.Edge;
import de.tu.darmstadt.es.KappaRules.ForwardRule;
import de.tu.darmstadt.es.KappaRules.Graph;
import de.tu.darmstadt.es.KappaRules.KappaRule;
import de.tu.darmstadt.es.KappaRules.KappaRuleContainer;
import de.tu.darmstadt.es.KappaRules.KappaRulesFactory;
import de.tu.darmstadt.es.KappaRules.Node;
import de.tu.darmstadt.es.KappaRules.Source;
import de.tu.darmstadt.es.KappaRules.Target;
import de.tu.darmstadt.es.KappaRules.validation.KappaRuleValidator;
import de.tu.darmstadt.es.kappaStructure.Agent;
import de.tu.darmstadt.es.kappaStructure.InternalState;
import de.tu.darmstadt.es.kappaStructure.KappaElement;
import de.tu.darmstadt.es.kappaStructure.Site;
import de.tu.darmstadt.es.neoKappa.NKADescriptionContainer;
import de.tu.darmstadt.es.neoKappa.NKAFile;
import de.tu.darmstadt.es.neoKappa.NKARule;
import de.tu.darmstadt.es.utils.NeoKappaUtil;


public class KappaRuleConverter {

	private KappaStructureConverter kappaStructureConverter = new KappaStructureConverter();
	private NeoKappaExpressionSolver kappaExpressionSolver = new NeoKappaExpressionSolver();
	private KappaRuleValidator kappaRuleValidator = new KappaRuleValidator();

	
	public KappaRuleContainer convert(NKAFile file) {
		KappaRuleContainer kappaRuleContainer = KappaRulesFactory.eINSTANCE.createKappaRuleContainer();		
		List<NKARule> nkaRules = NeoKappaUtil.getInstance().unsortedMapToSubType(file.getElements(), NKARule.class);
		List<List<KappaRule>> kappaRuleLists = nkaRules.parallelStream().map(this::convertRule).collect(Collectors.toList());
		List<KappaRule> kappaRules = kappaRuleLists.parallelStream().flatMap(rules -> rules.stream()).collect(Collectors.toList());
		kappaRuleContainer.getRules().addAll(kappaRules);
		kappaRuleValidator.validateModification(kappaRuleContainer);
		return kappaRuleContainer;
	}
	
	private List<KappaRule> convertRule(NKARule nkaRule) {
		ForwardRule fwd = KappaRulesFactory.eINSTANCE.createForwardRule();
		fillRule(fwd, nkaRule.getLhs(), nkaRule.getRhs());
		fwd.setName(nkaRule.getName());
		if(nkaRule.getRrating() != null) {
			fwd.setRating(kappaExpressionSolver.solveExpression(nkaRule.getRrating()));
		}
		
		
		if(nkaRule.isBiDirectional()) {
			BackwardRule bwd = KappaRulesFactory.eINSTANCE.createBackwardRule();
			bwd.setName(nkaRule.getName());
			fillRule(bwd, nkaRule.getRhs(), nkaRule.getLhs());			
			fwd.setOpposite(bwd);
			if(nkaRule.getLrating() != null) {
				bwd.setRating(kappaExpressionSolver.solveExpression(nkaRule.getLrating()));
			}			
			return Arrays.asList(fwd, bwd);
		}
		else {
			return Arrays.asList(fwd);
		}
	}
	
	private void fillRule(KappaRule rule, NKADescriptionContainer nkaFrom, NKADescriptionContainer nkaTo) {
		if(nkaFrom != null) { //nkaFrom == null are seeds
			Source src = KappaRulesFactory.eINSTANCE.createSource();
			rule.setSource(src);
			src.setKappaModel(kappaStructureConverter.convertToKappaContainer(nkaFrom));
			fillGraph(src, src.getKappaModel().getAgents());		
		}
		
		if(nkaTo != null) {
			Target trg = KappaRulesFactory.eINSTANCE.createTarget();
			rule.setTarget(trg);
			trg.setKappaModel(kappaStructureConverter.convertToKappaContainer(nkaTo));
			
			fillGraph(trg, trg.getKappaModel().getAgents());	
		}		
	}
	
	private void fillGraph(Graph graph,  List<Agent> agents) {
		agents.forEach(agent -> addAgentToGraph(graph, agent));
		final List<Node> siteNodes = graph.getNodes().parallelStream().filter(node -> node.getElement() instanceof Site).collect(Collectors.toList());
		List<List<Edge>> edges =siteNodes.parallelStream().map(node -> createConnections(node, new HashSet<Node>(siteNodes))).collect(Collectors.toList());
		graph.getEdges().addAll(edges.parallelStream().flatMap(edgeList -> edgeList.stream()).collect(Collectors.toList()));
	}
	
	private List<Edge> createConnections(Node siteNode, Set<Node> allNodes) {
		return Site.class.cast(siteNode.getElement()).getConnectedWith().stream().map(otherSite -> createEdge(siteNode, findNode(otherSite, allNodes))).collect(Collectors.toList());
	}
	
	private Node findNode(Site site, final Set<Node> allNodes) {
		return allNodes.parallelStream().filter(node -> node.getElement().equals(site)).findFirst().get();
	}	
	
	private void addAgentToGraph(Graph graph, Agent agent) {
		Node node = createNode(graph, agent);
		node.setIndexOfElement(agent.getContained().getAgents().indexOf(agent));
		List<Node> nodesFromSites = agent.getSites().stream().map(site -> convertSiteToNode(graph, site)).collect(Collectors.toList());
		graph.getEdges().addAll(nodesFromSites.stream().map(other -> createEdge(node, other)).collect(Collectors.toList()));
	}

	
	private Node convertSiteToNode(Graph graph, Site site) {
		Node node = createNode(graph, site);
		node.setIndexOfElement(site.getAgent().getSites().indexOf(site));
		List<Node> nodesFromSites = site.getStates().stream().map(state -> convertStateToNode(graph, state)).collect(Collectors.toList());
		graph.getEdges().addAll(nodesFromSites.stream().map(other -> createEdge(node, other)).collect(Collectors.toList()));
		return node;
	}
	
	private Node convertStateToNode(Graph graph, InternalState state) {
		Node node = createNode(graph, state);
		node.setIndexOfElement(state.getSite().getStates().indexOf(state));
		return node;
	}
	
	
	private Node createNode(Graph graph, KappaElement element) {
		Node node = KappaRulesFactory.eINSTANCE.createNode();
		node.setGraph(graph);
		node.setElement(element);
		return node;
	}
	
	private Edge createEdge(Node from, Node to) {
		Edge edge = KappaRulesFactory.eINSTANCE.createEdge();
		edge.setFrom(from);
		edge.setTo(to);
		return edge;
	}
}
