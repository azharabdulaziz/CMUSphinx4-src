package azhar.MDV;
import java.io.File;
import java.io.FileWriter;
/**
 * 
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import edu.cmu.sphinx.result.Lattice;


public class MultDecoderlatticeVote {
	public static void main(String[] args) throws IOException {
		String expName = "an4";   // Could be variable
		String baseDir = "/Users/Azhar/Desktop/MDC_Experiments/"+ expName + "/"; 
		String snr = "Results/Clean/" ;  // Should be variable
		String noiseType = "_";
		if(expName.equalsIgnoreCase("an4")) {
			noiseType = "_White";
		}
		String accModel1 = expName + "_Clean.cd_cont_200/Lattice/";
		String accModel2 = expName +noiseType + "10dB.cd_cont_200/Lattice/";
		String accModel3 = expName +noiseType + "15dB.cd_cont_200/Lattice/";
		String accModel4 = expName +noiseType + "20dB.cd_cont_200/Lattice/";
		String[] AcModel = {accModel1, accModel2,accModel3,accModel4};
		
		String outFile = baseDir +snr +"MDLFusing.txt";
		//PrintWriter out = new PrintWriter(outFile);
		
		// Read Lattices and store them to ArrayList<Lattice>
		String TestFileIds = expName + "_test.fileids";
		
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(new File(baseDir + TestFileIds));
		ArrayList<String> FinalResult =new ArrayList<String>(); 
		int count=0;
		while(scan.hasNextLine()) {
			count++;
			String fileName="";
			String uttId = "";
			String relPath = scan.nextLine();
			if(expName.equalsIgnoreCase("TIMIT")) {
				fileName = relPath.replace("/", "-");
			    fileName= fileName.replace(" ","");
			    uttId = relPath.substring(relPath.indexOf("/", relPath.indexOf("/")+1)+1, relPath.length());
				uttId = uttId.replace("/", "-");
				uttId = uttId.replace(" ","");

			}
			if(expName.equalsIgnoreCase("an4")) {
				fileName = relPath.substring(relPath.indexOf("/")+1, relPath.length());
				fileName = fileName.replace("/", "-");
				uttId = fileName;
			}
			//System.out.println("Lattice from: " + relPath);
			
			String testLattice = baseDir + snr;
			ArrayList<Lattice> lattices = new ArrayList<Lattice> ();
			for(int i= 0; i<4; i++) {
				String testFile = testLattice+AcModel[i]+fileName+".htk";
				//System.out.println("Lattice from: " + fileName);
				Lattice lattice = Lattice.readhtk(testFile);
				
				lattices.add(lattice);
			}
			
			// Combine lattices
			String hyp = FuseLattice.getFusedResutl(lattices);
			
			hyp = hyp+" (" + uttId + ")\n";
			FinalResult.add(hyp);
			//System.out.println(hyp);
			//StoreTextResult(hyp, out);
			System.out.print("*");
			
		}
		System.out.println();
		//System.out.println(FinalResult);
		StoreTextResult(FinalResult, outFile);
		
		Runtime.getRuntime().exec("/usr/bin/perl /src/azhar/MDV/word_align.pl");
	}


	/**
	 *  
	 * @param result
	 * @param fileName
	 * @return
	 * @throws IOException 
	 */
	private static void StoreTextResult(ArrayList<String> resultList, String outFile) throws IOException {

		FileWriter writer = new FileWriter(outFile); 
		for(String str: resultList) {
		  writer.write(str);
		}
		writer.close();
		
	}


}
