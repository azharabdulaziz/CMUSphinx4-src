/**
 * 
 */
package AzharTestPackage;

import java.util.Collection;
import java.util.Iterator;

import edu.cmu.sphinx.result.Edge;
import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.Node;

/**
 * @author Azhar
 *
 */
public class mergeLattice {
	
	protected final Lattice mergedLattice;
	protected final int nodeId = 0;

	public mergeLattice(Lattice mergedLattice) {
		//super();
		this.mergedLattice = mergedLattice;
	}
	
	public void MergeLattice(Lattice lattice) {
		
		setInitialNode(lattice);
		addLattice(lattice);
		setTerminalNode(lattice);
	}

	private void addLattice(Lattice lattice) {
		
		Node currentNode = lattice.getInitialNode();
		
		goDeeper(currentNode);
		
	}

	private void goDeeper(Node currentNode) {
		Collection<Edge> leavingEdges = currentNode.getCopyOfLeavingEdges();
		Iterator<Edge> iter = leavingEdges.iterator();
		while(iter.hasNext()) {
			Edge edge = iter.next();
			Node node = edge.getToNode();
	
			Collection<Edge> allentering = node.getCopyOfEnteringEdges();
			
			String id = node.getId();
			String currentword = node.getWord().toString();
			long currentbeginTime = node.getBeginTime();
			long currentendTime = node.getEndTime();
			
			/*fusedLattice.addNode(id, currentword, currentbeginTime, currentendTime);
			double acousticScore = edge.getAcousticScore();
			double lmScore = edge.getLMScore();
			fusedLattice.addEdge(node, finNode, acousticScore, lmScore);*/
		}
	
		
	}

	private void setTerminalNode(Lattice lattice) {
		
		Node finNode = lattice.getTerminalNode();
		long TbeginTime = finNode.getBeginTime();
		long TendTime = finNode.getEndTime();
		String word = "</s>";
		finNode.setId(word);
		mergedLattice.addNode(Integer.toString(99999), word, TbeginTime, TendTime);
		mergedLattice.setTerminalNode(finNode);
		
	}

	private void setInitialNode(Lattice lattice) {
		Node initNode = lattice.getInitialNode();
		long beginTime = 0;
		long endTime = 0;
		initNode.setBeginTime(beginTime);
		initNode.setEndTime(endTime);
		String word = "<s>";
		initNode.setId(word);
		mergedLattice.addNode(Integer.toString(0), word, beginTime, endTime);
		mergedLattice.setInitialNode(initNode);
		
	}
	
	

}
