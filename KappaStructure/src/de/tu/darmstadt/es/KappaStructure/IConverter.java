package de.tu.darmstadt.es.KappaStructure;

import org.eclipse.emf.ecore.EObject;

import kappaStructure.KappaContainer;

public interface IConverter <Type extends EObject>{
	KappaContainer convert(Type t);
}
