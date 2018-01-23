package de.tu.darmstadt.es.generator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.viatra.query.patternlanguage.patternLanguage.PatternModel;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;
import org.eclipse.xtext.xtext.generator.web.WebIntegrationFragment.Framework;

import de.tu.darmstadt.es.BiochemicalSimulationFramework.utils.FrameworkHelper;
import de.tu.darmstadt.es.KappaRules.KappaRuleContainer;
import de.tu.darmstadt.es.PatternMatchingEngine.viatra.VIATRAEngine;
import de.tu.darmstadt.es.PatternMatchingEngine.viatra.modelconverter.RuleToPatternConverter;
import de.tu.darmstadt.es.biochemicalSimulationFramework.SimulatorConfigurator;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.NaivePatternMatchingController;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.PatternMatchingController;
import de.tu.darmstadt.es.converter.KappaRuleConverter;
import de.tu.darmstadt.es.converter.KappaStructureConverter;
import de.tu.darmstadt.es.converter.NeoKappaExpressionSolver;
import de.tu.darmstadt.es.kappaStructure.KappaContainer;
import de.tu.darmstadt.es.neoKappa.NKAFile;
import de.tu.darmstadt.es.utils.NeoKappaUtil;
import de.tu.darmstadt.es.xtext.utils.utils.ResourceUtil;


public class AbstractNeoKappaGernerator extends AbstractGenerator{

	protected NeoKappaExpressionSolver neoKappaExpressionSolver;
	
	protected KappaStructureConverter modelConverter;
	
	protected KappaRuleConverter ruleConverter;
	
	public AbstractNeoKappaGernerator()
	{
		super();

		neoKappaExpressionSolver = new NeoKappaExpressionSolver();
		modelConverter = new KappaStructureConverter();
		ruleConverter = new KappaRuleConverter();
	}
	
	protected void generateTestViatra(KappaRuleContainer kappaRuleContainer, Resource resource, ResourceSet resSet) {
//		RuleToPatternConverter r2pc = new RuleToPatternConverter();
//		EObject model = r2pc.createPatternModel(kappaRuleContainer, getProjectName(resource)+ ".patterns");
//		ResourceSet resourceSet = new ResourceSetImpl();
//		URI rulesUri = createURIFromResource(resource, "model", "viatraConvertion.vql");
//		try {
//			Resource res = NeoKappaUtil.getInstance().addToResource(model, rulesUri, resourceSet);
//			resourceSet.getResources().remove(res);
//			res = NeoKappaUtil.getInstance().addToResource(model, rulesUri, resourceSet);
//			res.save(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		SimulatorConfigurator simConfig = FrameworkHelper.instance().getDefaultConfig(getProjectName(resource));
		PatternMatchingController pmc = simConfig.getPatternMatchingController();
		pmc.getPatternMatchingEngine().getConverter().convertToPatternModel(kappaRuleContainer);
				
	
		
//		VIATRAEngine engine = new VIATRAEngine(getProjectName(resource), new ResourceSetImpl());
//		engine.convertToPatternModel(kappaRuleContainer);

		
	}
	
	protected String getProjectName(Resource resource) {
		return resource.getURI().toString().split("/")[2];
	}
	
//	protected URI createURIFromResource(Resource resource, String folder, String file){
//		URI originUri = resource.getURI();
//		List<String> segments = Arrays.asList(originUri.toString().split("/"));
//		if(segments.size()>=3){
//			String prefix = segments.get(0) + "/" + segments.get(1) + "/" + segments.get(2) +"/";
//			String path = prefix+folder+"/" + file;
//			return URI.createURI(path);
//		}
//		return originUri;
//	}

	@Override
	public void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {
		try {
		List<EObject> contents = resource.getContents();
				
		if (contents.size() > 0) {
			EObject file = contents.get(0);
			if (file instanceof NKAFile) {
				ResourceSet resSet = resource.getResourceSet();
				KappaRuleContainer rules = this.ruleConverter.convert(NKAFile.class.cast(file));
				URI rulesUri = ResourceUtil.getInstance().createURIFromResource(resource, "model", "ruleSet.xmi");
				
				ResourceUtil.getInstance().saveToResource(rulesUri, resSet, rules);
								
				generateTestViatra(rules,resource,resSet);
				
				KappaContainer model = this.modelConverter.convert(NKAFile.class.cast(file));
				URI modelUri =  ResourceUtil.getInstance().createURIFromResource(resource, "model", "kappaModel.xmi");
				ResourceUtil.getInstance().saveToResource(modelUri, resSet, model);
				
				//NeoKappaUtil.instance.save(model, modelUri, resSet)
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	

		
		
		
}
