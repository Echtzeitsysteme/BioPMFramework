package de.tu.darmstadt.es.PatternMatchingEngine.viatra.runtime;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;

import de.tu.darmstadt.es.xtext.utils.utils.WorkspaceHelper;

public class MatchLoader {

	@SuppressWarnings("unchecked")
	public Map<String, Class<? extends IPatternMatch>> loadMatches(IFolder patternPackageFolder, String packageName, Collection<String> ruleNames) throws ClassNotFoundException, CoreException, MalformedURLException {
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
	
	private Class<?> getClassFromFile(IFile iFile, String className) throws CoreException, MalformedURLException, ClassNotFoundException{
		//String str = convertStreamToString(iFile.getContents());
		File javaFile = iFile.getFullPath().toFile().getAbsoluteFile();
		
		IFolder binFolder = WorkspaceHelper.INSTANCE.getBinFolder(iFile.getProject());
		URL binFolderAsFile = binFolder.getRawLocationURI().toURL();
		
		URLClassLoader classLoader = new URLClassLoader(new URL[] {binFolderAsFile});
		Class<?> clazz = Class.forName(className, true, classLoader);//classLoader.loadClass(className);
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
