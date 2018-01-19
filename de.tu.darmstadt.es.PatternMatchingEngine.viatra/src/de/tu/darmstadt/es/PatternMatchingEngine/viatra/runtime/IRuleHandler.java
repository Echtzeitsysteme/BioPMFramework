package de.tu.darmstadt.es.PatternMatchingEngine.viatra.runtime;



import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.osgi.framework.BundleException;

import de.tu.darmstadt.es.KappaRules.KappaRule;

public interface IRuleHandler {
	void addMatch(String ruleName, IPatternMatch match);
	void removeMatch(String ruleName, IPatternMatch match);
	int countMatchesForRule(String ruleName);
	int countAllMatches();
	
	void loadMatches(List<Class<?>> clases) throws ClassNotFoundException, CoreException, MalformedURLException, BundleException;
	Collection<Class<? extends IPatternMatch>> getMatches();
	void addKappaRule(String ruleName, KappaRule kappaRule);
}
