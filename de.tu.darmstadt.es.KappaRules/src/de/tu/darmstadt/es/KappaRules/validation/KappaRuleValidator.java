package de.tu.darmstadt.es.KappaRules.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.tu.darmstadt.es.KappaRules.BackwardRule;
import de.tu.darmstadt.es.KappaRules.Edge;
import de.tu.darmstadt.es.KappaRules.ForwardRule;
import de.tu.darmstadt.es.KappaRules.Graph;
import de.tu.darmstadt.es.KappaRules.KappaRule;
import de.tu.darmstadt.es.KappaRules.KappaRuleContainer;
import de.tu.darmstadt.es.KappaRules.KappaRulesFactory;
import de.tu.darmstadt.es.KappaRules.Mapping;
import de.tu.darmstadt.es.KappaRules.ModifiableElement;
import de.tu.darmstadt.es.KappaRules.Modifier;
import de.tu.darmstadt.es.KappaRules.Node;
import de.tu.darmstadt.es.KappaRules.NodeComparator;
import de.tu.darmstadt.es.KappaRules.Source;
import de.tu.darmstadt.es.KappaRules.Target;
import de.tu.darmstadt.es.KappaRules.exceptions.KappaRuleValidationException;
import de.tu.darmstadt.es.kappaStructure.Agent;
import de.tu.darmstadt.es.kappaStructure.InternalState;
import de.tu.darmstadt.es.kappaStructure.Site;

public class KappaRuleValidator {
	public void validateModification(KappaRuleContainer kappaRuleContainer) throws KappaRuleValidationException{
		kappaRuleContainer.getRules().parallelStream().forEach(this::validateRule);
	}
	
	private void validateRule(KappaRule kappaRule) throws KappaRuleValidationException{
		if(kappaRule instanceof BackwardRule) {
			ForwardRule fwd = BackwardRule.class.cast(kappaRule).getOpposite();
			if(fwd == null || fwd.getOpposite() == null || !fwd.getOpposite().equals(kappaRule))
				throw new KappaRuleValidationException("BackwardRule is not set correctly");
		}
		
		Source source = kappaRule.getSource();
		Target target = kappaRule.getTarget();
		
		List<Node> srcAgentNodes = getAgentNodes(source);
		List<Node> trgAgentNodes = getAgentNodes(target);
		
		int maxSize = Math.max(srcAgentNodes.size(), trgAgentNodes.size());
		
		for(int index = 0; index < maxSize; ++ index) {
			if(index < srcAgentNodes.size() && index < trgAgentNodes.size()) {
				if(sameNames(srcAgentNodes.get(index), trgAgentNodes.get(index))) {
					setPersistent(srcAgentNodes.get(index), trgAgentNodes.get(index), kappaRule);
					validateSitesOfPersistent(srcAgentNodes.get(index).getOutgoingEdges(), trgAgentNodes.get(index).getOutgoingEdges(), kappaRule);
				}
				else {
					setModifier(srcAgentNodes.get(index), Modifier.DELETE);
					setModifier(trgAgentNodes.get(index), Modifier.CREATE);
				}
			}
			else if(index < srcAgentNodes.size() && index >= trgAgentNodes.size()) 
				setModifier(srcAgentNodes.get(index), Modifier.DELETE);			
			else
				setModifier(trgAgentNodes.get(index), Modifier.CREATE);
				
		}
		
	}
	
	private void setModifier(List<Edge> edges, Modifier modifier) {
		List<Edge> restEdges = edges.parallelStream().filter(edge -> !edge.getTo().getModifier().equals(modifier)).collect(Collectors.toList());
		restEdges.parallelStream().forEach(edge->edge.setModifier(modifier));
		
		List<Edge> siteToSite = restEdges.parallelStream().filter(edge -> nodeHasTypeOf(edge.getFrom(), Site.class) && nodeHasTypeOf(edge.getTo(), Site.class)).collect(Collectors.toList());
		List<Edge> otherConnections = restEdges.parallelStream().filter(edge -> !(nodeHasTypeOf(edge.getFrom(), Site.class) && nodeHasTypeOf(edge.getTo(), Site.class))).collect(Collectors.toList());
		
		siteToSite.forEach(edge -> setCreateForSiteToSite(edge, modifier));
		otherConnections.parallelStream().forEach(edge-> setModifier(edge.getTo(), modifier));
		
	}
	
