package de.tu.darmstadt.es.KappaStructure;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import kappaStructure.KappaContainer;

public interface IConverter <Type extends EObject>{
	KappaContainer convert(Type t);
	//Map<> getRatings();
}
