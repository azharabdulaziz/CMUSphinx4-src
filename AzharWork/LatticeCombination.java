package AzharWork;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.opencsv.CSVReader;

import edu.cmu.sphinx.api.*;
import edu.cmu.sphinx.result.Edge;
import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.LatticeOptimizer;
import edu.cmu.sphinx.result.Nbest;
import edu.cmu.sphinx.result.Node;
import edu.cmu.sphinx.result.WordResult;


public class LatticeCombination {
	

	static String WavePath = "/Users/Azhar/Desktop/Exp1/timit/wav/";
	//static String fileName = "TEST/DR1/FAKS0/SI2203"; 
	static String fileName2 = "test/DR4/FEDW0/SI1653"; // He spoke soothingly.

	// Define acoustic models
		static String ModelsPath = "/Users/Azhar/Desktop/NoisyModels/TIMIT_AM/";
		static String AcModel0 ="timit_Clean.cd_cont_200";
		static String AcModel20 ="timit_20dB.cd_cont_200";
		static String AcModel15 ="timit_15dB.cd_cont_200";
		static String AcModel10 ="timit_10dB.cd_cont_200";
		static String[] AcModel = {AcModel0, AcModel20,AcModel15,AcModel10};
		
		static String DictName = "timit.dic";
		static final String LM_Name = "TIMIT_test.lm";
		static String speechFilePath = WavePath+ fileName2+".WAV";
		static String textFilePath = WavePath+ fileName2+".TXT";

	 
    public static void main(String[] args) throws IOException {
    	//WOW, best way to get rid of WARNINGS and info in the log an stop showing them on console
    			System.err.close();
    	
      
        Configuration config = new Configuration();
		ConfigSetup conf = new ConfigSetup();
		SpeechResult result10dB = recog(config,conf,AcModel[0],speechFilePath);
		
			 
			SpeechResult result15dB = recog(config,conf,AcModel[1],speechFilePath);
			
			 
	      // Gettting lattices results for each AM
		Lattice lattice10dB = new Lattice(result10dB.getResult());
	    	Lattice lattice15dB = new Lattice(result15dB.getResult());
	    	lattice10dB.dumpDot("lattice10dBBeforeUpdate.dot", "lattice10dB.dot");
	    	lattice15dB.dumpDot("lattice15dBBeforeUpdate.dot", "lattice15dB.dot");
	    	
	    	// Update the AM using the SNR estimator
	    	double[] LL_SPS = {-1e7,-10e7};
	    	lattice10dB = LatticeTest.UpdateAcousticScore(lattice10dB, LL_SPS[0]);
	    	lattice15dB = LatticeTest.UpdateAcousticScore(lattice15dB, LL_SPS[1]);
	    	
	    
	    	
	    	//Results
	    	
	    System.out.println("Speech file Abs Path: " + speechFilePath);  
	    System.out.println("Reference Utterance: He spoke soothingly.");
	    	System.out.println("Result from AM_10dB is:" + result10dB.getResult());
	    	System.out.println("Result from AM_15dB is:" + result15dB.getResult());
	    	
	    	// New combined results
	    	Lattice new_lattice = CombineLattice.CombineNoScale(lattice10dB, lattice15dB);
	    	new_lattice.computeNodePosteriors(1.0f); 
	 
	   // get textual utterance after combining
	    	
	   	String finalResult= getFinalReslts(new_lattice);
	   	finalResult = finalResult+"\n";
	   	// Store textual utterances in a file
	   	StoreTextResult(finalResult, "Result.txt");
   
    	System.out.print("Final Result: "+finalResult);
    	System.out.println();
	System.out.println();
	
	    
	    	
	    	// Store lattice
	    	System.out.println("Finished ........");
	    	lattice10dB.dumpDot("lattice10dB.dot", "lattice10dB.dot");
	    	lattice15dB.dumpDot("lattice15dB.dot", "lattice15dB.dot");
	    	new_lattice.dumpDot("new_Lattice.dot", "New_lattice.dot");
	    			
			
			
		}
    
    private static void dumpingResults(Lattice lattice) {
     	List<Node> xx = lattice.getViterbiPath();
	    Iterator<Node> nodes = xx.iterator();
	    
	    System.out.println();
	    System.out.print("New results: ");
	    while(nodes.hasNext()) {
	    		Node node = nodes.next();
	    		System.out.print(node.getWord()+" ");
	    }
	    	System.out.println();
	    	
	     List<WordResult> words = lattice.getWordResultPath();
	   	Iterator<WordResult> i = words.iterator();
    }
    
    public static SpeechResult recog(Configuration config,ConfigSetup conf, String AcModel, String speechFilePath) throws IOException{

		StreamSpeechRecognizer recognizer = conf.BuildRecognizer(ModelsPath , DictName, LM_Name,AcModel, config);
		
		
		// Start recognition
		recognizer.startRecognition(new FileInputStream(speechFilePath));
		SpeechResult result = recognizer.getResult();
		// Stop recognition
     	recognizer.stopRecognition();
     return result;
	
    }
    
  /**
   * 
   * @param lattice
   * @return textual results
   */
    private static String getFinalReslts(Lattice lattice) {
    	
    	List<WordResult> words = lattice.getWordResultPath();
    	Iterator<WordResult> i = words.iterator();
    	String FinalResult ="";
    	while(i.hasNext()) {
    		WordResult word = i.next();
    		FinalResult = FinalResult + word.getWord()+" ";
    	}
    	
    	return FinalResult;
    }
    
   /**
    *  
    * @param result
    * @param fileName
    * @return
    */
    private static boolean StoreTextResult(String result, String fileName) {
        	
    boolean done = false;
     
    	// Print to text file
    	try {
			PrintWriter out = new PrintWriter(fileName);
			out.println(result);
			out.flush();
			out.close();
			done = true;
		} catch (FileNotFoundException e) {
			System.out.println("Error: The output file not found");
			e.printStackTrace();
		}

    	 return done;
    }
    
       
         
}
