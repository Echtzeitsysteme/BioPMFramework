package de.tu.darmstadt.es.biochemicalSimulationFramework.internal.splitter;

import de.tu.darmstadt.es.biochemicalSimulationFramework.splitter.Splitter;

public class HybridSplitter extends Splitter  {
	private Splitter otherSplitter;
	
	public HybridSplitter(Splitter other) {
		otherSplitter = other;
	}
	
	public Splitter getOtherSplitter() {
		return otherSplitter;
	}

}
