
package AzharWork;

import java.io.IOException;
import edu.cmu.sphinx.api.*;
//import azharAPI.Configuration;
//import azharAPI.StreamSpeechRecognizer;

public class ConfigSetup {
	 
				

	public StreamSpeechRecognizer BuildRecognizer(String ModelPath, String DictName,String LM_name, String accModel, Configuration config) throws IOException{
		
		// Set path to acoustic model.
		
		System.out.println("Load Accoustic Mode:" + "file:"+ModelPath+accModel);
		config.setAcousticModelPath("file:"+ModelPath+accModel);
		
		// Set path to dictionary.
		System.out.println("Load Dictionary:"+ "file:"+ ModelPath + DictName);
		config.setDictionaryPath("file:"+ ModelPath + DictName);
		
		//Timit LM
		System.out.println("Load Language Model:" + "file:"+ ModelPath + LM_name);
		config.setLanguageModelPath("file:"+ ModelPath + LM_name);
		
		System.out.println("Setting Rcognizer");
		StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(config);
		
		System.out.println("Fnish Setting .... ");
		
		return recognizer;
	}

}
