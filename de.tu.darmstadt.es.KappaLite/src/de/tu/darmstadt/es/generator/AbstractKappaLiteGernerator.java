package de.tu.darmstadt.es.generator;

import org.eclipse.xtext.generator.AbstractGenerator;

import de.tu.darmstadt.es.KappaStructure.IConverter;
import de.tu.darmstadt.es.converter.KappaLiteToKappaStructureConverter;
import de.tu.darmstadt.es.kappaLite.KLIFile;

public abstract class AbstractKappaLiteGernerator extends AbstractGenerator{

	public AbstractKappaLiteGernerator()
	{
		super();
		converter = new KappaLiteToKappaStructureConverter();
	}
	
	protected IConverter<KLIFile> converter;
}
