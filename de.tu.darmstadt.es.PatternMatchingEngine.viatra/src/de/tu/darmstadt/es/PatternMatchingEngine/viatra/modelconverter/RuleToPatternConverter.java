package de.tu.darmstadt.es.PatternMatchingEngine.viatra.modelconverter;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.EntityType;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Modifiers;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Parameter;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.ParameterDirection;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Pattern;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternLanguageFactory;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguageFactory;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra.query.patternlanguage.emf.eMFPatternLanguage.XImportSection;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.Type;

import de.tu.darmstadt.es.KappaRules.BackwardRule;
import de.tu.darmstadt.es.KappaRules.ForwardRule;
import de.tu.darmstadt.es.KappaRules.KappaRule;
import de.tu.darmstadt.es.KappaRules.KappaRuleContainer;
import de.tu.darmstadt.es.KappaRules.ModifiableElement;
import de.tu.darmstadt.es.KappaRules.Node;
import de.tu.darmstadt.es.KappaRules.Source;
import de.tu.darmstadt.es.PatternMatchingEngine.viatra.utils.TypeCounter;
import de.tu.darmstadt.es.xtext.utils.utils.BiMap;
import de.tu.darmstadt.es.xtext.utils.utils.HashBiMap;

public class RuleToPatternConverter {
	
	private PackageImport packageImport;
	
	public PatternModel createPatternModel(KappaRuleContainer kappaRuleContainer) {
		PatternModel patternModel = EMFPatternLanguageFactory.eINSTANCE.createPatternModel();
		patternModel.setImportPackages(createImport(kappaRuleContainer.eClass().getEPackage()));
		
		List<KappaRule> rules = kappaRuleContainer.getRules().parallelStream().filter(rule -> rule.getSource() != null).collect(Collectors.toList());
		List<Pattern> patterns=rules.parallelStream().map(this::createPattern).collect(Collectors.toList());
		
		patternModel.getPatterns().addAll(patterns);
		patternModel.setPackageName("abc");
		
		
		
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
		Pattern pattern = PatternLanguageFactory.eINSTANCE.createPattern();
		pattern.setName(generatePatternName(rule));
		Source source = rule.getSource();
		TypeCounter typeCounter = new TypeCounter();
		BiMap<String, ModifiableElement> nameElementCache = new HashBiMap<>();
		List<Parameter> parameters = source.getNodes().stream().map(node -> generateParameter(node, nameElementCache, typeCounter)).collect(Collectors.toList());
		pattern.getParameters().addAll(parameters);
		
		PatternBody patternBody = PatternLanguageFactory.eINSTANCE.createPatternBody();
		pattern.getBodies().add(patternBody);
		
		Modifiers modifiers = PatternLanguageFactory.eINSTANCE.createModifiers();
		pattern.setModifiers(modifiers);
		
		modifiers.setPrivate(false);
		
		return pattern;
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
	
	private Parameter generateParameter(Node node, BiMap<String, ModifiableElement> nameElementCache, TypeCounter typeCounter) {
		Parameter parameter = PatternLanguageFactory.eINSTANCE.createParameter();
		String name = nameElementCache.getKeyOrDefault(node, generateLowerCaseName(node.getElement().eClass()) + typeCounter.getCurrentNubmer(node.getElement()));
		nameElementCache.put(name, node);
		parameter.setName(name);
		parameter.setType(generateType(node.getElement().eClass()));
		parameter.setDirection(ParameterDirection.INOUT);
		return parameter;
	}
	
	private Type generateType(EClass eclass) {
		ClassType type = EMFPatternLanguageFactory.eINSTANCE.createClassType();
		type.setClassname(eclass);
//		type.setMetamodel(packageImport);
//		type.setTypename(eclass.getName());
		return type;
	}
	
	private String generateLowerCaseName(EClass eClass) {
		String name = eClass.getName();
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}
}
