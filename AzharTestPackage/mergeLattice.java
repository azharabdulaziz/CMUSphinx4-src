/**
 * 
 */
package AzharTestPackage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import edu.cmu.sphinx.linguist.dictionary.Word;
import edu.cmu.sphinx.result.Edge;
import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.Node;
import edu.cmu.sphinx.util.LogMath;

/**
 * @author Azhar Abdulaziz
 * {@docRoot}
 *
 */
public class mergeLattice {
	
	long TerminalBeginTime;
	long initEndTime;
	protected final Lattice mergedLattice;
	protected int currentID;
	protected boolean initialNodeSet; 
	private Map<Integer, Integer> idsLookup;
	
	//private int noOfExistentNodes; 

	public mergeLattice(Lattice mergedLattice) {
		//super();
		this.mergedLattice = mergedLattice;
		this.currentID = 0;
		this.initialNodeSet = false;
		this.idsLookup = new HashMap<>();
		this.TerminalBeginTime = 0;
		this.initEndTime = 0;
		
		//this.noOfExistentNodes = 0;
		
	}
	
	public void Merge(Lattice lattice) {
		/**
		 *  1- Add all nodes of incomming lattice
		 *  Note that only the first lattice's init node is set to be the init node for the final 
		 *  merged lattice. This is controlled by the flag initialNodeSet.
		 *  For all inccoming lattices, the init node is added to the currentNodes list. 
		 */
		//addNodesTest(lattice);
		addNodes(lattice);
		
		//checkLookupTable();
		
		addEdges(lattice);
		
		
	}
	
	public void Merge(Lattice lattice, float snrLogProb) {
		/**
		 *  1- Add all nodes of incomming lattice
		 *  Note that only the first lattice's init node is set to be the init node for the final 
		 *  merged lattice. This is controlled by the flag initialNodeSet.
		 *  For all inccoming lattices, the init node is added to the currentNodes list. 
		 */
		//addNodesTest(lattice);
		addNodes(lattice);
		
		//checkLookupTable();
		
		addEdges(lattice, snrLogProb);
		
		
	}

	private void addEdges(Lattice lattice) {
		
		Collection<Edge> edges = lattice.getEdges();
		Iterator<Edge> iter = edges.iterator();
		while(iter.hasNext()) {
			Edge current_edge = iter.next();
			
			int oldFromNodeID = Integer.parseInt(current_edge.getFromNode().getId());
			int newFromNodeId = this.idsLookup.get(oldFromNodeID);
			//this.idsLookup.remove(oldFromNodeID);
			int oldToNodeID = Integer.parseInt(current_edge.getToNode().getId());
			int newToNodeId = this.idsLookup.get(oldToNodeID);
			//this.idsLookup.remove(oldToNodeID);
			
			Node fromNode= getNodebyId(newFromNodeId);
			Node toNode = getNodebyId(newToNodeId);
			double acousticScore = current_edge.getAcousticScore();
			double lmScore = current_edge.getLMScore();
			
			this.mergedLattice.addEdge(fromNode, toNode, acousticScore, lmScore);
		}
		
		
	}

private void addEdges(Lattice lattice, float snrWeight) {
		LogMath logMath = LogMath.getLogMath();
		Collection<Edge> edges = lattice.getEdges();
		Iterator<Edge> iter = edges.iterator();
		while(iter.hasNext()) {
			Edge current_edge = iter.next();
			
			int oldFromNodeID = Integer.parseInt(current_edge.getFromNode().getId());
			int newFromNodeId = this.idsLookup.get(oldFromNodeID);
			//this.idsLookup.remove(oldFromNodeID);
			int oldToNodeID = Integer.parseInt(current_edge.getToNode().getId());
			int newToNodeId = this.idsLookup.get(oldToNodeID);
			//this.idsLookup.remove(oldToNodeID);
			
			Node fromNode= getNodebyId(newFromNodeId);
			Node toNode = getNodebyId(newToNodeId);
			double acousticScore = logMath.addAsLinear((float)current_edge.getAcousticScore(),snrWeight);
			double lmScore = current_edge.getLMScore();
			
			this.mergedLattice.addEdge(fromNode, toNode, acousticScore, lmScore);
		}
		
		
	}

