/*
 *  This is testing AM combination
 *  Azhar Abdulaziz
 */

package AzharWork;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.opencsv.CSVReader;

import edu.cmu.sphinx.api.*;
import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.Node;
import edu.cmu.sphinx.result.WordResult;


public class Transcribe {
	
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
    			//System.err.close();
    			
    	System.out.println("Speech file Abs Path: " + speechFilePath);
        
        System.out.println("Reference Utterance: He spoke soothingly.");
      // Read CSV example
    	/*CSVReader reader = new CSVReader(new FileReader("/Users/Azhar/Desktop/Exp1/CSVexample.csv"));
    	String [] nextLine;
    	while ((nextLine = reader.readNext()) != null) {
    	   // nextLine[] is an array of values from the line
    	   System.out.println(nextLine[0] +"   "+ nextLine[1] + "  etc...");
    	}*/
    	
		// Or
    	/*CSVReader reader = new CSVReader(new FileReader("/Users/Azhar/Desktop/Exp1/CSVexample.csv"));
		List<String[]> myEntries = reader.readAll();
		for(int i=0; i< myEntries.size(); i++){
			System.out.println("Entrie(" + i + ")=" + myEntries.get(i).toString());
		}*/
		
        Configuration config = new Configuration();
		ConfigSetup conf = new ConfigSetup();
		
		for(int acModel=0; acModel<4; acModel = acModel+1){
			System.out.println("Accoustic Model: " + ModelsPath + AcModel[acModel]);
			StreamSpeechRecognizer recognizer = conf.BuildRecognizer(ModelsPath , DictName, LM_Name,AcModel[acModel], config);
			
			// Start recognition
			recognizer.startRecognition(new FileInputStream(speechFilePath));
		
			SpeechResult result = recognizer.getResult();
			System.out.println("Result: " + result.getResult().getBestFinalResultNoFiller());
			ResultAnalysis A = new ResultAnalysis();
			//A.getWordScore(result);
			double totalscore = A.getTotalWordScore(result);
			System.out.println("Total Score=" + totalscore);
			//A.Node_analysis(result);
			
			// Stop recognition
			recognizer.stopRecognition();
			System.out.println("---------------------------------------");
		}
			
		
		
		
		//recognizer.setDataBlocker();
		//recognizer.setSpeechClassifier();
		//System.out.println("SNR = " + recognizer.getSNR());
		//DataProcessor processor = recognizer.getDataBlocker().getPredecessor();
		//SpeechClassifier sc = new SpeechClassifier();
		//sc.setPredecessor(processor);
		//System.out.println("SNR = " + sc.getSNR());
		//System.out.println("Blocker = " + recognizer.getDataBlocker().getPredecessor());
		//System.out.println("This Recognizer is:" + recognizer.toString());
		
		
    }
    
         
}
