/*
 * generated by Xtext 2.12.0
 */
package de.tu.darmstadt.es.validation

import org.eclipse.xtext.validation.Check

/**
 * This class contains custom validation rules. 
 *
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
class NeoKappaValidator extends AbstractNeoKappaValidator {
	
	@Check 
	def checkForDanglingConnections()
	{
		
	}
	
	
//	public static val INVALID_NAME = 'invalidName'
//
//	@Check
//	def checkGreetingStartsWithCapital(Greeting greeting) {
//		if (!Character.isUpperCase(greeting.name.charAt(0))) {
//			warning('Name should start with a capital', 
//					KappaLightPackage.Literals.GREETING__NAME,
//					INVALID_NAME)
//		}
//	}
	
}