	private Node getNodebyId(int nodeId) {
		Collection<Node> nodes = this.mergedLattice.getNodes();
		Iterator<Node> iter = nodes.iterator();
		Node foundNode = null;
		while(iter.hasNext()) {
			Node node = iter.next();
			if(Integer.parseInt(node.getId()) == nodeId) {
				//System.out.println("Node " + node.getId()+"  Found");
				foundNode =  node;
			}
			
		}
		//System.out.println("Returned Node: "+ foundNode.getId() );
		return foundNode;
	}

	/**
	 * @deprecated
	 * @param lattice
	 */
	private void addNodesTest(Lattice lattice) {
		int new_id = this.currentID;
		
		/*Node initialNode =lattice.getInitialNode();
		Node TerminalNode  = lattice.getTerminalNode();
		System.out.println("Initial Node: "+ initialNode.getWord());
		System.out.println("Terminal Node: "+ TerminalNode.getWord());
		*/
		Collection<Node> nodes = lattice.getNodes();
		Iterator<Node> iter = nodes.iterator();
		while(iter.hasNext()) {
			Node current_node = iter.next(); 
			int old_id = Integer.parseInt(current_node.getId());
			
			String id = Integer.toString(new_id);
			String word = current_node.getWord().toString();
			long beginTime = current_node.getBeginTime();
			long endTime = current_node.getEndTime();
			this.mergedLattice.addNode(id, word, beginTime, endTime);
			
			// update ids lookup table
			//System.out.println("old_id: " + old_id + "  New_id: "+ new_id);
			this.idsLookup.put(old_id, new_id);
					
			// update new_id
			
			new_id = new_id+1;
			
			
			
		}
		
		// update currentId
		this.currentID = new_id+1;
		
	}

	private void addNodes(Lattice lattice) {
		int new_id = this.currentID;
		
		Node initialNode =lattice.getInitialNode();
		Node TerminalNode  = lattice.getTerminalNode();
		
		
		//System.out.println("Initial Node: "+ initialNode.getWord());
		//System.out.println("Terminal Node: "+ TerminalNode.getWord());
		
		Collection<Node> nodes = lattice.getNodes();
		Iterator<Node> iter = nodes.iterator();
		while(iter.hasNext()) {
			Node current_node = iter.next(); 
			String word = current_node.getWord().toString();
			long beginTime = current_node.getBeginTime();
			long endTime = current_node.getEndTime();
			
			if(current_node.isEquivalent(initialNode)) {
				new_id = 0;
				//System.out.println("Current Node that is equal to init: " + current_node.getWord());
			/*	if(endTime < initEndTime) {
					//new_id = 0;
					initEndTime = endTime;
					
					
				}*/
				
			}
			else if(current_node.isEquivalent(TerminalNode)) {
				new_id = 99999;
				//System.out.println("Current beginTime = "+beginTime + "  TerminalBeginTime = "+ TerminalBeginTime);
				if(beginTime > TerminalBeginTime ) {
					TerminalBeginTime = beginTime;
				}
				else {
					beginTime = TerminalBeginTime;
				}
			}
			
			
			
			int old_id = Integer.parseInt(current_node.getId());
			
			String id = Integer.toString(new_id);
			
			this.mergedLattice.addNode(id, word, beginTime, endTime);
			
			// update ids lookup table
			//System.out.println("old_id: " + old_id + "  New_id: "+ new_id);
			this.idsLookup.put(old_id, new_id);
					
			// update new_id
			
			if(new_id == 0) new_id = this.currentID;
			if(new_id >= 99999) new_id = this.currentID;
			new_id = new_id+1;
			
			
			
		}
		
		// update currentId
		this.currentID = new_id+2;		
		this.mergedLattice.setInitialNode(initialNode);
		this.mergedLattice.setTerminalNode(TerminalNode);
				
	}

	
	@SuppressWarnings({ "unused", "rawtypes" })
	private void checkLookupTable() {
		Set set = this.idsLookup.entrySet();
		Iterator hashSet = set.iterator();
		while(hashSet.hasNext()) {
			Map.Entry me = (Map.Entry) hashSet.next();
	         System.out.print("Old_id:  " + me.getKey() + "  ===>  ");
	         System.out.println(me.getValue() + " New_id");
			
		}
		
	
	}
	
	
}
