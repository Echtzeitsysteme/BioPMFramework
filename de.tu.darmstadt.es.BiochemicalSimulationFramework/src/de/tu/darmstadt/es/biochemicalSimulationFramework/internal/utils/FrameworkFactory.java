package de.tu.darmstadt.es.biochemicalSimulationFramework.internal.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.resource.ResourceSet;

import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.patternmatchingengine.PatternMatchingEngine;

public class FrameworkFactory {
	private static FrameworkFactory instance;
	
	private FrameworkFactory() {
		
	}
	
	public static FrameworkFactory instance() {
		if(instance == null)
			instance = new FrameworkFactory();
		return instance;
	}
	
	public <PM extends PatternMatchingEngine> PM createPatternMatchingEngine (String packageName, ResourceSet resourceSet, Class<PM> clazz) {
		
		Optional<Constructor<?>> constructorMonad = Arrays.asList(clazz.getConstructors()).parallelStream().filter(this::findConstructor).findFirst();
		if(constructorMonad.isPresent()) {
			Constructor<?> constructor = constructorMonad.get();
			try {
				Object engine = constructor.newInstance(packageName,resourceSet);
				if(engine instanceof PatternMatchingEngine) {
					return clazz.cast(engine);
				}				
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();				
			}
		}
		return null;
	}
	
	
	private boolean findConstructor(Constructor<?> constructor) {
		if(constructor.getParameterCount() == 2) {
			List<Class<?>> parameterTypes = Arrays.asList(constructor.getParameterTypes());
			return parameterTypes.stream().anyMatch(String.class::isAssignableFrom) && parameterTypes.stream().anyMatch(ResourceSet.class::isAssignableFrom);
		}
		
		return false;
	}
}
