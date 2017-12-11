package de.tu.darmstadt.es.PatternMatchingEngine.viatra.utils;

import java.util.HashMap;
import java.util.Map;

public class TypeCounter {
	private Map<Class<?>, Integer> counters;
	
	public TypeCounter() {
		counters=new HashMap<Class<?>, Integer>();
	}
	
	public int getCurrentNubmer(Object object) {
		int current = counters.getOrDefault(object.getClass(), -1);
		counters.put(object.getClass(), ++current);		
		return current;
	}
	
}
