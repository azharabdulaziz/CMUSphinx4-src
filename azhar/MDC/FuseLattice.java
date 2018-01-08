package azhar.MDC;
/**
 * This class extract textual results from an array of lattices, 
 * each element represents a final result lattice from certain noisy AM.
 * The steps are: 
 * 1- Combine the lattices produced by the four AMs 
 * 2- Estimate the posteriori probability of the comnbined lattice (it is neccesary for the next step)
 * 3- Retrieve the MAP path using Verbi search.
 * @author Azhar S Abdulaziz
 * @since 2017
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.cmu.sphinx.result.Lattice;
import edu.cmu.sphinx.result.Node;

public class FuseLattice {

	public Lattice getLattice(String fileName) throws IOException {
		
		Lattice lattice = Lattice.readhtk(fileName);
		//lattices.add(lattice);
		return lattice;

	}
	
	public static String getFusedResutlNoScaleAM(ArrayList<Lattice> lattices) {
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
		
		finalLattice.computeNodePosteriors(0);
		
		List<Node> nodes = finalLattice.getViterbiPath();
		Iterator<Node> nodeItr = nodes.iterator();
		String textResult="";
		while(nodeItr.hasNext()) {
			textResult = textResult + nodeItr.next().getWord()+" ";
		}
		
		String finalTextResult = textResult.replace("<s>", "");
		finalTextResult = finalTextResult.replace("</s>","");
		finalTextResult = finalTextResult.replace("<sil>","");
		finalTextResult = finalTextResult.trim().replaceAll(" +", " ");
		finalTextResult = finalTextResult.trim();
		//System.out.println("Final Text: " + finalTextResult);
		return finalTextResult;

	}
	
	/**
	 * 
	 * @param lattices
	 * @param alpha1
	 * @param alpha2
	 * @return
	 */
	public static String getFusedResutlScaleAM(ArrayList<Lattice> lattices, double alpha1, double alpha2) {
		// Combine lattices
		Lattice finalLattice = new Lattice();
		Iterator<Lattice> latIter = lattices.iterator();

		Lattice l1 = latIter.next();
		Lattice l2 = latIter.next();
		Lattice l3 = latIter.next();
		Lattice l4 = latIter.next();
		Lattice l5 = CombineLattice.CombineScaledAM(l1, l2, alpha1, alpha2);
		Lattice l6 = CombineLattice.CombineNoScale(l3, l4);

		finalLattice = CombineLattice.CombineNoScale(l5, l6);
		
		finalLattice.computeNodePosteriors(0);
		
		List<Node> nodes = finalLattice.getViterbiPath();
		Iterator<Node> nodeItr = nodes.iterator();
		String textResult="";
		while(nodeItr.hasNext()) {
			textResult = textResult + nodeItr.next().getWord()+" ";
		}
		
		String finalTextResult = textResult.replace("<s>", "");
		finalTextResult = finalTextResult.replace("</s>","");
		finalTextResult = finalTextResult.replace("<sil>","");
		finalTextResult = finalTextResult.trim().replaceAll(" +", " ");
		finalTextResult = finalTextResult.trim();
		//System.out.println("Final Text: " + finalTextResult);
		return finalTextResult;

	}

	
}
