package AzharTestPackage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import edu.cmu.sphinx.linguist.dictionary.Word;
import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.Node;

public class FuseLattices {
	protected Lattice fusedLattice;
	protected Lattice lattice1;
	protected Lattice lattice2;
	private ArrayList<Node> ListOfNodes  = new ArrayList<Node>();
	
	public FuseLattices(Lattice fusedLattice, Lattice lattice1, Lattice lattice2) {
		//super();
		this.fusedLattice = fusedLattice;
		this.lattice1 = lattice1;
		this.lattice2 = lattice2;
	}
	
	public void Fuse() {
		CombineNodes();
	}
	
	protected void CombineNodes() {
		Collection<Node> nodes1 = lattice1.getNodes();
		
		Iterator<Node> node1_iterator = nodes1.iterator();
		while(node1_iterator.hasNext()){
			Node current_node = node1_iterator.next();
			ListOfNodes.add(current_node);
			//fusedLattice.addNode(current_node.getId(), current_node.getWord().toString(), current_node.getBeginTime(), current_node.getEndTime());
			//System.out.println("Added Node(" + current_node.getId()+"): "+ current_node.getWord().toString());
		}

		Collection<Node> nodes2 = lattice2.getNodes();
		Iterator<Node> node2_iterator = nodes2.iterator();
		while(node2_iterator.hasNext()){
			Node current_node = node2_iterator.next();
			ListOfNodes.add(current_node);
			//fusedLattice.addNode(current_node.getId(), current_node.getWord().toString(), current_node.getBeginTime(), current_node.getEndTime());
			//System.out.println("Added Node(" + current_node.getId()+"): "+current_node.getWord().toString());
			
		}


		Iterator<Node> nodes = ListOfNodes.iterator();
		while(nodes.hasNext()) {
			Node currentNode = nodes.next();
			String id = currentNode.getId();
			String word = currentNode.getWord().toString();
			long beginTime = currentNode.getBeginTime();
			long endTime = currentNode.getEndTime();
			fusedLattice.addNode(id, word, beginTime, endTime);
		}
		//return fusedLattice;
	}
	

	

}
