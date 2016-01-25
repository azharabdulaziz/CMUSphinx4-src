package AzharWork;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import com.opencsv.CSVReader;

import edu.cmu.sphinx.api.*;


public class TIMIT_Transcriber {
	
	static int dB_val = 5; // Change This value to change the experiment, it could be 5,10,15,20,25,30,35,40,45 and 50.
	
	// Define acoustic models
	static String AcModelClean ="/Users/Azhar/Desktop/TIMIT_AM/timit_Clean.cd_cont_200/";
	static String AcModel20dB ="/Users/Azhar/Desktop/TIMIT_AM/timit_20dB.cd_cont_200/";
	static String AcModel15dB ="/Users/Azhar/Desktop/TIMIT_AM/timit_15dB.cd_cont_200/";
	static String AcModel10dB ="/Users/Azhar/Desktop/TIMIT_AM/timit_10dB.cd_cont_200/";
	static String[] AcModel = {AcModelClean, AcModel20dB,AcModel15dB,AcModel10dB};
	
	
	static String testFileIds = "/Users/Azhar/Desktop/Exp1/timit/etc/timit_test.fileids";
	static String BaseDir = "/Users/Azhar/Desktop/Exp1/timit/";
	
	
	
	 
    public static void main(String[] args) throws IOException {
    	//WOW, best way to get rid of WARNINGS and info in the log an stop showing them on console
    			System.err.close();
    			
    	    	
    			
    			String wavDir = "wav"+dB_val+"db/";
    			String fileName;
    // Configuration
    			Configuration config = new Configuration();
    			ConfigSetup conf = new ConfigSetup();
    			StreamSpeechRecognizer recognizer_clean = conf.BuildRecognizer(AcModel[0], config);
    			StreamSpeechRecognizer recognizer_20dB = conf.BuildRecognizer(AcModel[1], config);
    			StreamSpeechRecognizer recognizer_15dB = conf.BuildRecognizer(AcModel[2], config);
    			StreamSpeechRecognizer recognizer_10dB = conf.BuildRecognizer(AcModel[3], config);
    		
        // Read TIMIT test file IDs
    			BufferedReader reader = new BufferedReader(new FileReader(testFileIds)); //try with resources needs JDK 7  
    				String fileRelName;
    				while ((fileRelName = reader.readLine().trim()) != null) { //read until end of stream
    					
    					fileName = BaseDir+wavDir+fileRelName+".WAV";
    					System.out.println(fileName);
    					
    			    	// Start recognition
    			   		/*recognizer.startRecognition(new FileInputStream(fileName));
    			   		SpeechResult result = recognizer.getResult();
    					System.out.println("Result: " + result.getResult().getBestFinalResultNoFiller());
    					
    					
    					
    					// Stop recognition
    					recognizer.stopRecognition();*/

    			   		
    					
    				}
    			
    				
    }
    
         
}
