package azhar.MDC;
import java.io.BufferedReader;
import java.io.FileReader;
/**
 * 
 */
import java.io.IOException;
import java.util.ArrayList;


public class MultDecoderlatticeFusion {
	/*
	 * 
	 */
	public static void main(String[] args) throws IOException {
		String expName = "an4";   // Could be variable
		String expBaseDir = "/Users/Azhar/Desktop/MDC_Experiments/"+ expName + "/";
		
		preMAPdecodeScaleAM(expName, expBaseDir,5,50);
	}

	private static void preMAPdecode(String expName, String expBaseDir, int inputNoiseStarts, int inputNoiseEnds) throws IOException {
		String snr = "" ;  // Should be variable
		//ArrayList<String> FinalResult = new ArrayList<>();
		BatchMultiLattFusion x = new	BatchMultiLattFusion();
		
		for(int snrVal=inputNoiseStarts;snrVal<=inputNoiseEnds;snrVal+=5) {
			if(snrVal == 0) snr = "Results/Clean/";
			else snr = "Results/Noisy_"+Integer.toString(snrVal) +"db/";
			
			//System.out.println("Start batch decoding " + expName + " in " + snr + " dB ");
			x.Start(expName, snr, expBaseDir);
			
			//Runtime.getRuntime().exec("/usr/bin/perl /src/azhar/MDV/word_align.pl");
		}
		
	}

	private static void preMAPdecodeScaleAM(String expName, String expBaseDir,int inputNoiseStarts, int inputNoiseEnds) throws IOException {
		
		String snr = "" ;  // Should be variable
		//ArrayList<String> FinalResult = new ArrayList<>();
		BatchMultiLattFusion x = new	BatchMultiLattFusion();
		
		for(int snrVal=inputNoiseStarts;snrVal<=inputNoiseEnds;snrVal+=5) {
			if(snrVal == 0) {
				snr = "Results/Clean/";
				x.Start(expName, snr, expBaseDir);
			}
			else {
				String csvFile = expBaseDir + "LLsResults/MDL_LLs"+ Integer.toString(snrVal) + "dB.csv";
				ArrayList<double[]> stsLogProb = getSTSlogProb(csvFile);
				snr = "Results/Noisy_"+Integer.toString(snrVal) +"db/";
				x.StartScaleAM(expName, snr, expBaseDir, stsLogProb);
			}
			
			//System.out.println("Start batch decoding " + expName + " in " + snr + " dB ");
			
			
			//Runtime.getRuntime().exec("/usr/bin/perl /src/azhar/MDV/word_align.pl");
		}
		
	}

	

	/**
	 * The STSlogProb comes from LLsResults folder, which is formed by LLSExtractor.
	 * <p>
	 * LLsExtractor : 
	 * <p>
	 * Takes the trained parameters table values from audio of different noise levels and estimates the 
	 * LogLiklihood of each file in the test part. It stores  MDL_LLsxdB.csv table, where x=5:5:50 dB SNR 
	 * in the folder ~/LLsResults/.
	 * <p>
	 * Those files has the following arrangement:
	 * <p>
	 * 
	 * 				S_1	S_2	S_3	S_4
	 * <p>
	 * 				——	——	——	——
	 * <p>
	 * 		file_1
	 * <p>
	 * 		file_2
	 * <p>
	 * 		.
	 * <p>
	 * 		.
	 * <p>
	 * 		.
	 * <p>
	 * 		last file in test
	 * <p>
	 * Where S_i = {<10, 15 ,20 , Clean or >20} AM. 
	 * 
	 * 
	 * @param csvFile: the absolute path of the MDL_LLsxdB.csv file
	 * @return 
	 * @author Azhar Sabah Abdulaziz
	 */
	
	public static ArrayList<double[]> getSTSlogProb(String csvFile) {
		String line = "";
        String cvsSplitBy = ",";
        //double[] STSlogProb = null;
        ArrayList<double[]> STSlogProb = new ArrayList<double[]>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] SNRlogProb = line.split(cvsSplitBy);
                Double A0 = Double.parseDouble(SNRlogProb[0]);
                Double A1 = Double.parseDouble(SNRlogProb[1]);
                Double A2 = Double.parseDouble(SNRlogProb[2]);
                Double A3 = Double.parseDouble(SNRlogProb[3]);
                double[] e = {A0/100000,A1/100000,A2/100000,A3/100000};
				/*System.out.println("AM10 = " + SNRlogProb[0] + "  AM15 =" + SNRlogProb[1] + 
                		"  AM20 = " + SNRlogProb[2] + "  Clean = " + SNRlogProb[3]);*/
                STSlogProb.add(e);
            }

        } catch (IOException e) {
        		System.err.println("Could not read CSV file");
            e.printStackTrace();
        }

		return STSlogProb;
	}

	

}
