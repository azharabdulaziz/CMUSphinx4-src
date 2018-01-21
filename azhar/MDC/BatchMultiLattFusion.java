package azhar.MDC;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import edu.cmu.sphinx.result.Lattice;

public class BatchMultiLattFusion {
	public ArrayList<String> Start(String expName, String snr, String baseDir)  throws IOException{
		//String baseDir = "/Users/Azhar/Desktop/MDC_Experiments/"+ expName + "/"; 
		String outFile = baseDir +snr +"MDLFusing.txt";
		String FusionTimeOutFile = baseDir +snr +"MDLFusingTime.csv";
		String noiseType = "_";
		if(expName.equalsIgnoreCase("an4")) {
			noiseType = "_White";
		}
		String accModel1 = expName + "_Clean.cd_cont_200/Lattice/";
		String accModel2 = expName +noiseType + "10dB.cd_cont_200/Lattice/";
		String accModel3 = expName +noiseType + "15dB.cd_cont_200/Lattice/";
		String accModel4 = expName +noiseType + "20dB.cd_cont_200/Lattice/";
		String[] AcModel = {accModel1, accModel2,accModel3,accModel4};
		
		
		//PrintWriter out = new PrintWriter(outFile);
		
		// Read Lattices and store them to ArrayList<Lattice>
		String TestFileIds = expName + "_test.fileids";
		
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(new File(baseDir + TestFileIds));
		ArrayList<String> FinalResult =new ArrayList<String>(); 
		ArrayList<String> TimeInMilli = new ArrayList<String>();
		//int count=0;
		while(scan.hasNextLine()) {
			//count++;
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
			long StartTime = System.nanoTime();
			String hyp = FuseLattice.getFusedResutlNoScaleAM(lattices);
			long EndTime = System.nanoTime();
			// Estimate elapsed time for attice fusion
			long FusionDuration = EndTime - StartTime;
			long TimeMS = TimeUnit.NANOSECONDS.toMillis(FusionDuration);
			String TimeInfo = fileName +","+ TimeMS+"\n";
			//System.out.println("Fusion Processing time: " + TimeInfo);
			TimeInMilli.add(TimeInfo);
			
			// store results
			hyp = hyp+" (" + uttId + ")\n";
			FinalResult.add(hyp);
			//System.out.println(hyp);
			//StoreTextResult(hyp, out);
			System.out.print("*");
			
			
		}
		
		System.out.println();
		System.out.println("Store decoded text results in: " + outFile); 
		System.out.println("Store fusion procesing time in: " + FusionTimeOutFile);
		System.out.println();
		//System.out.println(FinalResult);
		// Store decoding results
		StoreTextResult(FinalResult, outFile);
		
		//Store fusion processing time results
		StoreTextResult(TimeInMilli, FusionTimeOutFile);

		return FinalResult;
	}
	
	public ArrayList<String> StartForSingleAM(String expName, String snr, String baseDir, String WhichModel, float lmWeight)  throws IOException{
		//String baseDir = "/Users/Azhar/Desktop/MDC_Experiments/"+ expName + "/"; 
		String snrValue = snr.replace("/", "");
		
		String outFile = baseDir +snr +snrValue + "_" + WhichModel +".txt";
		System.out.println("Storing decoded text results in: " + outFile);
		//String FusionTimeOutFile = baseDir +snr +"MDLFusingTime.csv";
		String noiseType = "_";
		if(expName.equalsIgnoreCase("an4")) {
			noiseType = "_White";
		}
		String accModel1 = expName + "_Clean.cd_cont_200/Lattice/";
		String accModel2 = expName +noiseType + "10dB.cd_cont_200/Lattice/";
		String accModel3 = expName +noiseType + "15dB.cd_cont_200/Lattice/";
		String accModel4 = expName +noiseType + "20dB.cd_cont_200/Lattice/";
		String[] AcModel = {accModel1, accModel2,accModel3,accModel4};
		String AcousticModel = "";
		switch (WhichModel.toLowerCase()) {
		case "clean":
			AcousticModel = AcModel[0];
			break;
		default:
			System.err.println("Input acoustic model is not defined");
			break;
		}
		
		//PrintWriter out = new PrintWriter(outFile);
		
		// Read Lattices and store them to ArrayList<Lattice>
		String TestFileIds = expName + "_test.fileids";
		
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(new File(baseDir + TestFileIds));
		ArrayList<String> FinalResult =new ArrayList<String>(); 
		//ArrayList<String> TimeInMilli = new ArrayList<String>();
		//int count=0;
		while(scan.hasNextLine()) {
			//count++;
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
			
			//for(int i= 0; i<4; i++) {
				String testFile = testLattice+AcousticModel+fileName+".htk";
				//System.out.println("Lattice from: " + fileName);
				Lattice lattice = Lattice.readhtk(testFile);
				
				//lattices.add(lattice);
			//}
			
			// Combine lattices
			/*long StartTime = System.nanoTime();
			String hyp = FuseLattice.getFusedResutlNoScaleAM(lattices);
			long EndTime = System.nanoTime();
			// Estimate elapsed time for attice fusion
			long FusionDuration = EndTime - StartTime;
			long TimeMS = TimeUnit.NANOSECONDS.toMillis(FusionDuration);
			String TimeInfo = fileName +","+ TimeMS+"\n";
			//System.out.println("Fusion Processing time: " + TimeInfo);
			TimeInMilli.add(TimeInfo);
			*/
			String hyp = FuseLattice.getTextResultFromLattice(lattice, lmWeight);
			// store results
			hyp = hyp+" (" + uttId + ")\n";
			FinalResult.add(hyp);
			//System.out.println(hyp);
			//StoreTextResult(hyp, out);
			System.out.print("*");
			
			
		}
		
		System.out.println();
		System.out.println("Store decoded text results in: " + outFile); 
		//System.out.println("Store fusion procesing time in: " + FusionTimeOutFile);
		System.out.println();
		//System.out.println(FinalResult);
		// Store decoding results
		StoreTextResult(FinalResult, outFile);
		
		//Store fusion processing time results
		//StoreTextResult(TimeInMilli, FusionTimeOutFile);

		return FinalResult;
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
