package de.tu.darmstadt.es.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;

import de.tu.darmstadt.es.neoKappa.NKADecimalLiteralExpression;
import de.tu.darmstadt.es.neoKappa.NKAExpression;
import de.tu.darmstadt.es.neoKappa.NKAIntegerLiteralExpression;
import de.tu.darmstadt.es.neoKappa.NKAMathExpression;
import de.tu.darmstadt.es.neoKappa.NKAVariableExpression;

public class NeoKappaExpressionSolver {
	
	private Map<String, BiFunction<Double, Double, Double>> operations;
	
	public NeoKappaExpressionSolver() {
		operations = new HashMap<>();
		
		operations.put("+", (a,b) -> a + b);
		operations.put("-", (a,b) -> a - b);
		operations.put("*", (a,b) -> a * b);
		operations.put("/", (a,b) -> a / b);
	}

	public double solveExpression(NKAExpression expression) {
		Stack<NKAExpression> stack = new Stack<>();
		stack.push(expression);
		double tmp=0.0;
		while(stack.size()>0) {
			NKAExpression expr = stack.pop();
			if(expr instanceof NKAIntegerLiteralExpression) {
				tmp = NKAIntegerLiteralExpression.class.cast(expr).getValue();
			}else if (expr instanceof NKADecimalLiteralExpression) {
				tmp = NKADecimalLiteralExpression.class.cast(expr).getValue();
			}else if(expr instanceof NKAVariableExpression) {
				stack.push(NKAVariableExpression.class.cast(expr).getValue().getExpr());
			}else if(expr instanceof NKAMathExpression) {
				NKAMathExpression math = NKAMathExpression.class.cast(expr);
				tmp=operations.get(math.getOperator()).apply(solveExpression(math.getLExpr()), solveExpression(math.getRExpr()));
			}			
		}		
		return tmp;
	}
}
