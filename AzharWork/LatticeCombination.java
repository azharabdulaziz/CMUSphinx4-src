package AzharWork;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.opencsv.CSVReader;

import edu.cmu.sphinx.api.*;
import edu.cmu.sphinx.result.Edge;
import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.LatticeOptimizer;
import edu.cmu.sphinx.result.Node;
import edu.cmu.sphinx.result.WordResult;


public class LatticeCombination {
	
	static String DirectoryPath = "/Users/Azhar/Desktop/Exp1/timit/wav/";
	//static String fileName = "TEST/DR1/FAKS0/SI2203"; 
	static String fileName2 = "test/DR4/FEDW0/SI1653"; // He spoke soothingly.
	
	// Define acoustic models
	static String AcModelClean ="/Users/Azhar/Desktop/TIMIT_AM/timit_Clean.cd_cont_200/";
	static String AcModel20 ="/Users/Azhar/Desktop/TIMIT_AM/timit_20dB.cd_cont_200/";
	static String AcModel15 ="/Users/Azhar/Desktop/TIMIT_AM/timit_15dB.cd_cont_200/";
	static String AcModel10 ="/Users/Azhar/Desktop/TIMIT_AM/timit_10dB.cd_cont_200/";
	
	static String[] AcModel = {AcModelClean, AcModel20,AcModel15,AcModel10};
	
	static String speechFilePath = DirectoryPath+ fileName2+".WAV";
	static String textFilePath = DirectoryPath+ fileName2+".TXT";
	 
    public static void main(String[] args) throws IOException {
    	//WOW, best way to get rid of WARNINGS and info in the log an stop showing them on console
    			System.err.close();
    	
    	System.out.println("Speech file Abs Path: " + speechFilePath);
        
        System.out.println("Reference Utterance: He spoke soothingly.");
      
        Configuration config = new Configuration();
		ConfigSetup conf = new ConfigSetup();
		SpeechResult result10dB = recog(config,conf,AcModel[3],speechFilePath);
		
			System.out
					.println("Result from AM_10dB is:" + result10dB.getResult()); 
			SpeechResult result15dB = recog(config,conf,AcModel[2],speechFilePath);
			
			System.out
			.println("Result from AM_15dB is:" + result15dB.getResult()); 
	      // Building the combination of two AMs
			Lattice lattice10dB = new Lattice(result10dB.getResult());
	    	Lattice lattice15dB = new Lattice(result15dB.getResult());
	    	double[] LL_SPS = {-1e4,-2e4};
	    	Lattice new_lattice = CombineLattice(lattice10dB, lattice15dB, LL_SPS);
	    	System.out.println("Finished ........");
	    	lattice10dB.dumpDot("lattice10dB.dot", "lattice10dB.dot");
	    	lattice15dB.dumpDot("lattice15dB.dot", "lattice15dB.dot");
	    	new_lattice.dumpDot("new_Lattice.dot", "New_lattice.dot");
	    			
			
			
		}
    
    public static SpeechResult recog(Configuration config,ConfigSetup conf, String AcModel, String speechFilePath) throws IOException{

		StreamSpeechRecognizer recognizer = conf.BuildRecognizer(AcModel, config);
		
		
		// Start recognition
		recognizer.startRecognition(new FileInputStream(speechFilePath));
		SpeechResult result = recognizer.getResult();
		// Stop recognition
     	recognizer.stopRecognition();
     return result;
	
    }
    
    public static Lattice CombineLattice(Lattice lattice1, Lattice lattice2, double[] LL_SPS){
    	Lattice fused_lattice = new Lattice();
    	
    	Node initialNode = lattice1.getInitialNode();
    	Node terminalNode = lattice1.getTerminalNode();
    	Collection<Edge> edges1 = lattice1.getEdges();
    	Collection<Node> nodes1 = lattice1.getNodes();
    	
    	Collection<Edge> edges2 = lattice2.getEdges();
    	Collection<Node> nodes2 = lattice2.getNodes();
    	
    	fused_lattice.setInitialNode(initialNode);
    	fused_lattice.setTerminalNode(terminalNode);

    	Iterator<Node> node1_iterator = nodes1.iterator();
    	Iterator<Edge> edge1_iterator = edges1.iterator();
    	Iterator<Node> node2_iterator = nodes2.iterator();
    	Iterator<Edge> edge2_iterator = edges2.iterator();

    	while(node1_iterator.hasNext()){
    		Node current_node = node1_iterator.next();
    		fused_lattice.addNode(current_node.getId(), current_node.getWord().toString(), current_node.getBeginTime(), current_node.getEndTime());
    	}
    	
    	while(edge1_iterator.hasNext()){
    		Edge current_edge = edge1_iterator.next();
    		double AC_score = LL_SPS[0]+current_edge.getAcousticScore();
    		
    		fused_lattice.addEdge(current_edge.getFromNode(), current_edge.getToNode(), AC_score, current_edge.getLMScore());
    	}
	
    	while(node2_iterator.hasNext()){
    		Node current_node = node2_iterator.next();
    		fused_lattice.addNode(current_node.getId(), current_node.getWord().toString(), current_node.getBeginTime(), current_node.getEndTime());
    	}
    	while(edge2_iterator.hasNext()){
    		Edge current_edge = edge2_iterator.next();
    		double AC_score = LL_SPS[1]+current_edge.getAcousticScore();
    		
    		fused_lattice.addEdge(current_edge.getFromNode(), current_edge.getToNode(), AC_score, current_edge.getLMScore());
    	}
    
    return fused_lattice;	

    }
    
         
}
