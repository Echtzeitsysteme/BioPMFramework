package de.tu.darmstadt.es.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class NeoKappaUtil {
	private static NeoKappaUtil instance;
	
	private NeoKappaUtil() {}
	
	public static NeoKappaUtil getInstance() {
		if(instance == null)
			instance = new NeoKappaUtil();
		return instance;
	}
	
	public <SuperType extends Object, SubType extends SuperType> List<SubType> sortedMapToSubType(Collection<SuperType> supCol, Class<SubType> clazz){
		return mapToSubType(supCol.stream(), clazz);
	}
	
	private <SuperType extends Object, SubType extends SuperType> List<SubType> mapToSubType(Stream<SuperType> stream, Class<SubType> clazz){
		return stream.filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList());
	}
	
	public <SuperType extends Object, SubType extends SuperType> List<SubType> unsortedMapToSubType(Collection<SuperType> supCol, Class<SubType> clazz){
		return mapToSubType(supCol.parallelStream(), clazz);
	}
	
	public Resource save(EObject model, URI uri, ResourceSet resourceSet) throws IOException {
		Resource resource = addToResource(model, uri, resourceSet);
		resource.save(null);
		return resource;
	}
	
	public Resource addToResource(EObject model, URI uri, ResourceSet resourceSet) throws IOException {
		Resource resource = getResource(uri, resourceSet, false);
		resource.getContents().clear();
		resource.getContents().add(model);
		return resource;
	}
	
	public Resource getResource(URI uri, ResourceSet resourceSet, boolean load) throws IOException {
		Resource resource = resourceSet.getResource(uri, false);
		if(resource == null) {
			resource = resourceSet.createResource(uri);
		}
		if(load) {
			resource.load(Collections.EMPTY_MAP);
			EcoreUtil.resolveAll(resource);
		}
		return resource;
	}
}
