package AzharWork;

import java.io.IOException;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.cmu.sphinx.linguist.dictionary.Word;
import edu.cmu.sphinx.result.Edge;
import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.LatticeOptimizer;
import edu.cmu.sphinx.result.Node;
//import edu.cmu.sphinx.result.SausageMaker;
import edu.cmu.sphinx.result.WordResult;
//import edu.cmu.sphinx.result.AbstractSausageMaker;


public class LatticeTest {



	private static final double[] LL_SPS = {1,0.4,1};

	public static void main(String[] args) throws IOException {
		//WOW, best way to get rid of WARNINGS and info in the log an stop showing them on console
		//System.err.close();
		float languageModelWeightAdjustment = 1;

		String[] label1 = {"A","B"};
		String[] label2 = {"C","D"};
		//String[] label3 = {"GG","HH","II"};

		Lattice lattice1 = CreateLattice(label1,4);
		Lattice lattice2 = CreateLattice(label2,1);
		//Update acoustic score using the SPS estimator
		//lattice1 = UpdateAcousticScore(lattice1, LL_SPS[0]);
		//lattice2 = UpdateAcousticScore(lattice2, LL_SPS[1]);
		//lattice3 = UpdateAcousticScore(lattice3, LL_SPS[2]);

		// Optimization
		//LatticeOptimizer optimizer=new LatticeOptimizer(lattice);
		//optimizer.optimize();

		lattice1.computeNodePosteriors(languageModelWeightAdjustment, true,LL_SPS[0]);
		lattice2.computeNodePosteriors(languageModelWeightAdjustment,true,LL_SPS[1]);

		// Combine results lattices
		System.out.println("Combining Two ");
		Lattice latticeOfTwo = CombineLattice(lattice1, lattice2);
		System.out.println();
		
		System.out.println("Combining Three ");
		Lattice latticeOfThree = CombineLattice(lattice1,latticeOfTwo);
		//latticeOfThree = CorrectMultipleEdges(latticeOfThree);
		
		//System.out.println("Before Computing Node Posterior.........." );
		// DisplayLattice(lattice);
		// Compute posteriori
		//lattice.computeNodePosteriors(languageModelWeightAdjustment);
		//latticeOfTwo.computeNodePosteriors(languageModelWeightAdjustment, true);
		//System.out.println("After Computing Node Posterior.........." );
		// DisplayLattice(latticeOfTwo);

		// Estimate best path using Viterbi Algorithm
		//  System.out.println(latticeOfTwo.getViterbiPath());

		//  Show all paths
		//		System.out.println("All Paths for Fused Lattice are:");
		//	    	System.out.println(latticeOfTwo.allPaths());
		//		System.out.println("All Edges:");
		//		System.out.println(latticeOfTwo.getEdges());
		//		System.out.println("First Edge");
		//		System.out.println(latticeOfTwo.getEdges().iterator().next().getFromNode());

		// Dump lattices
		lattice1.dumpDot("lattice1.dot", "lttice1.dot");
		lattice2.dumpDot("lattice2.dot", "lttice2.dot");

		latticeOfTwo.dumpDot("latticeOfTwo.dot", "ltticeOfTwo.dot");
		latticeOfThree.dumpDot("latticeOfThree.dot", "latticeOfThree.dot");


	}

	public static Lattice CreateLattice(String[] Label,int ids){
		/*
		 * Only give String[] for lattice words not including the start <s> and the end </s>
		 */
		Lattice lattice= new Lattice();
		int k=0;

		int l = Label.length;
		//double idn = 1+Math.random()*100;
		int kk=l;
		long st = 0;
		long et1 = 2*k;
		long et2 = -1;


		String id="";
		String firstID="";
		String lastID = "";
		int i=0;
		for(i=0; i<l;i++){
			id=Integer.toString(ids+1+i);
			if(i==0){
				firstID =  Integer.toString(ids);

			}

			kk=kk-1;
			long beginTime=i+k;
			long endTime=i+k+1;
			k=k+1;
			lattice.addNode(id, Label[i], beginTime, endTime);


		}
		lastID = Integer.toString(i+2*l+1);

		//lattice.addNode(Integer.toString((int) (99999+Math.round(idn))), "</s>", et1,et2);
		//lattice.addNode(Integer.toString((int) (0+Math.round(idn))), "<s>", st, st);
		lattice.addNode(Integer.toString(ids), "<s>", st, st);
		lattice.addNode(lastID, "</s>", et1,et2);


		/*
		 * Adding edges to formulate lattice
		 */

		double score = 1;
		/*double backwardScore=1+Math.random()*100;;
		double forwardScore = 1;
		double posterior = 1+Math.random()*100;
		double viterbiScore = 1;*/

		Iterator<Node> nodes = lattice.getNodes().iterator();

		Node initialNode = nodes.next();
		Node fromNode = initialNode;
		Node currentNode = fromNode;
		while(nodes.hasNext()){
			k=k+1;
			currentNode = nodes.next();

			/*currentNode.setBackwardScore(backwardScore);
			currentNode.setForwardScore(forwardScore);
			currentNode.setPosterior(posterior);
			currentNode.setViterbiScore(viterbiScore);*/

			//System.out.println("Current Node: " + currentNode.getWord());
			double acousticScore = score;
			double lmScore = score;
			lattice.addEdge(fromNode, currentNode, acousticScore, lmScore);
			//System.out.println("From Node " + fromNode.getWord() + " --> " + currentNode.getWord());
			fromNode = currentNode;

		}

		lattice.setInitialNode(initialNode);
		lattice.setTerminalNode(currentNode);



		return lattice;
	}



