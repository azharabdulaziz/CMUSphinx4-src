package AzharTestPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.Node;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.LogMath;

public class SimpleLatticeFusion {

	private static int id;
	private static Lattice newLattice;
	private static boolean initNodeSet;


	public static void main(String[] args) throws IOException {
		//Initialize
		id = 0;
		newLattice = new Lattice();
		initNodeSet = false;
		ArrayList<WordResult> words = new ArrayList<>();
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
		//ArrayList<Lattice> lattices = new ArrayList<Lattice> ();
		for(int i= 0; i<4; i++) {
			String testFile = testLattice+AcModel[i]+fileName;
			System.out.println("Lattice from: " + testFile);
			Lattice lattice = Lattice.readhtk(testFile);
		//	if(!initNodeSet) SetInitNode(lattice); 
			lattice.computeNodePosteriors(1.0f);
			//words = (ArrayList<WordResult>) lattice.getWordResultPath();
			words.addAll(lattice.getWordResultPath());

			
			//lattices.add(lattice);
		}

		//getBestResultLtt(words);
		
	
	}

	
		
		
	

	private static void ShowLattice(Lattice lattice) {
		Collection<Node> nodes = lattice.getNodes();
		Iterator<Node> iter = nodes.iterator();
		while(iter.hasNext()) {
			Node node = iter.next();
			System.out.println("Node(" + node.getId()+"): "+ node.getWord());
		}
		
	}
	
	
	
	
	
	

}
