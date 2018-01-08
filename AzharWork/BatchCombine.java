package AzharWork;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import edu.cmu.sphinx.api.*;
import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.WordResult;


public class BatchCombine {

	static String BaseDir = "/Users/Azhar/Desktop/MDC_Experiments/";
	static String TestFileIds = "an4/an4_test.fileids";
	static String BaseWavPath = "/Users/Azhar/Desktop/Exp2/an4/wavWhite50db/"; 
	// Define acoustic models
	static String ModelsPath = BaseDir + "an4/AN4_AM/";
	static String AcModel0 ="an4_Clean.cd_cont_200";
	static String AcModel20 ="an4_White20dB.cd_cont_200";
	static String AcModel15 ="an4_White15dB.cd_cont_200";
	static String AcModel10 ="an4_White10dB.cd_cont_200";
	static String[] AcModel = {AcModel0, AcModel20,AcModel15,AcModel10};

	static String DictName = "an4.dic";
	static final String LM_Name = "an4.lm";


	public static void main(String[] args) throws IOException {
		//WOW, best way to get rid of WARNINGS and info in the log an stop showing them on console
		//System.err.close();

		// Initialize recognizers, each for specific AM 
		Configuration config = new Configuration();
		ConfigSetup conf = new ConfigSetup();
		StreamSpeechRecognizer CleanRecognizer = conf.BuildRecognizer(ModelsPath , DictName, LM_Name,AcModel[0], config);
		StreamSpeechRecognizer Recognizer20dB = conf.BuildRecognizer(ModelsPath , DictName, LM_Name,AcModel[1], config);
		StreamSpeechRecognizer Recognizer15dB = conf.BuildRecognizer(ModelsPath , DictName, LM_Name,AcModel[2], config);
		StreamSpeechRecognizer Recognizer10dB = conf.BuildRecognizer(ModelsPath , DictName, LM_Name,AcModel[3], config);
		StreamSpeechRecognizer[]	Recognizer = {CleanRecognizer,Recognizer20dB,Recognizer15dB,Recognizer10dB};
		
		

		String finalResult = "";

		
		PrintWriter out = new PrintWriter("Result.txt");
		
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(new File(BaseDir + TestFileIds));
		int i =0;
		while(scan.hasNextLine()){
			String line = scan.nextLine();
			// To extract the file name from relative path
			String fName = line.substring(line.indexOf("/")+1, line.length());
			fName = fName.replace("/", "-");
			String fileName = BaseWavPath + line+".wav";
			System.out.println("Decoding file: " + fileName);
			//Here you can manipulate the string the way you want
			//Lattice new_lattice = getCombinedResult(Recognizer, fileName);
			Lattice new_lattice = getSingledResult(Recognizer[0], fileName);
			//Dump lattice for debug
			new_lattice.dumpDot("Combined4AMs.dot", "FourAMsTest");
			// get textual utterance after combining
			finalResult= finalResult + getFinalReslts(new_lattice)+" ("+fName+")" +"\n";
			finalResult = finalResult.replace("<sil>", "");
			
			i++;
			//System.out.print("\33[1A\33[2K");
			//System.out.print(finalResult+"  "+ Math.round(100*i/130)+ "%");
			

			
			// Store textual utterances in a file
			boolean done  = StoreTextResult(finalResult, out);
			if(!done) {
				System.out.println("Couldn't print the results on file");
			}
			
		}

		

		//get single result for debug
/*		System.out.println("Single result for debug.");
		//String fileName ="/Users/Azhar/Desktop/Exp2/an4/wav/an4test_clstk/menk/an422-menk-b.sph"; // Deleted from fileids
		String fileName ="/Users/Azhar/Desktop/Exp2/an4/featClean/an4test_clstk/mjwl/an392-mjwl-b.mfc";
		//Lattice lattice = getSingledResult(Recognizer[0], fileName);
		Lattice lattice = getCombinedResult(Recognizer, fileName);
		finalResult = getFinalReslts(lattice);
*/		
		
		
		
		
		System.out.print("Final Result: "+finalResult);    




		System.out.println();
		System.out.println();



		// Store lattice
		System.out.println("Finished ........");
		// 	lattice10dB.dumpDot("lattice10dB.dot", "lattice10dB.dot");
		//	lattice15dB.dumpDot("lattice15dB.dot", "lattice15dB.dot");
		// 	new_lattice.dumpDot("new_Lattice.dot", "New_lattice.dot");



	}


	/**
	 * 
	 * @param config
	 * @param conf
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private static Lattice getCombinedResult(StreamSpeechRecognizer[] Recognizer, String fileName ) throws IOException {

		// get results for all 4 AMs
		SpeechResult resultClean = Decode(Recognizer[0],fileName);
		SpeechResult result20dB = Decode(Recognizer[1],fileName);
		SpeechResult result15dB = Decode(Recognizer[2],fileName);
		SpeechResult result10dB = Decode(Recognizer[3],fileName);

		// Gettting lattices results for each AM
		Lattice latticeClean = new Lattice(resultClean.getResult());
		Lattice lattice20dB = new Lattice(result20dB.getResult());
		Lattice lattice15dB = new Lattice(result15dB.getResult());
		Lattice lattice10dB = new Lattice(result10dB.getResult());


		// New combined results
		Lattice lattice1 = CombineLattice.CombineNoScale(latticeClean, lattice20dB);
		Lattice lattice2 = CombineLattice.CombineNoScale(lattice10dB, lattice15dB);
		Lattice new_lattice = CombineLattice.CombineNoScale(lattice1, lattice2);
		// Compute posteriori, it is required
		new_lattice.computeNodePosteriors(1.0f); 

		return new_lattice;
	}


	/**
	 * 
	 * @param Recognizer
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private static Lattice getSingledResult(StreamSpeechRecognizer Recognizer, String fileName ) throws IOException {

		// get results for a single AM
		SpeechResult result = Decode(Recognizer,fileName);
		
		// Getting lattices results for each AM
		Lattice lattice = new Lattice(result.getResult());
		

		
		// Compute posterior, it is required
		lattice.computeNodePosteriors(1.0f); 

		return lattice;
	}

	/**
	 * 
	 * @param recognizer
	 * @param speechFilePath
	 * @return
	 * @throws IOException
	 */
	private static SpeechResult Decode(StreamSpeechRecognizer recognizer, String speechFilePath) throws IOException{
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
	private static boolean StoreTextResult(String result, PrintWriter out) {

		boolean done = false;

		out.append(result);
		//out.println(result);
		out.flush();
		out.close();
		done = true;

		return done;
	}



}
