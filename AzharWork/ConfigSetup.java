
package AzharWork;

import java.io.IOException;
import edu.cmu.sphinx.api.*;
//import azharAPI.Configuration;
//import azharAPI.StreamSpeechRecognizer;

public class ConfigSetup {
	 
				

	public StreamSpeechRecognizer BuildRecognizer(String accModelPath, Configuration config) throws IOException{
		
		// Set path to acoustic model.
		
		System.out.println("Load Accoustic Model........");
		config.setAcousticModelPath("file:"+accModelPath);
		
		// Set path to dictionary.
		System.out.println("Load Dictionary: TIMIT dictionary");
		config.setDictionaryPath("file:/Users/Azhar/Desktop/TIMIT_AM/timit_Dict/timit.dic");
		
		//Timit LM
		System.out.println("Load Language Model: TIMIT lm");
		config.setLanguageModelPath("resource:/transcriber/TIMIT_test.lm");
		
		StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(config);
		
		System.out.println("Fnish Setting .... ");
		
		return recognizer;
	}

}
