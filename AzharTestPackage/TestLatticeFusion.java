package AzharTestPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.cmu.sphinx.linguist.dictionary.Word;
import edu.cmu.sphinx.result.Edge;
import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.LatticeOptimizer;
import edu.cmu.sphinx.result.Node;
import edu.cmu.sphinx.util.LogMath;


public class TestLatticeFusion {

	public static void main(String[] args) throws IOException {
		
		String[] label1 = {"hell","or","wood"};
		String[] label2 = {"hallo","word"};
		String[] label3 = {"how", "word"};
		String[] label4 = {"how", "all","yard"};
		

		Lattice lattice4 = CreateLattice(label4,1);
		Lattice lattice1 = CreateLattice(label3,1);
		Lattice lattice2 = CreateLattice(label1,1);
		Lattice lattice3 = CreateLattice(label2,1);
		
		
		// merge lattices
		Lattice fusedLattice = new Lattice();
		mergeLattice ml = new mergeLattice(fusedLattice);
		ml.Merge(lattice1);
		ml.Merge(lattice2);
		ml.Merge(lattice3);
		ml.Merge(lattice4);
		
		System.out.println("Result: "+getTextResultFromLattice(fusedLattice, 1.0f));
		
		fusedLattice.dumpDot("FuseLAtticeTest.dot","FuseLAttice Testing My algorithm" );
		lattice1.dumpDot("Lattice1.dot", "TestLAtticeFusion");
		lattice2.dumpDot("Lattice2.dot", "TestLAtticeFusion");
		lattice3.dumpDot("Lattice3.dot", "TestLAtticeFusion");
	
	}
	
	

	/**
	 * 
	 * @param finalLattice
	 * @param lmWeight
	 * @return
	 */
	public static String getTextResultFromLattice(Lattice finalLattice, float lmWeight) {
		finalLattice.computeNodePosteriors(lmWeight);
		List<Node> nodes = finalLattice.getViterbiPath();
		Iterator<Node> nodeItr = nodes.iterator();
		String textResult="";
		while(nodeItr.hasNext()) {
			textResult = textResult + nodeItr.next().getWord()+" ";
		}
		
		String finalTextResult = textResult.replace("<s>", "");
		finalTextResult = finalTextResult.replace("</s>","");
		finalTextResult = finalTextResult.replace("<sil>","");
		finalTextResult = finalTextResult.trim().replaceAll(" +", " ");
		finalTextResult = finalTextResult.trim();
		//System.out.println("Final Text: " + finalTextResult);

		return finalTextResult;
	}

	public static Lattice FuseLattices(Lattice lattice1,Lattice lattice2) {
		Lattice fusedLattice = new Lattice();
		ArrayList<Node> allNodes = getAllNodes(lattice1, lattice2);
		ArrayList<Edge> allEdges = getAllEdges(lattice1, lattice2);
		
		Node initialNode = lattice1.getInitialNode();
		Node terminalNode = lattice1.getTerminalNode();
		initialNode.setId(Integer.toString(0));
		terminalNode.setId(Integer.toString(999999));
		fusedLattice.setInitialNode(initialNode );
		fusedLattice.setTerminalNode(terminalNode );
		
		
		System.out.println(" Fused Lattice nodes:");
		Iterator<Node> niter = allNodes.iterator();
		int new_id = 1;
		while(niter.hasNext()) {
			
			Node currentNode = niter.next();
			//String id = currentNode.getId();
			String word = currentNode.getWord().toString();
			long beginTime = currentNode.getBeginTime();
			long endTime = currentNode.getEndTime();
			fusedLattice.addNode(Integer.toString(new_id), word, beginTime, endTime);
			System.out.println("Node(" + Integer.toString(new_id) + "): " + word);
			new_id++;
		}
		
		
		
		System.out.println(" Fused Lattice Edges:");
		Iterator<Edge> Eiter = allEdges.iterator();
		while(Eiter.hasNext()) {
			Edge currentEdge = Eiter.next();
			Node fromNode = currentEdge.getFromNode();
			Node toNode = currentEdge.getToNode();
			double acousticScore = currentEdge.getAcousticScore();
			double lmScore = currentEdge.getLMScore();
			fusedLattice.addEdge(fromNode, toNode, acousticScore, lmScore);
			System.out.println("EDGE: " + fromNode.toString() + "--> " + toNode.toString());
		}
		
		return fusedLattice;
	}
	
	
	
	
	public static Lattice CreateLattice(String[] Label,int ids){
		/*
		 * Only give String[] for lattice words not including the start <s> and the end </s>
		 */
		LogMath logMath = LogMath.getLogMath();
		Lattice lattice= new Lattice();
		int k=0;

		int l = Label.length;
		//double idn = 1+Math.random()*100;
		int kk=l;
		long st = 0;
		//long et1 = 5;
		long et2 = -1;
		long endTime = 0;

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
			endTime=i+k+1;
			k=k+1;
			lattice.addNode(id, Label[i], beginTime, endTime);


		}
		lastID = Integer.toString(i+2*l+1);

