package AzharTestPackage;

import java.util.ArrayList;
import java.util.Iterator;

import edu.cmu.sphinx.result.Lattice;

public class TestJavaArrayList {

	public static void main(String[] args) {
		ArrayList<String> lattices = new ArrayList<String>();
		
		String accModel1 = "10dB";
		String accModel2 = "15dB";
		String accModel3 = "20dB";
		String accModel4 = "Clean";
		String[] AcModel = {accModel1, accModel2,accModel3,accModel4};
		
		for(int i=0;i<4;i++) {
			lattices.add(AcModel[i]);
		}
		
		
		Iterator<String> latIter = lattices.iterator();

		String l1 = latIter.next();
		String l2 = latIter.next();
		String l3 = latIter.next();
		String l4 = latIter.next();
		
		System.out.println("l1: " + l1);
		System.out.println("l2: " + l2);
		System.out.println("l3: " + l3);
		System.out.println("l4: " + l4);


	}

}