	private void setModifier(Node node, Modifier modifier) {
		node.setModifier(modifier);
		setModifier(node.getOutgoingEdges(), modifier);
	}
	
	private void setCreateForSiteToSite(Edge siteToSiteEdge, Modifier modifier) {
		Node to = siteToSiteEdge.getTo();
		Optional<Edge> opposite = to.getOutgoingEdges().parallelStream().filter(edge -> edge.getTo().equals(siteToSiteEdge.getFrom())).findFirst();
		if(opposite.isPresent())
			opposite.get().setModifier(modifier);
	}
	
	
	private boolean sameNames(Node srcNode, Node trgNode) {
		return nodeToName(srcNode).equals(nodeToName(trgNode));
	}
	
	private void validateSitesOfPersistent(List<Edge> srcSiteEdges, List<Edge> trgSiteEdges, KappaRule rule) {	
		List<List<Edge>> result = validateEdgeAndNodeOfPersistentt(srcSiteEdges, trgSiteEdges);
		List<Edge> srcPersistent = result.get(0);
		List<Edge> trgPersistent = result.get(1);
		srcPersistent.forEach(edge -> setSitePersistent(edge, trgPersistent, rule));			
	}
	
	private void setSitePersistent(Edge srcSideEdge, Collection<Edge> persistentEdges, KappaRule rule) {
		Edge otherSide = setPersistentForEdgeAndNode(srcSideEdge, persistentEdges, rule);
		Node srcNode=srcSideEdge.getTo();
		Node trgNode=otherSide.getTo();
		
		List<Edge> srcStateEdges = filterEdgeListForType(srcNode.getOutgoingEdges(), InternalState.class);
		List<Edge> trgStateEdges = filterEdgeListForType(trgNode.getOutgoingEdges(), InternalState.class);
		
		validateStatesOfPersistent(srcStateEdges, trgStateEdges, rule);
		
		List<Edge> srcSiteToSiteConnections = filterEdgeListForType(srcNode.getOutgoingEdges(), Site.class);
		List<Edge> trgSiteToSiteConnections = filterEdgeListForType(trgNode.getOutgoingEdges(), Site.class);
		
		List<List<Edge>> siteToSiteValidation = validateEdgeOfPersistentt(srcSiteToSiteConnections, trgSiteToSiteConnections);
		List<Edge> srcSiteToSitePersistentConnections = siteToSiteValidation.get(0);
		List<Edge> trgSiteToSitePersistentConnections = siteToSiteValidation.get(1);
		
		srcSiteToSitePersistentConnections.parallelStream().forEach(srcConnection -> setPersistentForEdge(srcConnection, trgSiteToSitePersistentConnections, rule));
		
		siteToSiteValidation.get(2).parallelStream().forEach(edge -> edge.setModifier(Modifier.CREATE));
		siteToSiteValidation.get(3).parallelStream().forEach(edge -> edge.setModifier(Modifier.DELETE));
		
	}
	
