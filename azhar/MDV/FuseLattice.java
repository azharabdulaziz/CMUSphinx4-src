package azhar.MDV;

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
	
	public static String getFusedResutl(ArrayList<Lattice> lattices) {
		// Combine lattices
		Lattice finalLattice = new Lattice();
		Iterator<Lattice> latIter = lattices.iterator();

		Lattice l1 = latIter.next();
		Lattice l2 = latIter.next();
		Lattice l3 = latIter.next();
		Lattice l4 = latIter.next();
		Lattice l5 = CombineLattice.Combine(l1, l2);
		Lattice l6 = CombineLattice.Combine(l3, l4);

		finalLattice = CombineLattice.Combine(l5, l6);
		
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
