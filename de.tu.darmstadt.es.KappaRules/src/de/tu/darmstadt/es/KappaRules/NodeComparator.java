package de.tu.darmstadt.es.KappaRules;

import java.util.Comparator;

public class NodeComparator implements Comparator<Node> {

	private NodeComparator() {}
	
	public static Comparator<Node> create(){
		return new NodeComparator();
	}
	
	@Override
	public int compare(Node o1, Node o2) {
		return o1.getIndexOfElement() - o2.getIndexOfElement();
	}

}
