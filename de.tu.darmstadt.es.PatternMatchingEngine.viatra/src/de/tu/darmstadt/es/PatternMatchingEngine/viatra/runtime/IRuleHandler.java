package de.tu.darmstadt.es.PatternMatchingEngine.viatra.runtime;



import java.net.MalformedURLException;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;

import de.tu.darmstadt.es.KappaRules.KappaRule;

public interface IRuleHandler {
	void addMatch(String ruleName, IPatternMatch match);
	void removeMatch(String ruleName, IPatternMatch match);
	int countMatchesForRule(String ruleName);
	int countAllMatches();
	
	Collection<Class<? extends IPatternMatch>> loadMatches() throws ClassNotFoundException, CoreException, MalformedURLException;
	
	void addKappaRule(String ruleName, KappaRule kappaRule);
}
