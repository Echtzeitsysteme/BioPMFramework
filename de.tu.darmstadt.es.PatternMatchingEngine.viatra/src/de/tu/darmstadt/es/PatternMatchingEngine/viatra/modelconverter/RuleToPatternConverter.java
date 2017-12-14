package de.tu.darmstadt.es.PatternMatchingEngine.viatra.modelconverter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.CompareConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.CompareFeature;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ExecutionType;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Modifiers;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Parameter;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ParameterDirection;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ParameterRef;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionConstraint;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionHead;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PathExpressionTail;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguageFactory;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguageFactory;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ReferenceType;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.XImportSection;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Type;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Variable;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.VariableReference;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.VariableValue;

import de.tu.darmstadt.es.KappaRules.BackwardRule;
import de.tu.darmstadt.es.KappaRules.Edge;
import de.tu.darmstadt.es.KappaRules.ForwardRule;
import de.tu.darmstadt.es.KappaRules.KappaRule;
import de.tu.darmstadt.es.KappaRules.KappaRuleContainer;
import de.tu.darmstadt.es.KappaRules.Node;
import de.tu.darmstadt.es.KappaRules.Source;
import de.tu.darmstadt.es.KappaRules.SubRule;
import de.tu.darmstadt.es.PatternMatchingEngine.viatra.utils.TypeCounter;
import de.tu.darmstadt.es.kappaStructure.Agent;
import de.tu.darmstadt.es.kappaStructure.KappaContainer;
import de.tu.darmstadt.es.xtext.utils.utils.BiMap;
import de.tu.darmstadt.es.xtext.utils.utils.HashBiMap;

public class RuleToPatternConverter {
	
	private PackageImport packageImport;
	
	public PatternModel createPatternModel(KappaRuleContainer kappaRuleContainer) {
		//patternmodel creation
		PatternModel patternModel = EMFPatternLanguageFactory.eINSTANCE.createPatternModel();
		patternModel.setPackageName("abc"); // TODO SaschaEdwinZander insert real packagename 
		
		// create imports
		patternModel.setImportPackages(createImport(kappaRuleContainer.eClass().getEPackage()));
		
		//Pattern creation
		List<KappaRule> rules = kappaRuleContainer.getRules().parallelStream().filter(rule -> rule.getSource() != null).collect(Collectors.toList());
		List<Pattern> patterns=rules.parallelStream().map(this::createPattern).collect(Collectors.toList());		
		patternModel.getPatterns().addAll(patterns);
		
		return patternModel;
	}
	
	private XImportSection createImport(EPackage ePackage) {
		XImportSection importSection = EMFPatternLanguageFactory.eINSTANCE.createXImportSection();
		
		packageImport = EMFPatternLanguageFactory.eINSTANCE.createPackageImport();
		importSection.getPackageImport().add(packageImport);
		
		packageImport.setEPackage(ePackage);
		
		
		return importSection;
	}
	
	private Pattern createPattern(KappaRule rule) {
		//Pattern basic creation
		Pattern pattern = PatternLanguageFactory.eINSTANCE.createPattern();
		pattern.setName(generatePatternName(rule));
		
		//create Parameters
		Source source = rule.getSource();
		TypeCounter typeCounter = new TypeCounter();
		BiMap<String, EObject> nameElementCache = new HashBiMap<>();
		List<Parameter> parameters = source.getNodes().stream().map(node -> generateParameter(node.getElement(), nameElementCache, typeCounter)).collect(Collectors.toList());
		parameters.add(generateParameter(source.getKappaModel(), nameElementCache, typeCounter));
		pattern.getParameters().addAll(parameters);
		
		//create Body
		PatternBody patternBody = PatternLanguageFactory.eINSTANCE.createPatternBody();
		pattern.getBodies().add(patternBody);
		fillBody(patternBody, parameters, nameElementCache, rule);
		
		//create Modifiers
		Modifiers modifiers = PatternLanguageFactory.eINSTANCE.createModifiers();
		pattern.setModifiers(modifiers);
		modifiers.setPrivate(false);
		modifiers.setExecution(ExecutionType.UNSPECIFIED);
		
		return pattern;
	}
	
