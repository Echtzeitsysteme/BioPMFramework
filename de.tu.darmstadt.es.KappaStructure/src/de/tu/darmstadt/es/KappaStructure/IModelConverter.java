package de.tu.darmstadt.es.KappaStructure;

import org.eclipse.emf.ecore.EObject;

import kappaStructure.KappaContainer;

public interface IModelConverter <Type extends EObject>{
	KappaContainer convert(Type t);
}
