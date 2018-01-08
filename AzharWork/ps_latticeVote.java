package AzharWork;
/**
 * Pocketsphinx lattice vote, is a test for what pocketsphinx output produces in PocketsphinxPy/MDC_MDV.py.
 * @paragraph
 * The steps are: 
 * 1- For each test utterance, combine the lattices produced by the four AMs 
 * 2- Estimate the posteriori probability of the comnbined lattice (it is neccesary for the next step)
 * 3- Retrieve the MAP path using Verbi search.  
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.Node;
import edu.cmu.sphinx.result.WordResult;
import AzharWork.CombineLattice;

public class ps_latticeVote {
	public static void main(String[] args) throws IOException {
		String expName = "TIMIT";   // Could be variable
		String baseDir = "/Users/Azhar/Desktop/MDC_Experiments/"+ expName + "/Results/"; 
		String snr = "Clean/" ;  // Should be variable
		String accModel1 = "TIMIT_Clean.cd_cont_200/Lattice/";
		String accModel2 = "TIMIT_10dB.cd_cont_200/Lattice/";
		String accModel3 = "TIMIT_15dB.cd_cont_200/Lattice/";
		String accModel4 = "TIMIT_20dB.cd_cont_200/Lattice/";
		String[] AcModel = {accModel1, accModel2,accModel3,accModel4};
		
		String fileName = "test-DR4-FEDW0-SI1653.htk"; // Will be variable 
		// Read Lattices and store them to ArrayList<Lattice>
		String testLattice = baseDir + snr;
		ArrayList<Lattice> lattices = new ArrayList<Lattice> ();
		for(int i= 0; i<4; i++) {
			String testFile = testLattice+AcModel[i]+fileName;
			System.out.println("Lattice from: " + testFile);
			Lattice lattice = Lattice.readhtk(testFile);
			ShowWordScores(lattice);
			lattices.add(lattice);
		}
		
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
		
		String finalTextResult = getFinalResultNoFiller(finalLattice);
		System.out.println();
		System.out.println("Show Fused lattice word scores: ");
		ShowWordScores(finalLattice);
		System.out.println();
		System.out.println("Final Text: " + finalTextResult);
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

}
