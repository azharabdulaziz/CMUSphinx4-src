package AzharTestPackage;

import java.io.IOException;

import edu.cmu.sphinx.result.Lattice;
import AzharWork.LatticeTest;
public class readLattice {
	public static void main(String[] args) {
		
		String fileName = "/Users/Azhar/Desktop/MDC_Experiments/an4/Results/Clean/"
				+ "an4_Clean.cd_cont_200/Lattice/fvap-cen1-fvap-b.htk";
		Lattice lattice= new Lattice();
		Lattice new_lattice = new Lattice();
		try {
			
			 new_lattice = lattice.readhtk(fileName);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LatticeTest.DisplayLattice(new_lattice);
		new_lattice.dumpDot("FromLatToDot.dot", "TEST from "+fileName);
	}

}