	public static void DisplayLattice(Lattice lattice){
		System.out.println("Displaying all the nodes");
		Iterator<Node> nodes2 = lattice.getNodes().iterator();
		while(nodes2.hasNext()){
			Node cur = nodes2.next();
			System.out.println("Node(" + cur.getId() + "): " + cur.getWord()); 
			System.out.println("ForwardScore = " + cur.getForwardScore());
			System.out.println("BackwardScore = " + cur.getBackwardScore());
			System.out.println("ViterbiScore = " + cur.getViterbiScore());
			System.out.println("Posterior = " + cur.getPosterior());

		}
	}

	public static void ShowInitialTerminal(Lattice lattice){
		Node init = lattice.getInitialNode();
		Node term = lattice.getTerminalNode();
		System.out.println("Get Initial Node and Terminal Node for the Lattice....");
		System.out.println("Initial Node: " + init);
		System.out.println("Terminal Node:" + term);
	}
	public static Lattice CombineLattice(Lattice lattice1, Lattice lattice2){
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
		fused_lattice = CombineEdges(lattice1,lattice2, fused_lattice);
		    	
		return fused_lattice;	

	}


	
	/*
	 * Add the nodes of two lattices into one lattice
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
	
	private static Lattice CombineEdges(Lattice lattice1, Lattice lattice2, Lattice fused_lattice) {
		// TODO Auto-generated method stub
		Collection<Edge> edges1 = lattice1.getEdges();
		Collection<Edge> edges2 = lattice2.getEdges();
		Iterator<Edge> edge1_iterator = edges1.iterator();
		

		// Add edges to the new fused lattice
		while(edge1_iterator.hasNext()){
			Edge currentedge = edge1_iterator.next();
			fused_lattice.addEdge(currentedge.getFromNode(), currentedge.getToNode(), currentedge.getAcousticScore(), currentedge.getLMScore());
		}

		// Add the second lattice's edges. 
		Iterator<Edge> edge2_iterator = edges2.iterator();
		while(edge2_iterator.hasNext()){
			Edge additionaltedge = edge2_iterator.next();
			fused_lattice.addEdge(additionaltedge.getFromNode(), 
					additionaltedge.getToNode(), additionaltedge.getAcousticScore(), additionaltedge.getLMScore());

		}



		return fused_lattice;
	}


	private static Lattice CorrectMultipleEdges(Lattice lattice) {
		// TODO Remove any multiple edges that exist between two nodes
		Lattice result = lattice;
		Collection<Edge> edges = lattice.getEdges();
		Iterator<Edge> edgeIterator1 = edges.iterator();
		
		while(edgeIterator1.hasNext()) {
			
			int k=0; // counts repetition of similarities
			Edge currentedge = edgeIterator1.next();
			Iterator<Edge> edgeIterator2 = edges.iterator();
			//System.out.println("Current Edge: " + currentedge);
			while(edgeIterator2.hasNext()) {
				Edge Testedge = edgeIterator2.next();
			//	System.out.println("Test edge: " + Testedge);
				if(currentedge.isInLine(Testedge)) {
					k++;
					System.out.println("k= "+ k + ". For edge:" + Testedge);
					if(k>=2) {
						System.out.println("Multiple Edge is at: " + currentedge);
						double AccScore = Testedge.getAcousticScore() + currentedge.getAcousticScore();
						double LMScore = Testedge.getLMScore()  +currentedge.getLMScore();
						
						result.addEdge(currentedge.getFromNode(), currentedge.getToNode(), AccScore, LMScore);
						
					}
				}
			}
			
		}

		return lattice;
	}

	public static Lattice UpdateAcousticScore(Lattice lattice,double LL_SPS){
		Lattice new_lattice = new Lattice();

		// Copy nodes
		Iterator<Node> nodes = lattice.getNodes().iterator();
		while(nodes.hasNext()){
			Node cur = nodes.next();
			new_lattice.addNode(cur.getId(), cur.getWord().toString(), cur.getBeginTime(), cur.getEndTime());
			//System.out.println("Node(" + cur.getId() + ")=" + cur.getWord().toString());
		}

		//Copy initial and terminal nodes
		new_lattice.setInitialNode(lattice.getInitialNode());
		new_lattice.setTerminalNode(lattice.getTerminalNode());
		// Update acoustic scores in edges
		Iterator<Edge> edges = lattice.getEdges().iterator();
		while(edges.hasNext()){
			Edge current_edge = edges.next();
			double AC_score = LL_SPS+current_edge.getAcousticScore();

			new_lattice.addEdge(current_edge.getFromNode(), current_edge.getToNode(), AC_score, current_edge.getLMScore());
			//System.out.println("From Node" + current_edge.getFromNode() + " -->" + current_edge.getToNode());
		}

		return new_lattice;
	}



}
