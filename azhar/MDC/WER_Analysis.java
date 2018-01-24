package azhar.MDC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.cmu.sphinx.util.NISTAlign;

public class WER_Analysis {

	public static void main(String[] args) {
		//randomTest();
		String expName = "an4";
		String baseDir = "/Users/Azhar/Desktop/MDC_Experiments/";
		
		
		String refFile = baseDir+expName+"/RefClean.txt";
		String hypFileName = "MDLFusingScaleAM.txt";
		ArrayList<String> Ref = readLines(refFile);
		ArrayList<String> AlignmentResults = new ArrayList<String>();
		int inputNoiseStarts=5;
		int inputNoiseEnds=50;
		String snr;
		
		for(int snrVal=inputNoiseStarts;snrVal<=inputNoiseEnds;snrVal+=5) {
			if(snrVal == 0) snr = "/Results/Clean/";
			else snr = "/Results/Noisy_"+Integer.toString(snrVal) +"db/";
			
			String hypFile = baseDir + expName + snr + hypFileName;
			System.out.println("HypeFile is: " + hypFile);
			ArrayList<String> Hyp = readLines(hypFile);
			AlignmentResults = getWER(Ref,Hyp);
			System.out.println();
		}
		
	}
	

	/**
	 * Aligned Hypothesis
	 * Reference
	 * Words: 2 Correct: 0 Errors: 7 Percent correct = 0.00% Error = 350.00% Accuracy = -250.00%
	 * Insertions: 5 Deletions: 0 Substitutions: 2

	 * @param ref
	 * @param hyp
	 * @return
	 */
	private static ArrayList<String> getWER(ArrayList<String> ref, ArrayList<String> hyp) {
		NISTAlign aligner = new NISTAlign(true,true);
		ArrayList<String> Results = new ArrayList<String> ();
		int no_utt = ref.size();
		for(int i=0; i<no_utt; i++) {
			String Ref = ref.get(i);
			String Hyp = hyp.get(i);
			aligner.align(Ref.toUpperCase(), Hyp.toUpperCase());
			/*String ahyp = aligner.getAlignedHypothesis();
			String aref = aligner.getAlignedReference();*/
			
			
			
		}
		
		
		aligner.printTotalSummary();
		
		return Results;
	}


	private static ArrayList<String> readLines(String textFile) {
		ArrayList<String> text = new ArrayList<String>();
		
		 try (BufferedReader br = new BufferedReader(new FileReader(textFile))) {
			 
	            String line;
				while ((line = br.readLine()) != null) {
					text.add(line);
	            }

	        } catch (IOException e) {
	        		System.err.println("Could not read"+ textFile+" file");
	            e.printStackTrace();
	        }

		 
		return text;
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

	private static void randomTest() {
		NISTAlign aligner = new NISTAlign(true,true);
		String hypothesis = "RUBOUT G E N E S B NINE (fcaw-an406-fcaw-b)" ;
		String reference = "rubout g m e f three nine (fcaw-AN406-FCAW-B)";
		aligner.align(reference.toUpperCase(), hypothesis.toUpperCase());
		int del = aligner.getTotalDeletions();
		int sub = aligner.getTotalSubstitutions();
		int ins = aligner.getTotalInsertions();
		int totalError = aligner.getTotalWordErrors();
		int totalWords = aligner.getTotalWords();
		String hypo = aligner.getHypothesis();
		
		System.out.println("Deletion: " + del + " Insertions: " + ins + "  Substitions: "+ sub);
		System.out.println("Total Word Error: " + totalError);
		System.out.println("Total Words: "+ totalWords);
		System.out.println("Hypothesis: " + hypo);
		
	}
}
