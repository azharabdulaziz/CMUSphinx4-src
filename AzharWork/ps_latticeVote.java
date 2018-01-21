package AzharWork;
import java.io.File;
/**
 * Pocketsphinx lattice vote, is a test for what pocketsphinx output produces in PocketsphinxPy/MDC_MDV.py.
 * @paragraph
 * The steps are: 
 * 1- For each test utterance, combine the lattices produced by the four AMs 
 * 2- Estimate the posteriori probability of the comnbined lattice (it is neccesary for the next step)
 * 3- Retrieve the MAP path using Verbi search.  
 */
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.Context;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.linguist.WordSequence;
import edu.cmu.sphinx.linguist.dictionary.Word;
import edu.cmu.sphinx.linguist.language.ngram.LanguageModel;
import edu.cmu.sphinx.linguist.language.ngram.trie.BinaryLoader;
import edu.cmu.sphinx.result.Edge;
import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.LatticeRescorer;
import edu.cmu.sphinx.result.Node;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.LogMath;
import edu.cmu.sphinx.util.props.ConfigurationManagerUtils;
import edu.cmu.sphinx.util.props.PropertySheet;
import AzharWork.CombineLattice;

public class ps_latticeVote {
	public static void main(String[] args) throws IOException {
		String expName = "TIMIT";   // Could be variable
		String baseDir = "/Users/Azhar/Desktop/MDC_Experiments/"+ expName +"/";
		
		//Common In itialization
		String LatticeBaseDir = baseDir + "Results/";
		String snr = "Clean/" ;  // Should be variable
		String accModel1 = "TIMIT_Clean.cd_cont_200/Lattice/";
		String accModel2 = "TIMIT_10dB.cd_cont_200/Lattice/";
		String accModel3 = "TIMIT_15dB.cd_cont_200/Lattice/";
		String accModel4 = "TIMIT_20dB.cd_cont_200/Lattice/";
		String[] AcModel = {accModel1, accModel2,accModel3,accModel4};
		//String fileName = "test-DR4-FEDW0-SI1653.htk"; // Will be variable
		String fileName ="test-DR1-FAKS0-SA1.htk";
		
		//Lattice l1 = ReadSingleModelLattices(LatticeBaseDir, fileName, accModel1, snr);
		//l1.dumpDot("l1.dot", getFinalResultNoFiller(l1));
		// Initialize configuration to get LanguageModel class from configuration. This will be used in LatticeRescorer
		String LMPath = baseDir + "TIMIT_Models/timit.test.lm";
		String DictPath = baseDir + "TIMIT_Models/";
		String AMPath = baseDir + "TIMIT_Models/timit_Clean.cd_cont_200/";
	
		// get configuration values
		Configuration config = new Configuration();
		ConfigSetup conf = new ConfigSetup();
		StreamSpeechRecognizer Recognizer = conf.BuildRecognizer(AMPath, LMPath, DictPath, config);
		
		
		LanguageModel langModel= getLnguageModel(config,AMPath,LMPath,DictPath); 
		
		System.out.println("LM Max Depth: " + langModel.getMaxDepth());
		System.out.println("Current Language Model is in: " + config.getLanguageModelPath());
		Lattice l1 = ReadSingleModelLattices(LatticeBaseDir, fileName, accModel1, snr);
		RescoreLatticeToLm lr = new RescoreLatticeToLm(l1, langModel);
		lr.rescore();
		
		
		/*ArrayList<Lattice> lattices = ReadNoisyModelLattices(LatticeBaseDir, fileName,AcModel,snr);
		ArrayList<WordResult> a = getBestPath(LatticeBaseDir,fileName,accModel1,snr); 
		ShowLatticeofBestPath(a);
		Lattice finalLattice = CombineModels(lattices);
		String finalTextResult = getFinalResultNoFiller(finalLattice);
		System.out.println();
		System.out.println("Show Fused lattice word scores: ");
		ShowWordScores(finalLattice);
		System.out.println();
		System.out.println("Final Text: " + finalTextResult);*/
	}

	@SuppressWarnings("static-access")
	private static void ShowLatticeofBestPath(ArrayList<WordResult> wordList) {
		// TODO Auto-generated method stub
		Iterator<WordResult> iter = wordList.iterator();
		while(iter.hasNext()) {
			WordResult wordresult = iter.next();
			double score = wordresult.getScore();
			Word word = wordresult.getWord();
			double logConf = wordresult.getConfidence();
			LogMath logmath = LogMath.getLogMath();
			double confLinear = logmath.getLogMath().logToLinear((float) logConf);
			
			System.out.println("Word('" + word.toString() + "'): " + " Score: " + Double.toString(score) 
			+ " Confidence: " + Double.toString(confLinear));
			
		}
		
	}

	private static void ShowAllWordResultPath(ArrayList<WordResult> wordResults) {
		// TODO Auto-generated method stub
		Iterator<WordResult> iter = wordResults.iterator();
		while(iter.hasNext()) {
			System.out.println("Score of Word( " + iter.next().getWord() + ") " + iter.next().getScore());
		}
		
	}
/**
 * This method gives the four AMs best path
 * @param latticeBaseDir
 * @param fileName
 * @param acModelsList
 * @param snr
 * @return
 * @throws IOException
 */
	private static ArrayList<WordResult> getBestPath(String latticeBaseDir, String fileName, String[] acModelsList,
			String snr) throws IOException {
		String testLattice = latticeBaseDir + snr;
		ArrayList<WordResult> wordResults = new ArrayList<WordResult> ();
		for(int i= 0; i<4; i++) {
			String testFile = testLattice+acModelsList[i]+fileName;
			Lattice lattice = Lattice.readhtk(testFile);
			lattice.computeNodePosteriors(1.0f);
			List<WordResult> wordpath = lattice.getWordResultPath();
			Iterator<WordResult> it = wordpath.iterator();
			while(it.hasNext()) {
				wordResults.add(it.next());
			}
		}
		


		return wordResults;
	}