		//lattice.addNode(Integer.toString((int) (99999+Math.round(idn))), "</s>", et1,et2);
		//lattice.addNode(Integer.toString((int) (0+Math.round(idn))), "<s>", st, st);
		lattice.addNode(Integer.toString(ids), "<s>", st, st);
		lattice.addNode(lastID, "</s>", endTime+1,et2);


		/*
		 * Adding edges to formulate lattice
		 */

		
		
		/*double backwardScore=1+Math.random()*100;;
		double forwardScore = 1;
		double posterior = 1+Math.random()*100;
		double viterbiScore = 1;*/

		Iterator<Node> nodes = lattice.getNodes().iterator();

		Node initialNode = nodes.next();
		Node fromNode = initialNode;
		Node currentNode = fromNode;
		while(nodes.hasNext()){
			double acousticScore = logMath.linearToLog(Math.random());
			double lmScore = logMath.linearToLog(Math.random());
			
			k=k+1;
			currentNode = nodes.next();

			/*currentNode.setBackwardScore(backwardScore);
			currentNode.setForwardScore(forwardScore);
			currentNode.setPosterior(posterior);
			currentNode.setViterbiScore(viterbiScore);*/

			//System.out.println("Current Node: " + currentNode.getWord());
			//double acousticScore = score;
			//double lmScore = score;
			lattice.addEdge(fromNode, currentNode, acousticScore, lmScore);
			//System.out.println("From Node " + fromNode.getWord() + " --> " + currentNode.getWord());
			fromNode = currentNode;

		}

		lattice.setInitialNode(initialNode);
		lattice.setTerminalNode(currentNode);

		


		return lattice;
	}

	
	private static ArrayList<Node> getAllNodes(Lattice lattice1,Lattice lattice2) {
		Lattice fused_lattice  = new Lattice();
		ArrayList<Node> ListOfNodes  = new ArrayList<Node>();
		Collection<Node> nodes1 = lattice1.getNodes();
		Collection<Node> nodes2 = lattice2.getNodes();
		
		Iterator<Node> node1_iterator = nodes1.iterator();
		while(node1_iterator.hasNext()){
			Node current_node = node1_iterator.next();
			// Ignore the start and end words id <s> and </s>
			/*Word currentWord = current_node.getWord();
			if(!(currentWord.isSentenceStartWord() || currentWord.isSentenceEndWord())){
				ListOfNodes.add(current_node);
			}*/
			ListOfNodes.add(current_node);
		}


		Iterator<Node> node2_iterator = nodes2.iterator();
		while(node2_iterator.hasNext()){
			Node current_node = node2_iterator.next();
			// Ignore the start and end words id <s> and </s>
			/*Word currentWord = current_node.getWord();
			if(!(currentWord.isSentenceStartWord() || currentWord.isSentenceEndWord())){
				ListOfNodes.add(current_node);
			}*/
			
			ListOfNodes.add(current_node);

			//fused_lattice.addNode(current_node.getId(), current_node.getWord().toString(), current_node.getBeginTime(), current_node.getEndTime());
			//System.out.println("Added Node(" + current_node.getId()+"): "+current_node.getWord().toString());
			
		}


		
		return ListOfNodes;
	}
	
	
	private static ArrayList<Edge> getAllEdges(Lattice lattice1,Lattice lattice2) {
		ArrayList<Edge> ListOfEdges  = new ArrayList<Edge>();
		Collection<Edge> edges1 = lattice1.getEdges();
		Collection<Edge> edges2 = lattice2.getEdges();
		
		Iterator<Edge> edge1_iterator = edges1.iterator();
		while(edge1_iterator.hasNext()){
			Edge current_edge = edge1_iterator.next();

			// Ignore the start and end words id <s> and </s>
			/*Word fromWord = current_edge.getFromNode().getWord();
			Word currentWord = current_edge.getToNode().getWord();
			if(!(fromWord.isSentenceStartWord() || currentWord.isSentenceEndWord())){
				ListOfNodes.add(current_edge);
			}*/
			
			ListOfEdges.add(current_edge);
		}


		Iterator<Edge> edge2_iterator = edges2.iterator();
		while(edge2_iterator.hasNext()){
			Edge current_edge = edge2_iterator.next();

			// Ignore the start and end words id <s> and </s>
			/*Word fromWord = current_edge.getFromNode().getWord();
			Word currentWord = current_edge.getToNode().getWord();
			if(!(fromWord.isSentenceStartWord() || currentWord.isSentenceEndWord())){
				ListOfNodes.add(current_edge);
			}*/
			
			ListOfEdges.add(current_edge);
		}

			//fused_lattice.addNode(current_node.getId(), current_node.getWord().toString(), current_node.getBeginTime(), current_node.getEndTime());
			//System.out.println("Added Node(" + current_node.getId()+"): "+current_node.getWord().toString());

		return ListOfEdges;
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




}
