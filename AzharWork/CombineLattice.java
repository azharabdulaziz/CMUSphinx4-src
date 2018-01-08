/**
 * 
 */
package AzharWork;


import java.util.Collection;
import java.util.Iterator;

import edu.cmu.sphinx.result.Edge;
import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.Node;

/**
 * <p>
 * This class contians combine public methods, which is used to fuse two different lattices. 
 *  
 *  </p>
 * @author Azhar Abdulaziz
 * @since November, 2017
 */
public class CombineLattice {

	/**
	 * This method combines two different lattices by fusing the nodes of both of them into a single lattice.
	 * Nodes should not be repeated and there will be no parallel edges (also known as paths) in the new fused lattice. 
	 * This is usefull when two or more results of recognizers are combined together. 
	 *  
	 * @param lattice1
	 * @param lattice2
	 * @return fused_lattice
	 * @param alpha1
	 * @param alpha2
	 * 
	 * @author Azhar Abdulaziz
	 * 
	 */
	
	public static Lattice CombineScaledAM(Lattice lattice1, Lattice lattice2, double alpha1, double alpha2){
		Lattice fused_lattice = new Lattice();

		Node initialNode = lattice1.getInitialNode();
		Node terminalNode = lattice1.getTerminalNode();
		// Change the start and end words id <s> and </s>
		initialNode.setId(Integer.toString(0));
		terminalNode.setId(Integer.toString(999999));

		Node initialNode2 = lattice2.getInitialNode();
		Node terminalNode2 = lattice2.getTerminalNode();
		// Change the start and end words id <s> and </s>
		initialNode2.setId(Integer.toString(0));
		terminalNode2.setId(Integer.toString(999999));
		fused_lattice.setInitialNode(initialNode);
		fused_lattice.setTerminalNode(terminalNode);
		
		/*
		 * Add Nodes
		 */
		fused_lattice = CombineNodes(lattice1,lattice2,fused_lattice);
		
		/*
		 * Add Edges
		 */
		fused_lattice = CombineEdgesScaleAM(lattice1,lattice2, alpha1,alpha2,fused_lattice);
		    	
		return fused_lattice;	

	}


	/**
	 * 
	 * @param lattice1
	 * @param lattice2
	 * @return
	 */
	public static Lattice CombineNoScale(Lattice lattice1, Lattice lattice2){
		Lattice fused_lattice = new Lattice();

		Node initialNode = lattice1.getInitialNode();
		Node terminalNode = lattice1.getTerminalNode();
		// Change the start and end words id <s> and </s>
		initialNode.setId(Integer.toString(0));
		terminalNode.setId(Integer.toString(999999));

		Node initialNode2 = lattice2.getInitialNode();
		Node terminalNode2 = lattice2.getTerminalNode();
		// Change the start and end words id <s> and </s>
		initialNode2.setId(Integer.toString(0));
		terminalNode2.setId(Integer.toString(999999));
		fused_lattice.setInitialNode(initialNode);
		fused_lattice.setTerminalNode(terminalNode);
		
		/*
		 * Add Nodes
		 */
		fused_lattice = CombineNodes(lattice1,lattice2,fused_lattice);
		
		/*
		 * Add Edges
		 */
		fused_lattice = CombineEdgesNoScale(lattice1,lattice2,fused_lattice);
		    	
		return fused_lattice;	

	}


	
/**
 * 
 * @param lattice1
 * @param lattice2
 * @param fused_lattice
 * @return
 */
	private static Lattice CombineNodes(Lattice lattice1, Lattice lattice2,Lattice fused_lattice) {
		// TODO Auto-generated method stub
		
		Collection<Node> nodes1 = lattice1.getNodes();
		Collection<Node> nodes2 = lattice2.getNodes();
		
		Iterator<Node> node1_iterator = nodes1.iterator();
		Iterator<Node> node2_iterator = nodes2.iterator();
		
		// Add Nodes to the new fused lattice
		while(node1_iterator.hasNext()){
			Node current_node = node1_iterator.next();
			// Change the start and end words id <s> and </s>
			if(current_node.getWord().isSentenceStartWord()){
				current_node.setId(Integer.toString(0));
			}
			if(current_node.getWord().isSentenceEndWord()){
				current_node.setId(Integer.toString(999999));
			}

			fused_lattice.addNode(current_node.getId(), current_node.getWord().toString(), current_node.getBeginTime(), current_node.getEndTime());
		}

		while(node2_iterator.hasNext()){
			Node current_node = node2_iterator.next();
			// Change the start and end words id <s> and </s>
			if(current_node.getWord().isSentenceStartWord()){
				current_node.setId(Integer.toString(0));
			}
			if(current_node.getWord().isSentenceEndWord()){
				current_node.setId(Integer.toString(999999));
			}


			fused_lattice.addNode(current_node.getId(), current_node.getWord().toString(), current_node.getBeginTime(), current_node.getEndTime());
		}


		return fused_lattice;
	}
	