	private void fillBody(PatternBody body, List<Parameter>  parameters, BiMap<String, EObject> nameElementCache, KappaRule rule) {
		// create references to the parameter
		BiMap<Parameter, ParameterRef> parameterRefCache = new HashBiMap<>();
		body.getVariables().addAll(parameters.parallelStream().map(param -> createParameterRef(param, parameterRefCache)).collect(Collectors.toList()));
		
		// create distinction of same types in main patterns
		if(!(rule instanceof SubRule))
			createConstraintsForDistinction(body, parameters, parameterRefCache);
		
		KappaContainer model = rule.getSource().getKappaModel();
		List<Node> agentNodes = rule.getSource().getNodes().parallelStream().filter(node -> node.getElement() instanceof Agent).collect(Collectors.toList());
		body.getConstraints().addAll(startCreateConnection(model, agentNodes, nameElementCache, parameterRefCache));
	}
	
	private List<PathExpressionConstraint> startCreateConnection(KappaContainer model, List<Node> nodes, BiMap<String, EObject> nameElementCache, BiMap<Parameter, ParameterRef> parameterRefCache) {
		List<PathExpressionConstraint> constraints = new ArrayList<>();
		constraints.addAll(nodes.parallelStream().map(node -> createPathExpressionConstraint(model, node.getElement(), nameElementCache, parameterRefCache)).collect(Collectors.toList()));
		Set<Edge> visited = new HashSet<>(); //for cycles
		Stack<Node> stack = new Stack<>();
		stack.addAll(nodes);
		
		while(!stack.isEmpty()) {
			Node srcNode = stack.pop();
			List<Edge> edges = srcNode.getOutgoingEdges().parallelStream().filter(edge -> !visited.contains(edge)).collect(Collectors.toList());
			visited.addAll(edges);
			List<Node> targets = edges.parallelStream().map(edge -> edge.getTo()).collect(Collectors.toList());
			constraints.addAll(targets.parallelStream().map(targetNode -> createPathExpressionConstraint(srcNode.getElement(), targetNode.getElement(), nameElementCache, parameterRefCache)).collect(Collectors.toList()));
			stack.addAll(targets);			
		}		
		
		return constraints;
	}
	
	private PathExpressionConstraint createPathExpressionConstraint(EObject source, EObject target, BiMap<String, EObject> nameElementCache, BiMap<Parameter, ParameterRef> parameterRefCache) {
		//init
		PathExpressionConstraint constraint = PatternLanguageFactory.eINSTANCE.createPathExpressionConstraint();
		
		//create head
		PathExpressionHead head = PatternLanguageFactory.eINSTANCE.createPathExpressionHead();
		constraint.setHead(head);
		
		//fill head
		head.setType(generateType(source.eClass()));
		
		//create Tail
		PathExpressionTail tail = PatternLanguageFactory.eINSTANCE.createPathExpressionTail();
		head.setTail(tail);
		tail.setType(createReferenceType(source.eClass(), target));
		
		//create Src
		String srcTypeName = nameElementCache.getKey(source);
		Variable srcRef = parameterRefCache.values().parallelStream().filter(paramRef -> paramRef.getName().equals(srcTypeName)).findFirst().get();
		head.setSrc(createVariableReference(srcRef));
		
		//create Des
		String desTypeName = nameElementCache.getKey(target);
		Parameter desParameter = parameterRefCache.keySet().parallelStream().filter(param -> param.getName().equals(desTypeName)).findFirst().get();
		head.setDst(createVariableValue(desParameter, parameterRefCache));
		
		return constraint;
	}
	
	
	
	private ReferenceType createReferenceType(EClass sourceType, EObject target) {
		ReferenceType referenceType = EMFPatternLanguageFactory.eINSTANCE.createReferenceType();
		EStructuralFeature feature = target.eContainingFeature();
		referenceType.setRefname(feature);
		return referenceType;
	}
	
	private VariableReference createVariableReference(Variable variable) {
		VariableReference variableReference = PatternLanguageFactory.eINSTANCE.createVariableReference();
		variableReference.setVariable(variable);
		variableReference.setVar(variable.getName());
		variableReference.setAggregator(false);
		return variableReference;
	}
	
