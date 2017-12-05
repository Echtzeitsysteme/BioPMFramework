package de.tu.darmstadt.es.generator;

import org.eclipse.xtext.generator.AbstractGenerator;

import de.tu.darmstadt.es.converter.KappaRuleConverter;
import de.tu.darmstadt.es.converter.KappaStructureConverter;
import de.tu.darmstadt.es.converter.NeoKappaExpressionSolver;


public abstract class AbstractNeoKappaGernerator extends AbstractGenerator{

	protected NeoKappaExpressionSolver neoKappaExpressionSolver;
	
	protected KappaStructureConverter modelConverter;
	
	protected KappaRuleConverter ruleConverter;
	
	public AbstractNeoKappaGernerator()
	{
		super();

		neoKappaExpressionSolver = new NeoKappaExpressionSolver();
		modelConverter = new KappaStructureConverter();
		ruleConverter = new KappaRuleConverter();
	}
	

	

		
		
		
}
