/**
 * 
 */
package AzharWork;

import java.io.FileInputStream;
import java.io.IOException;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.Lattice;

/**
 * @author Azhar S. Abdulaziz
 *
 */
public class decodeOnce {
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
	public static Lattice getLattice(String fileName, String AM, String LM,String Dict) throws IOException {
		Lattice lattice = new Lattice();
		Configuration config = new Configuration();
		ConfigSetup conf = new ConfigSetup();
		StreamSpeechRecognizer recognizer = conf.BuildRecognizer(AM,LM,Dict,config);
		recognizer.startRecognition(new FileInputStream(fileName));
		SpeechResult result = recognizer.getResult();
		lattice = result.getLattice();
		return lattice;
	}

}