	/**
	 * This method returns word result from a single AM lattice
	 * @param latticeBaseDir
	 * @param fileName
	 * @param acModel
	 * @param snr
	 * @return wordResults
	 * @throws IOException
	 */
	private static ArrayList<WordResult> getBestPath(String latticeBaseDir, String fileName, String acModel,
			String snr) throws IOException {
		String testLattice = latticeBaseDir + snr;
		ArrayList<WordResult> wordResults = new ArrayList<WordResult> ();
		String testFile = testLattice+acModel+fileName;
		Lattice lattice = Lattice.readhtk(testFile);
		lattice.computeNodePosteriors(1.0f);
		List<WordResult> wordpath = lattice.getWordResultPath();
		Iterator<WordResult> it = wordpath.iterator();
		while(it.hasNext()) {
			wordResults.add(it.next());
		}
		
		


		return wordResults;
	}

	
	/**
	 * 
	 * @param lattices
	 * @param langModel 
	 * @return
	 */
	private static Lattice getLangModelScoredLattice(Lattice lattice, LanguageModel langModel) {
		// TODO Auto-generated method stub
		Lattice rescoredLattice = new Lattice();
		LatticeRescorer rescoredLattice1 = new LatticeRescorer(lattice,langModel);
		rescoredLattice = rescoredLattice1.getRescoredLattice();
		
		return rescoredLattice ;
	}

	
	/**
	 * 
	 * @param acousticModelPath
	 * @param lmPath
	 * @param dictPath
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private static LanguageModel getLnguageModel(Configuration config,String acousticModelPath, String lmPath, String dictPath) throws MalformedURLException, IOException  {
		// TODO Auto-generated method stub
/*		config.setLanguageModelPath(lmPath);
		config.setAcousticModelPath(acousticModelPath);
		config.setDictionaryPath(dictPath );
		
*/		
		Context context = new Context(config);
//		context.setLanguageModel(lmPath);
		//String path = config.getLanguageModelPath();
//		System.out.println("LM Path from Context:  " + context.getLanguageModel());
		
		LanguageModel langModel = context.getInstance(LanguageModel.class);
		return langModel;
	}


	private static Lattice CombineModels(ArrayList<Lattice> lattices) {
		// Combine lattices
				Lattice finalLattice = new Lattice();
				Iterator<Lattice> latIter = lattices.iterator();
				Lattice l1 = latIter.next();
				Lattice l2 = latIter.next();
				Lattice l3 = latIter.next();
				Lattice l4 = latIter.next();
				Lattice l5 = CombineLattice.CombineNoScale(l1, l2);
				Lattice l6 = CombineLattice.CombineNoScale(l3, l4);
				finalLattice = CombineLattice.CombineNoScale(l5, l6);
		return finalLattice;
	}


	private static ArrayList<Lattice> ReadNoisyModelLattices(String baseDir, String fileName, String[] AcModel,String snr) throws IOException {
		// Read Lattices and store them to ArrayList<Lattice>
				String testLattice = baseDir + snr;
				ArrayList<Lattice> lattices = new ArrayList<Lattice> ();
				for(int i= 0; i<4; i++) {
					String testFile = testLattice+AcModel[i]+fileName;
					Lattice lattice = Lattice.readhtk(testFile);
					//ShowWordScores(lattice);
					lattices.add(lattice);
				}
				
		
		
		return lattices;
	}
	
	
	private static Lattice ReadSingleModelLattices(String baseDir, String fileName, String AcModel,String snr) throws IOException {
		// Read Lattices and store them to ArrayList<Lattice>
				String testLattice = baseDir + snr;
				String testFile = testLattice+AcModel+fileName;
				Lattice lattice = Lattice.readhtk(testFile);
			    //System.out.println("Lattice from: " + testFile);
				return lattice;
	}


	


	/**
	 * 
	 * @param finalLattice
	 * @return
	 */
private static String getFinalResultNoFiller(Lattice finalLattice) {
		// TODO Auto-generated method stub
		finalLattice.computeNodePosteriors(0);
		
		List<Node> nodes = finalLattice.getViterbiPath();
		Iterator<Node> nodeItr = nodes.iterator();
		String textResult="";
		while(nodeItr.hasNext()) {
			textResult = textResult + nodeItr.next().getWord()+" ";
		}
		
		String finalTextResult = textResult.replace("<s>", "");
		finalTextResult = finalTextResult.replace("</s>","");
		return finalTextResult;
	}

	/**
	 * 
	 * @param lattice
	 */
	private static void ShowWordScores(Lattice lattice) {
		// TODO Auto-generated method stub
		lattice.computeNodePosteriors(0);
		List<WordResult> wr = lattice.getWordResultPath();
		Iterator<WordResult> word_iterator = wr.iterator();
		
		while(word_iterator.hasNext()) {
			WordResult x = word_iterator.next();
			System.out.println("Score of word('"+ x.getWord()  + "'): " + x.getScore());
			
		}
	}
	
	private static void ShowEdgeScores(Lattice lattice) {
		// TODO Auto-generated method stub
		lattice.computeNodePosteriors(0);
		Collection<Edge> edges = lattice.getEdges();
		Iterator<Edge> edgeIterator = edges.iterator();
		while(edgeIterator.hasNext()) {
			Edge x = edgeIterator.next();
			System.out.println("LM Score of word(''): " + x.getLMScore());
			
		}
	}


}
