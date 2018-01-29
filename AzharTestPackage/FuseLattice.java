package AzharTestPackage;
/**
 * This class extract textual results from an array of lattices, 
 * each element represents a final result lattice from certain noisy AM.
 * The steps are: 
 * 1- Fuse the lattices produced by the each noisy decoder. 
 * Links that correspond to the same word are combined, so that their posteriors and acoustic model scores are added and the graph topology is updated.
 * Time overlap is not considered in this method.   
 * 2- Estimate the posteriori probability of the comnbined lattice (it is neccesary for the next step)
 * 3- Retrieve the MAP path using Verbi search.
 * @author Azhar S Abdulaziz
 * @since 2017
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.cmu.sphinx.result.Edge;
import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.Node;
import edu.cmu.sphinx.util.LogMath;

public class FuseLattice {
	

	public Lattice getLattice(String fileName) throws IOException {
		
		Lattice lattice = Lattice.readhtk(fileName);
		//lattices.add(lattice);
		return lattice;

	}
	
	public static String getFusedResutlNoScaleAM(ArrayList<Lattice> lattices) {
		// Combine lattices
		Lattice finalLattice = new Lattice();
		Iterator<Lattice> latIter = lattices.iterator();

		Lattice l1 = latIter.next();
		Lattice l2 = latIter.next();
//		Lattice l3 = latIter.next();
//		Lattice l4 = latIter.next();
//		Lattice l5 = CombineNoScale(l1, l2);
//		Lattice l6 = CombineNoScale(l3, l4);

		finalLattice = CombineNoScale(l1, l2);
		finalLattice = NormalizeLattice(finalLattice);
		float lmWeight = 1;
		String finalTextResult = getTextResultFromLattice(finalLattice,lmWeight);
		return finalTextResult;

	}
	
	
	/**
	 * 
	 * @param lattices
	 * @param alpha1
	 * @param alpha2
	 * @return
	 */
	public static String getFusedResutlScaleAM(ArrayList<Lattice> lattices, double[] snrLogProb) {
		// The final combined lattice
		Lattice finalLattice = new Lattice();
		// prepare input SNR logProb
		double alpha1 = snrLogProb[0];
		double alpha2 = snrLogProb[1];
		double alpha3 = snrLogProb[2];
		double alpha4 = snrLogProb[3];
		
		// prepare input lattices
		Iterator<Lattice> latIter = lattices.iterator();
		Lattice l1 = latIter.next();  // from AM10
		Lattice l2 = latIter.next();  // from AM15
		Lattice l3 = latIter.next();  // from AM20
		Lattice l4 = latIter.next();  // from AM_Clean
		
		Lattice l5 = CombineScaledAM(l1, l2, alpha1, alpha2);
		Lattice l6 = CombineScaledAM(l3, l4,alpha3,alpha4);

		finalLattice = CombineNoScale(l5, l6);
		finalLattice = NormalizeLattice(finalLattice);
		float lmWeight = 1;
		String finalTextResult = getTextResultFromLattice(finalLattice,lmWeight);
		return finalTextResult;

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
		LogMath logMath = LogMath.getLogMath();
		// Scle Alphas to logBase
		float newalpha1 = logMath.log10ToLog((float) alpha1);
		float newalpha2 = logMath.log10ToLog((float) alpha2);
		
		Collection<Edge> edges1 = lattice1.getEdges();
		Collection<Edge> edges2 = lattice2.getEdges();
		Iterator<Edge> edge1_iterator = edges1.iterator();
		

		// Add edges to the new fused lattice
		while(edge1_iterator.hasNext()){
			Edge currentedge = edge1_iterator.next();
			double newAMScore = logMath.addAsLinear((float)alpha1,(float)currentedge.getAcousticScore());
			fused_lattice.addEdge(currentedge.getFromNode(), currentedge.getToNode(),
					newAMScore , currentedge.getLMScore());
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
					double newAMScore = logMath.addAsLinear(newalpha2 ,(float)CurrentEdge.getAcousticScore());
					//float scaled = (float) (CurrentEdge.getAcousticScore()*alpha2);
					accScore = logMath.addAsLinear((float)e1.getAcousticScore(), (float)newAMScore );
					lmScore = logMath.addAsLinear((float)e1.getLMScore(), (float)CurrentEdge.getLMScore());
					//accScore = e1.getAcousticScore() + alpha2*CurrentEdge.getAcousticScore();
					//lmScore = e1.getLMScore() + CurrentEdge.getLMScore();
					
				}
				
			}
			
			if(flag) {
				//If the edge exist update scores only
				fused_lattice.updateEdge(CurrentEdge,accScore, lmScore);
			}
			
			//Esle, add the new edge
			else {
				double newAMScore = logMath.addAsLinear(newalpha2 ,(float)CurrentEdge.getAcousticScore());
				fused_lattice.addEdge(CurrentEdge.getFromNode(), 
						CurrentEdge.getToNode(), newAMScore, CurrentEdge.getLMScore());

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
					LogMath x = LogMath.getLogMath();
					float currentAMScore = (float) (CurrentEdge.getAcousticScore());
					accScore = x.addAsLinear((float)e1.getAcousticScore(), currentAMScore );
					lmScore = x.addAsLinear((float)e1.getLMScore(), (float)CurrentEdge.getLMScore());
					
					//accScore = e1.getAcousticScore() - CurrentEdge.getAcousticScore();
					//lmScore = e1.getLMScore() - CurrentEdge.getLMScore();
					
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


	private static Lattice NormalizeLattice(Lattice lattice) {
		LogMath logMath = LogMath.getLogMath();
		//Lattice normLattice = new Lattice();
		float totalAmScore = getTotalAMscore(lattice,logMath);
		
		Collection<Edge> edges = lattice.getEdges();
		Iterator<Edge> iter = edges.iterator();
		
		while(iter.hasNext()) {
			Edge currentEdge = iter.next();
			double inLinScore = logMath.logToLinear((float)currentEdge.getAcousticScore());
			double normLinScore = logMath.linearToLog(inLinScore/totalAmScore);
			lattice.updateEdge(currentEdge, normLinScore, currentEdge.getLMScore());
		}
		
		return lattice;
	}

	private static float getTotalAMscore(Lattice lattice, LogMath logMath) {
		float totalAmScore = 0;
		Collection<Edge> edges = lattice.getEdges();
		Iterator<Edge> iter = edges.iterator();
		
		while(iter.hasNext()) {
			Edge edge = iter.next();
			totalAmScore = logMath.addAsLinear((float)edge.getAcousticScore(), totalAmScore);
		}
		return totalAmScore;
	}


	
}
