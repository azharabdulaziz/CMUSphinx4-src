package AzharTestPackage;

import java.util.ArrayList;
import java.util.Iterator;

import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.Node;

public class simpleMerge {
	protected final Lattice finalLat;
	
	public simpleMerge(Lattice finalLattice) {
		//super();
		this.finalLat = finalLattice;
	}

	public void Merge(ArrayList<Lattice> latList) {
		Lattice lat1 = latList.get(0);
		addLattice(lat1);
		Node initialNode = lat1.getInitialNode();
		Node terminalNode = lat1.getTerminalNode();
		
		initialNode.setId("0");
		terminalNode.setId("999999");
		
		this.finalLat.setInitialNode(initialNode);
		this.finalLat.setTerminalNode(terminalNode);
		
		for(int i=1;i<latList.size();i++) {
			Lattice lattice = latList.get(i);
			
			lattice.setInitialNode(initialNode);
			
			lattice.setTerminalNode(terminalNode);
		}
		
		
	}

	private void addLattice(Lattice lat1) {
		
		
	}

}
