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
		System.out.println("Start batch decoding " + expName + " in " + snr + " dB  Processing = 0.0%");
		String outFile = baseDir +snr +"MDLFusing.txt";
		String FusionTimeOutFile = baseDir +snr +"MDLFusingTime.csv";
		String noiseType = "_";
		int no_files = 0;
		if(expName.equalsIgnoreCase("an4")) {
			no_files  = 130;
		}
		else {
			no_files  = 1680;
		}
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
		int count=0;
		float prev_processing = 0;
		while(scan.hasNextLine()) {
			count++;
			float processing = 100*(count/no_files);
			if(processing - prev_processing > 0.95) {
				//Runtime.getRuntime().exec("cls");
				System.out.println("Start batch decoding " + expName + " in " + snr + " dB  Processing = "+ processing +"%");
				prev_processing = processing;
			}
			
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
			
			Lattice finalLatt = new Lattice();
			FuseLattice fl = new FuseLattice(finalLatt );
			
			long StartTime = System.nanoTime();
			String hyp = fl.getFusedResutlNoScaleAM(lattices);
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
			//System.out.print("*");
			
			
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
	
	/**
	 * This method fuse multiple scaled lattice from four noisy AMs. The lttices are scaled by {@value stsLogProb}
	 * that comes from STS-SNR estimator. 
	 *  
	 * @param expName
	 * @param snr
	 * @param baseDir
	 * @param stsLogProb
	 * @return
	 * @throws IOException
	 * 
	 * @author Azhar Sabah Abdulaziz
	 */
	public ArrayList<String> StartScaleAM(String expName, String snr, String baseDir, ArrayList<double[]> stsLogProb)  throws IOException{
		//String baseDir = "/Users/Azhar/Desktop/MDC_Experiments/"+ expName + "/"; 
		System.out.println("Start batch decoding " + expName + " in " + snr + " dB  Processing = 0.0%");
		String outFile = baseDir +snr +"MDLFusingScaleAM.txt";
		String FusionTimeOutFile = baseDir +snr +"MDLFusingTimeScaleAM.csv";
		String noiseType = "_";
		int no_files = 0;
		if(expName.equalsIgnoreCase("an4")) {
			no_files  = 130;
		}
		else {
			no_files  = 1680;
		}
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
		int count=0;
		float prev_processing = 0;
		int file_no = 0;
		while(scan.hasNextLine()) {
			
			// Guage Matter
			count++;
			float processing = 100*(count/no_files);
			if((processing - prev_processing) > 0.95) {
				//Runtime.getRuntime().exec("cls");
				System.out.println("Start batch decoding " + expName + " in " + snr + " dB  Processing = "+ processing +"%");
				prev_processing = processing;
			}
			
			
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
			// Get STS-SNR logProb to weight lattice path(links)
			double[] snrLogProb = stsLogProb.get(file_no);
			
			Lattice finalLatt = new Lattice();
			FuseLattice fl = new FuseLattice(finalLatt );
			// Combine lattices
			long StartTime = System.nanoTime();
			//String hyp = FuseLattice.getFusedResutlNoScaleAM(lattices);
			String hyp = fl.getFusedResutlScaleAM(lattices, snrLogProb);
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
			//System.out.print("*");
			
			
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