	private ParameterRef createParameterRef(Parameter parameter, BiMap<Parameter, ParameterRef> parameterRefCache) {
		ParameterRef parameterRef = PatternLanguageFactory.eINSTANCE.createParameterRef();
		parameterRef.setReferredParam(parameter);
		parameterRef.setName(parameter.getName());
		parameterRefCache.put(parameter, parameterRef);
		return parameterRef;
	}
	
	private void createConstraintsForDistinction(PatternBody body, List<Parameter>  parameters, BiMap<Parameter, ParameterRef> parameterRefCache) {
		List<List<Parameter>> sameTypes = getSameTypes(parameters);
		List<List<CompareConstraint>> compareConstraintss = sameTypes.parallelStream().map(same -> createDistinction(same, parameterRefCache)).collect(Collectors.toList());
		body.getConstraints().addAll(compareConstraintss.parallelStream().flatMap(compareConstraints -> compareConstraints.stream()).collect(Collectors.toList()));
	}
	
	private List<CompareConstraint> createDistinction(List<Parameter> parameters, BiMap<Parameter, ParameterRef> parameterRefCache){
		List<CompareConstraint> compareConstraints = new ArrayList<>();
		for(int i = 0; i < parameters.size(); ++i) {
			for(int k = i+1; k < parameters.size(); ++k) {
				Parameter left = parameters.get(i);
				Parameter right = parameters.get(k);
				compareConstraints.add(createCompareConstraint(left, right, parameterRefCache));
			}
		}
		return compareConstraints;
	}
	
	private CompareConstraint createCompareConstraint(Parameter left, Parameter right, BiMap<Parameter, ParameterRef> parameterRefCache) {
		CompareConstraint constraint = PatternLanguageFactory.eINSTANCE.createCompareConstraint();
		constraint.setFeature(CompareFeature.INEQUALITY);

		constraint.setLeftOperand(createVariableValue(left, parameterRefCache));
		
		constraint.setRightOperand(createVariableValue(right, parameterRefCache));
		
		return constraint;
	}
	
	private VariableValue createVariableValue(Parameter parameter, BiMap<Parameter, ParameterRef> parameterRefCache) {
		VariableValue variableValue = PatternLanguageFactory.eINSTANCE.createVariableValue();
		VariableReference variableReference = PatternLanguageFactory.eINSTANCE.createVariableReference();
		variableValue.setValue(variableReference);
		variableReference.setAggregator(false);
		variableReference.setVar(parameter.getName());
		variableReference.setVariable(parameterRefCache.getValue(parameter));
		return variableValue;
	}
	
	private List<List<Parameter>> getSameTypes (List<Parameter>  parameters) {
		Set<String> names = parameters.parallelStream().map(param -> removeLastCharacter(param.getName())).collect(Collectors.toSet());
		return names.parallelStream().map(name -> parameters.stream().filter(param -> param.getName().startsWith(name)).collect(Collectors.toList())).filter(lst->lst.size()>=2).collect(Collectors.toList());
	}
	
	private String removeLastCharacter(String string) {
		return string.substring(0, string.length()-1);
	}
	
	private String generatePatternName(KappaRule rule) {
		String prefix;
		if(rule instanceof ForwardRule)
			prefix = "fwd_";
		else if(rule instanceof BackwardRule)
			prefix = "bwd_";
		else
			prefix = "sub_";
		
		return prefix + rule.getName();
	}
	
	private Parameter generateParameter(EObject eObject, BiMap<String, EObject> nameElementCache, TypeCounter typeCounter) {
		Parameter parameter = PatternLanguageFactory.eINSTANCE.createParameter();
		String name = nameElementCache.getKeyOrDefault(eObject, generateLowerCaseName(eObject.eClass()) + typeCounter.getCurrentNubmer(eObject));
		nameElementCache.put(name, eObject);
		parameter.setName(name);
		parameter.setType(generateType(eObject.eClass()));
		parameter.setDirection(ParameterDirection.INOUT);
		return parameter;
	}
	
	private Type generateType(EClass eclass) {
		ClassType type = EMFPatternLanguageFactory.eINSTANCE.createClassType();
		type.setClassname(eclass);
		return type;
	}
	
	private String generateLowerCaseName(EClass eClass) {
		String name = eClass.getName();
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}
}
