package de.tu.darmstadt.es.PatternMatchingEngine.viatra.template



class VIATRAMainTemplate extends AbstractVIATRAMainTemplate {
	
	new(String packageName) {
		super(packageName)
	}
	
	override public getTemplateText() {
		
		'''
		«packageDesciption»
		
		«imports»
		
		«classDescription»
		'''
	}
	
	override protected getImports() {
		'''
		import java.io.File;
		import java.util.Arrays;
		import java.util.List;
		import java.util.stream.Collectors;
		import org.eclipse.viatra.query.runtime.exception.ViatraQueryException;
		import de.tu.darmstadt.es.BiochemicalSimulationFramework.utils.FrameworkHelper;
		import de.tu.darmstadt.es.biochemicalSimulationFramework.SimulatorConfigurator;
		import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.PatternMatchingController;
		import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.patternmatchingengine.PatternMatchingEngine;
		import de.tu.darmstadt.es.kappaStructure.KappaStructurePackage;
		import «patternPackageString».*;
		'''
	}
	
	override protected getStaticVariables() {
		'''
			private final static String PACKAGE_NAME = "«patternPackageString»";	
			private final static String PACKAGE_PREFIX = PACKAGE_NAME + '.';
			private final static String CLASS_SUFFIX = ".class";
			private final static String MATCH_CLASS = "Match" + CLASS_SUFFIX;
			private final static String CLASS_PATH = "«packageBinaryPath»";
			private final static String MODEL_PATH = "model/kappaModel.xmi";
		'''
	}
	
	override protected getClassBody() {
		'''
			«staticVariables»
		'''
	}
	
	override protected getClassDescription() {
		'''
			public class Main {
					«classBody»
					
					«methods»
			}
		'''
	}
	
	override protected getPackageDesciption() {
		'''package «packageName».main;'''
	}
	
	override protected getMethods() {
		'''
			private List<Class<?>> loadClasses() throws ClassNotFoundException{
				File dir = new File(CLASS_PATH).getAbsoluteFile();
				if(dir.exists() && dir.isDirectory()) {
					List<String> classNames = Arrays.asList(dir.list()).parallelStream().filter(filename->filename.endsWith(MATCH_CLASS)).map(str -> PACKAGE_PREFIX +  str.replace(CLASS_SUFFIX, "")).collect(Collectors.toList());
					return classNames.parallelStream().map(this::convertToClass).collect(Collectors.toList());
				}		
				return null;
			}
			
			private Class<?> convertToClass (String name){
				try {
					return Class.forName(name);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			private static void loadDependencies() throws ViatraQueryException {
				KappaStructurePackage.eINSTANCE.getName();
				ViatraConvertion.instance();
			}
			
			public static void main(String[] args) throws ClassNotFoundException, ViatraQueryException {
				loadDependencies();		
				SimulatorConfigurator sc = FrameworkHelper.instance().getDefaultConfig(PACKAGE_NAME);
				PatternMatchingController pmc  = sc.getPatternMatchingController();
				PatternMatchingEngine pm = pmc.getPatternMatchingEngine();
				pm.run(new Main().loadClasses(), MODEL_PATH);
			}
		'''
	}
	
	
	
}