	/*
	 * Combine two lattices edges
	 */
	
	private static Lattice CombineEdgesScaleAM(Lattice lattice1, Lattice lattice2, double alpha1, double alpha2, Lattice fused_lattice) {
		// TODO Auto-generated method stub
		Collection<Edge> edges1 = lattice1.getEdges();
		Collection<Edge> edges2 = lattice2.getEdges();
		Iterator<Edge> edge1_iterator = edges1.iterator();
		

		// Add edges to the new fused lattice
		while(edge1_iterator.hasNext()){
			Edge currentedge = edge1_iterator.next();
			fused_lattice.addEdge(currentedge.getFromNode(), currentedge.getToNode(),
					alpha1*currentedge.getAcousticScore(), currentedge.getLMScore());
		}

		 
		/*
		 * Add the second lattice's edges.
		 * The flag is used to detect existent edge before adding it.
		 */
		
		
		Iterator<Edge> edge2_iterator = edges2.iterator();
		while(edge2_iterator.hasNext()){
			boolean flag=false;
			double accScore = 0;
			double lmScore = 0;
			Collection<Edge> es1 = lattice1.getEdges();
			Iterator<Edge> e1_iteraotr = es1.iterator();
			
			Edge CurrentEdge = edge2_iterator.next();
			
			while(e1_iteraotr.hasNext()) {
				Edge e1 = e1_iteraotr.next();
				if(CurrentEdge.isParallel(e1)) {
					flag = true; 
					accScore = e1.getAcousticScore() + alpha2*CurrentEdge.getAcousticScore();
					lmScore = e1.getLMScore() + CurrentEdge.getLMScore();
					
				}
				
			}
			
			if(flag) {
				//If the edge exist update scores only
				fused_lattice.updateEdge(CurrentEdge,accScore, lmScore);
			}
			
			//Esle, add the new edge
			else {
				fused_lattice.addEdge(CurrentEdge.getFromNode(), 
						CurrentEdge.getToNode(), alpha2*CurrentEdge.getAcousticScore(), CurrentEdge.getLMScore());

			}
			
		}

		return fused_lattice;
	}
	
	
	
	private static Lattice CombineEdgesNoScale(Lattice lattice1, Lattice lattice2, Lattice fused_lattice) {
		// TODO Auto-generated method stub
		Collection<Edge> edges1 = lattice1.getEdges();
		Collection<Edge> edges2 = lattice2.getEdges();
		Iterator<Edge> edge1_iterator = edges1.iterator();
		

		// Add edges to the new fused lattice
		while(edge1_iterator.hasNext()){
			Edge currentedge = edge1_iterator.next();
			fused_lattice.addEdge(currentedge.getFromNode(), currentedge.getToNode(),currentedge.getAcousticScore(), currentedge.getLMScore());
		}

		 
		/*
		 * Add the second lattice's edges.
		 * The flag is used to detect existent edge before adding it.
		 */
		
		
		Iterator<Edge> edge2_iterator = edges2.iterator();
		while(edge2_iterator.hasNext()){
			boolean flag=false;
			double accScore = 0;
			double lmScore = 0;
			Collection<Edge> es1 = lattice1.getEdges();
			Iterator<Edge> e1_iteraotr = es1.iterator();
			
			Edge CurrentEdge = edge2_iterator.next();
			
			while(e1_iteraotr.hasNext()) {
				Edge e1 = e1_iteraotr.next();
				if(CurrentEdge.isParallel(e1)) {
					flag = true; 
					accScore = e1.getAcousticScore() - CurrentEdge.getAcousticScore();
					lmScore = e1.getLMScore() - CurrentEdge.getLMScore();
					
				}
				
			}
			
			if(flag) {
				//If the edge exist update scores only
				fused_lattice.updateEdge(CurrentEdge,accScore, lmScore);
			}
			
			//Esle, add the new edge
			else {
				fused_lattice.addEdge(CurrentEdge.getFromNode(), 
						CurrentEdge.getToNode(), CurrentEdge.getAcousticScore(), CurrentEdge.getLMScore());

			}
			
		}

	return fused_lattice;
	}

	
}