package de.tu.darmstadt.es.PatternMatchingEngine.viatra.runtime;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.eclipse.osgi.internal.loader.BundleLoader;
import org.eclipse.osgi.internal.loader.EquinoxClassLoader;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import de.tu.darmstadt.es.xtext.utils.utils.WorkspaceHelper;

public class MatchLoader {

	@SuppressWarnings("unchecked")
	public Map<String, Class<? extends IPatternMatch>> loadMatches(IFolder patternPackageFolder, String packageName, Collection<String> ruleNames) throws ClassNotFoundException, CoreException, MalformedURLException, BundleException {
		Map<String, Class<? extends IPatternMatch>> matchClasses = new HashMap<>();
		for(String ruleName : ruleNames) {
			Package package1 = Package.getPackage(packageName);
			String className = capitalizeFirstLetter(ruleName + "Match");
			String classNameWithPackage=packageName+'.'+className;
			IFile classFile = patternPackageFolder.getFile(className+".java");
			if(classFile.exists()) {
				getClassFromFile(classFile, classNameWithPackage);
				Class<?> clazz = Class.forName(classNameWithPackage);
				if(IPatternMatch.class.isAssignableFrom(clazz)) {
					matchClasses.put(ruleName, (Class<? extends IPatternMatch>) clazz);
			}
				
					
			}
		}		
		return matchClasses;
	}
	
	private Class<?> getClassFromFile(IFile iFile, String className) throws CoreException, MalformedURLException, ClassNotFoundException, BundleException{
		//String str = convertStreamToString(iFile.getContents());
		IProject project = iFile.getProject();
		
		File javaFile = iFile.getFullPath().toFile().getAbsoluteFile();
		
		IFolder binFolder = WorkspaceHelper.INSTANCE.getBinFolder(iFile.getProject());
		URL binFolderAsFile = binFolder.getRawLocationURI().toURL();
		ClassLoader innerClassLoader = this.getClass().getClassLoader();
		String projectName = project.getName();
//		File file = project.getRawLocation().toFile();
		FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class).iterator().next();
		Map<String,String> config = new HashMap<String,String>();
		Framework framework = frameworkFactory.newFramework(config);
		framework.start();
		BundleContext context = framework.getBundleContext();
		Bundle bundle = context.installBundle("reference:file:"+project.getLocation().toString());
		bundle.start();
		
		//Bundle bundle = Platform.getBundle(projectName);
		
//		List<Bundle> bundles = Arrays.asList(Platform.getBundles(projectName, ""));
		
		
		//ClassLoader classLoader = new EquinoxClassLoader(parent, configuration, delegate, generation); //;new URLClassLoader(new URL[] {binFolderAsFile});
		Class<?> clazz = bundle.loadClass(className);//Class.forName(className, true, classLoader);//classLoader.loadClass(className);
		//JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		
		return clazz;
	}
	
	private static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	private String capitalizeFirstLetter(String name) {
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}
}
