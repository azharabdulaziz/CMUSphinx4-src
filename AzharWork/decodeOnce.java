/**
 * 
 */
package AzharWork;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.Context;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.decoder.ResultListener;
import edu.cmu.sphinx.linguist.language.classes.ClassBasedLanguageModel;
import edu.cmu.sphinx.linguist.language.ngram.LanguageModel;
import edu.cmu.sphinx.linguist.language.ngram.large.BinaryStreamLoader;
import edu.cmu.sphinx.linguist.language.ngram.trie.BinaryLoader;
import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.LogMath;
import edu.cmu.sphinx.util.props.PropertySheet;
import edu.cmu.sphinx.util.props.S4String;

/**
 * @author Azhar S. Abdulaziz
 *
 */

public class decodeOnce {
	
	 /** The property specifying the location of the language model. */
    @S4String(defaultValue = ".")
    public final static String PROP_LOCATION = "location";
    
	public static void main(String[] args) throws IOException {
		
		float logValue = (float) -1.234E2;
		TestLogMathValues(logValue);
		
		
		
		String fileName = "/Users/Azhar/Desktop/Exp1/timit/wav/TEST/DR1/FAKS0/SA1.wav";
		
		String expName = "TIMIT";   // Could be variable
		String baseDir = "/Users/Azhar/Desktop/MDC_Experiments/"+ expName +"/";
		
		String LMPath = baseDir + "TIMIT_Models/TIMIT_test.lm";
		String DictPath = baseDir + "TIMIT_Models/timit.dic";
		String AMPath = baseDir + "TIMIT_Models/timit_Clean.cd_cont_200/";
		String an4_LMPath = "/Users/Azhar/Desktop/MDC_Experiments/an4/an4.lm";
		
		
		
		/*Configuration config = new Configuration();
		System.out.println("Before setup: LM: "+ config.getLanguageModelPath());
		Lattice S4lattice = getLattice(fileName, AMPath, LMPath, DictPath,config);
		System.out.println("After setup: LM: "+ config.getLanguageModelPath());
		*/
		
		
		//File LMPathh = new File(an4_LMPath );
		/*File LMPathh = new File(LMPath );
		System.out.println("From decodeOnce         Line 49: LM Path is: "+ LMPathh.getPath());
		String format = "";
		boolean applyLanguageWeightAndWip=false;
		float languageWeight=1.0f;
		double wip = 0;
		float unigramWeight = 0;
		URL lcation = LMPathh.toURI().toURL() ;
		*/
		
		
		/*String psLatticeFile = "/Users/Azhar/Desktop/MDC_Experiments/TIMIT/Results/"
				+ "Clean/TIMIT_Clean.cd_cont_200/Lattice/test-DR1-FAKS0-SA1.htk";
		Lattice psLattice = Lattice.readhtk(psLatticeFile);
		
		S4lattice.computeNodePosteriors(1.0f, true);
		psLattice.computeNodePosteriors(1.0f, true);
		
		ResultAnalysis.ShowBestPath(S4lattice);
		
		ResultAnalysis.ShowBestPath(psLattice);
		
		Context context = new Context(config);
		
		LanguageModel langModel =context.getInstance(LanguageModel.class); 
		RescoreLatticeToLm lr = new RescoreLatticeToLm(S4lattice, langModel);
		*/
		//Dump Lattices
		/*
		psLattice.dumpDot("ps_SA1.dot", "PocketSphinx lattice Python");
		S4lattice.dumpDot("SA1.dot", "S4 JAVA");
		*/
	}
	
	
	private static void TestLogMathValues(float logValue) {
		// TODO Auto-generated method stub
		LogMath logmath = LogMath.getLogMath();
		double x = logmath.logToLinear(logValue);		
		float lin2 = logmath.linearToLog(x);
		
		System.out.println(" Log Value: " + logValue + "  Linear=" + x);
		System.out.println(" Linear Value: " + lin2 + "  Log=" + x);

		
	}


	/**
	 * 
	 * @param fileName
	 * @param AM
	 * @param LM
	 * @param Dict
	 * @return
	 * @throws IOException
	 * @author Azhar Abdulaziz
	 * @since November, 2017
	 */
	public static Lattice getLattice(String fileName, String AM, String LM,String Dict,Configuration config) throws IOException {
		Lattice lattice = new Lattice();
		config.setAcousticModelPath("file:"+AM);
		// Set path to dictionary.
		config.setDictionaryPath("file:" + Dict);
		//Timit LM
		config.setLanguageModelPath("file:" + LM);
		
		StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(config);
		
		recognizer.startRecognition(new FileInputStream(fileName));
		SpeechResult result = recognizer.getResult();
		lattice = result.getLattice();
		
		return lattice;
	}
	
}
