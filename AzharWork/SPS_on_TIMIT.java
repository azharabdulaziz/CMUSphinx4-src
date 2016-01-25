package AzharWork;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import com.opencsv.CSVReader;

import edu.cmu.sphinx.api.*;


public class SPS_on_TIMIT {
	
	static int dB_val = 5; // Change This value to change the experiment, it could be 5,10,15,20,25,30,35,40,45 and 50.
	
	// Define acoustic models
	static String AcModelClean ="/Users/Azhar/Desktop/TIMIT_AM/timit_Clean.cd_cont_200/";
	static String AcModel20dB ="/Users/Azhar/Desktop/TIMIT_AM/timit_20dB.cd_cont_200/";
	static String AcModel15dB ="/Users/Azhar/Desktop/TIMIT_AM/timit_15dB.cd_cont_200/";
	static String AcModel10dB ="/Users/Azhar/Desktop/TIMIT_AM/timit_10dB.cd_cont_200/";
	static String[] AcModel = {AcModelClean, AcModel20dB,AcModel15dB,AcModel10dB};
	
	
	static String testFileIds = "/Users/Azhar/Desktop/Exp1/timit/etc/timit_test.fileids";
	static String BaseDir = "/Users/Azhar/Desktop/Exp1/timit/";
	
	
	static String Models_LL = "/Users/Azhar/Documents/MATLAB/TheProposedSNR/MDL_LLs"+dB_val+"dB.csv";
	 
    public static void main(String[] args) throws IOException {
    	//WOW, best way to get rid of WARNINGS and info in the log an stop showing them on console
    			System.err.close();
    			
    			// Read CSV example
    	    	CSVReader Model_weights = new CSVReader(new FileReader(Models_LL));
    	    	String [] nextLine;
    	    	double LL_MDL1,LL_MDL2,LL_MDL3,LL_MDL4;
    	    	
    			
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
    					// nextLine[] is an array of values from the line which has Weights from SSP-SNR, or the LL(xdB) where xdB is the model
    					// They are arranged in this way: LL10dB, LL15dB, LL20dB, LLClean
    					/*nextLine = Model_weights.readNext();
    			    	   LL_MDL1 = Double.parseDouble(nextLine[0]);  // MDL1= AM_10dB
    			    	   LL_MDL2 = Double.parseDouble(nextLine[1]);  // MDL2 = AM_15dB
    			    	   LL_MDL3 = Double.parseDouble(nextLine[2]);  // MDL3 = AM_20dB
    			    	   LL_MDL4 = Double.parseDouble(nextLine[3]);  // MDL4 = AM_Clean
    					*/
    					
    			    	// Start recognition
    			   		/*recognizer.startRecognition(new FileInputStream(fileName));
    			   		SpeechResult result = recognizer.getResult();
    					System.out.println("Result: " + result.getResult().getBestFinalResultNoFiller());
    					
    					
    					
    					// Stop recognition
    					recognizer.stopRecognition();*/

    			   		
    					
    				
    			}
    				
    }
    
         
}
