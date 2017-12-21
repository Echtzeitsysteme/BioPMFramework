package de.tu.darmstadt.es.PatternMatchingEngine.viatra.runtime;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;

import de.tu.darmstadt.es.KappaRules.KappaRule;
import de.tu.darmstadt.es.PatternMatchingEngine.viatra.VIATRAEngine;
import de.tu.darmstadt.es.xtext.utils.utils.WorkspaceHelper;

public class RuleHandler implements IRuleHandler {
	
	private String packageName;
	
	private MatchLoader matchLoader;
	
	private Map<String, Collection<IPatternMatch>> matchCache = new HashMap<>();
	
	private Map<String, KappaRule> ruleCache = new HashMap<>();
	
	private Map<String, Class<? extends IPatternMatch>> matchClasses;
	
	private VIATRAEngine engine;
	
	public RuleHandler(String packageName, VIATRAEngine engine) {
		this.packageName = packageName;
		matchLoader = new MatchLoader();
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
	public Collection<Class<? extends IPatternMatch>> loadMatches() throws ClassNotFoundException, CoreException, MalformedURLException {
		if (!engine.isLoaded() || matchClasses == null) {
			String packageRealName = packageName.substring(0, packageName.length() - 9);
			IProject project = WorkspaceHelper.INSTANCE.getProjectByName(packageRealName);
			IFolder srcGen = WorkspaceHelper.INSTANCE.getSrcGenFolder(project);
			IFolder projectNameFolder = WorkspaceHelper.INSTANCE.getSubFolderFromQualifiedName(srcGen, packageName);
			matchClasses = matchLoader.loadMatches(projectNameFolder, packageName, ruleCache.keySet());
		}
		return matchClasses.values();
	}

	@Override
	public int countAllMatches() {
		return matchCache.entrySet().parallelStream().mapToInt(entry -> entry.getValue().size()).reduce(0, (a,b) -> a + b);
	}

}
