/*
 * generated by Xtext 2.12.0
 */
package de.tu.darmstadt.es.scoping

import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.scoping.Scopes
import de.tu.darmstadt.es.kappaLite.KLISiteDescription
import de.tu.darmstadt.es.kappaLite.KLIAgentDescription
import de.tu.darmstadt.es.kappaLite.KLISite
import de.tu.darmstadt.es.kappaLite.KLIStateDescription
import de.tu.darmstadt.es.kappaLite.KLIState

/**
 * This class contains custom scoping description.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
 * on how and when to use it.
 */
class KappaLiteScopeProvider extends AbstractKappaLiteScopeProvider {
	override getScope(EObject context, EReference reference) {
		if(context instanceof KLISiteDescription && reference.name === "site"){
			val agentDes = context.eContainer as KLIAgentDescription
			val candidates = EcoreUtil2.getAllContentsOfType(agentDes.agent, KLISite)
			return Scopes.scopeFor(candidates)			
		}
		
		if(context instanceof KLIStateDescription){
			val agentDes = context.eContainer as KLISiteDescription
			val candidates = EcoreUtil2.getAllContentsOfType(agentDes.site, KLIState)
			return Scopes.scopeFor(candidates)			
		}
		
		super.getScope(context,reference)
	}
}
