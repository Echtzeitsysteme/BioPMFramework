package de.tu.darmstadt.es.PatternMatchingEngine.viatra.runtime;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.osgi.framework.BundleException;

import de.tu.darmstadt.es.KappaRules.KappaRule;
import de.tu.darmstadt.es.PatternMatchingEngine.viatra.VIATRAEngine;
import de.tu.darmstadt.es.xtext.utils.utils.WorkspaceHelper;

public class RuleHandler implements IRuleHandler {
	
	private String packageName;
	
	private Map<String, Collection<IPatternMatch>> matchCache = new HashMap<>();
	
	private Map<String, KappaRule> ruleCache = new HashMap<>();
	
	private Map<String, Class<? extends IPatternMatch>> matchClasses;
	
	private VIATRAEngine engine;
	
	public RuleHandler(String packageName, VIATRAEngine engine) {
		this.packageName = packageName;
		this.engine = engine;
	}


	
	@Override
	public void addMatch(String ruleName, IPatternMatch it) {
		Collection<IPatternMatch> matches = matchCache.getOrDefault(ruleName, new HashSet<>());
		matches.add(it);
		matchCache.put(ruleName, matches);
	}

	@Override
	public void removeMatch(String ruleName, IPatternMatch it) {
		Collection<IPatternMatch> matches = matchCache.getOrDefault(ruleName, new HashSet<>());
		matches.remove(it);
		matchCache.put(ruleName, matches);
	}

	@Override
	public int countMatchesForRule(String ruleName) {
		return matchCache.getOrDefault(ruleName, new ArrayList<>()).size();
	}

	@Override
	public void addKappaRule(String ruleName, KappaRule kappaRule) {
		ruleCache.put(ruleName, kappaRule);

	}

	@Override
	public void loadMatches(List<Class<?>> classes) throws ClassNotFoundException, CoreException, MalformedURLException, BundleException {
		if (!engine.isLoaded() || matchClasses == null) {
			if(matchClasses == null)
				matchClasses = new HashMap<>();
			List<Class<IPatternMatch>> matchClassList = classes.parallelStream().filter(IPatternMatch.class::isAssignableFrom).map(this::convertAnyClassToIPatternMatchClass).collect(Collectors.toList());
			for(Class<IPatternMatch> clazz : matchClassList) {
				matchClasses.put(clazz.getName(), clazz);
			}
		}		
	}

	@SuppressWarnings("unchecked")
	private Class<IPatternMatch> convertAnyClassToIPatternMatchClass(Class<?> anyClass) {
		return (Class<IPatternMatch>) anyClass;
	}
	
	@Override
	public int countAllMatches() {
		return matchCache.entrySet().parallelStream().mapToInt(entry -> entry.getValue().size()).reduce(0, (a,b) -> a + b);
	}



	@Override
	public Collection<Class<? extends IPatternMatch>> getMatches() {
		return matchClasses.values();
	}

}
