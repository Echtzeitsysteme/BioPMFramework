package de.tu.darmstadt.es.biochemicalSimulationFramework.internal.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.emf.ecore.resource.ResourceSet;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.PatternMatchingController;
import de.tu.darmstadt.es.biochemicalSimulationFramework.patternmatchingcontroller.geolibrary.GeoLibrary;
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
		return create(clazz, packageName, resourceSet);
	}
	
	public <PMC extends PatternMatchingController> PMC createPatternMatchingController (String packageName, ResourceSet resourceSet, Class<? extends PatternMatchingEngine> engineType, Class<? extends GeoLibrary> geoLibType, Class<PMC> clazz) {
		if(geoLibType == null) {
			return create(clazz, packageName, resourceSet, engineType);
		}else {
			return create(clazz, packageName, resourceSet, engineType, geoLibType);
		}
	}
	
	public <GL extends GeoLibrary> GL createGeoLibrary(Class<GL> clazz) {
		return create(clazz);
	}
	
	private <T extends Object> T create (Class<T> clazz, Object... arguments) {
		List<Class<?>> paramTypeList = Arrays.asList(arguments).parallelStream().map(Object::getClass).collect(Collectors.toList());
		Optional<Constructor<?>> constructorMonad = findConstructor(clazz, paramTypeList);
		return create(constructorMonad, clazz, arguments);
	}
	
	private <T extends Object> T create (Optional<Constructor<?>> constructorMonad, Class<T> clazz, Object... arguments) {
		if(constructorMonad.isPresent()){
			Constructor<?> constructor = constructorMonad.get();			
				try {
					Object object = constructor.newInstance(arguments);
					if(clazz.isInstance(object)) {
						return clazz.cast(object);
					}
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException	| InvocationTargetException e) {
					e.printStackTrace();
				}
				
			
		}
		
		return null;
	}
	
	private Optional<Constructor<?>> findConstructor(Class<?> clazz ,List<Class<?>> paramTypes){
		return Arrays.asList(clazz.getConstructors()).parallelStream().filter(constructor -> isConstructor(constructor, paramTypes)).findFirst();
	}	
	
	private boolean isConstructor(Constructor<?> constructor, List<Class<?>> paramTypes) {
		if(constructor.getParameterCount() == paramTypes.size()) {
			Class<?>[] constructorTypes = constructor.getParameterTypes();
			for(int index = 0; index < paramTypes.size(); ++index) {
				if(!constructorTypes[index].isAssignableFrom(paramTypes.get(index))) {
					return false;
				}				
			}
			return true;			
		}
		
		return false;
	}
}