	private List<List<Edge>> validateEdgeOfPersistentt(List<Edge> srcEdges, List<Edge> trgEdges){
		final Set<String> srcSymbols = srcEdges.parallelStream().map(edge -> nodeToName(edge.getTo())).collect(Collectors.toSet());
		final Set<String> trgSymbols = trgEdges.parallelStream().map(edge -> nodeToName(edge.getTo())).collect(Collectors.toSet());	
		List<Edge> srcPersistent = srcEdges.parallelStream().filter(edge -> nodeIsContaining(edge.getTo(), trgSymbols)).collect(Collectors.toList());
		List<Edge> trgPersistent = trgEdges.parallelStream().filter(edge -> nodeIsContaining(edge.getTo(), srcSymbols)).collect(Collectors.toList());
		List<Edge> toDelete = srcEdges.parallelStream().filter(edge -> !nodeIsContaining(edge.getTo(), trgSymbols)).collect(Collectors.toList());
		List<Edge> toCreate = trgEdges.parallelStream().filter(edge -> !nodeIsContaining(edge.getTo(), srcSymbols)).collect(Collectors.toList());
		
		return Arrays.asList(srcPersistent, trgPersistent, toCreate, toDelete);
	}
	
	
	private void validateStatesOfPersistent(List<Edge> srcStateEdges, List<Edge> trgStateEdges, KappaRule rule) {
		List<List<Edge>> result = validateEdgeAndNodeOfPersistentt(srcStateEdges, trgStateEdges);
		List<Edge> srcPersistent = result.get(0);
		List<Edge> trgPersistent = result.get(1);
		srcPersistent.forEach(edge -> setPersistentForEdgeAndNode(edge, trgPersistent, rule));
	}
	
	private Edge setPersistentForEdge(Edge srcSideEdge, Collection<Edge> persistentEdges, KappaRule rule) {
		Edge otherSide = getOtherSideEdge(srcSideEdge, persistentEdges);
		setPersistent(srcSideEdge, otherSide, rule);
		return otherSide;
	}
	
	private Edge setPersistentForEdgeAndNode(Edge srcSideEdge, Collection<Edge> persistentEdges, KappaRule rule) {
		Edge otherSide = getOtherSideEdge(srcSideEdge, persistentEdges);
		setPersistent(srcSideEdge, otherSide, rule);
		setPersistent(srcSideEdge.getTo(), otherSide.getTo(), rule);
		return otherSide;
	}
	
	private List<List<Edge>> validateEdgeAndNodeOfPersistentt(List<Edge> srcEdges, List<Edge> trgEdges) {		
		List<List<Edge>> validated = new ArrayList<>(validateEdgeOfPersistentt(srcEdges, trgEdges));
		setModifier(validated.remove(2), Modifier.CREATE);
		setModifier(validated.remove(2), Modifier.DELETE);		
		return validated;
	}
	
	
	private List<Edge> filterEdgeListForType(Collection<Edge> origin, Class<?> type){
		return origin.parallelStream().filter(edge->nodeHasTypeOf(edge.getTo(), type)).collect(Collectors.toList());
	}
	
	private boolean nodeHasTypeOf(Node node, Class<?> type){
		return type.isInstance(node.getElement());
	}
	
	private Edge getOtherSideEdge(Edge edge, Collection<Edge> otherSideEdges) {
		String name = nodeToName(edge.getTo());
		Optional<Edge> monad = otherSideEdges.parallelStream().filter(other -> nodeToName(other.getTo()).equals(name)).findFirst();
		if(monad.isPresent())
			return monad.get();
		else
			return null;
	}
	
	private boolean nodeIsContaining(Node node, Set<String> names) {
		return names.contains(nodeToName(node));
	}
	
	private String nodeToName(Node node) {
		return node.getElement().getName();
	}
	
	private void setPersistent(ModifiableElement srcElement, ModifiableElement trgElement, KappaRule rule) {
		Mapping mapping = KappaRulesFactory.eINSTANCE.createMapping();
		mapping.setRule(rule);
		mapping.setSourceElement(srcElement);
		mapping.setTargetElement(trgElement);		
	}
	
	private List<Node> getAgentNodes(Graph graph){
		List<Node> agentNodes = graph == null ? new ArrayList<>() : graph.getNodes().parallelStream().filter(node -> node.getElement() instanceof Agent).sorted(NodeComparator.create()).collect(Collectors.toList());
		return agentNodes;
	}
}
