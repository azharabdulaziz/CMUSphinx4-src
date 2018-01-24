package AzharTestPackage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import edu.cmu.sphinx.util.LogMath;

public class LLS_ReadTest {
	public static void main(String[] args) {
		String csvFile = "/Users/Azhar/Desktop/MDC_Experiments/an4/LLsResults/MDL_LLs50dB.csv";
        ArrayList<double[]> sts = getLinearSTSlogProb(csvFile, 100000);
        
        /*double[] x = sts.get(1);
    		System.out.println("A0 = " + x[0]);
    		*/
        Iterator<double[]> iter = sts.iterator();
        while(iter.hasNext()) {
        	double[] STSfile = iter.next();
        	System.out.println("A0 = " + STSfile[0]+"     A1 = " + STSfile[1]+"     A2 = " + STSfile[2]
        			+"     A3 = " + STSfile[3]);
        }
    
	}

	private static void showSTSlogProb(String csvFile) {
		String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] SNRlogProb = line.split(cvsSplitBy);

                System.out.println("AM10 = " + SNRlogProb[0] + "  AM15 =" + SNRlogProb[1] + 
                		"  AM20 = " + SNRlogProb[2] + "  Clean = " + SNRlogProb[3]);

            }

        } catch (IOException e) {
        		System.err.println("Could not read CSV file");
            e.printStackTrace();
        }

		
	}
	

	/**
	 * The STSlogProb comes from LLsResults folder, which is formed by LLSExtractor.
	 * LLsExtractor : 
	 * Takes the trained parameters table values from audio of different noise levels and estimates the 
	 * LogLiklihood of each file in the test part. It stores  MDL_LLsxdB.csv table, where x=5:5:50 dB SNR 
	 * in the folder ~/LLsResults/
	 * Those files has the following arrangement:
	 * 				S_1	S_2	S_3	S_4
	 * 				——	——	——	——
	 * 		file_1
	 * 		file_2
	 * 		.
	 * 		.
	 * 		.
	 * 		last file in test
	 * 
	 * Where S_i = {<10, 15 ,20 , Clean or >20} AM. 
	 * 
	 * 
	 * @param csvFile: the absolute path of the MDL_LLsxdB.csv file
	 * @return 
	 * @author Azhar Sabah Abdulaziz
	 */
	public static ArrayList<double[]> getSTSlogProb(String csvFile) {
		LogMath logMath  = LogMath.getLogMath();
		String line = "";
        String cvsSplitBy = ",";
        //double[] STSlogProb = null;
        ArrayList<double[]> STSlogProb = new ArrayList<double[]>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] SNRlogProb = line.split(cvsSplitBy);
                double A0 = Double.parseDouble(SNRlogProb[0]);
                double A1 = Double.parseDouble(SNRlogProb[1]);
                double A2 = Double.parseDouble(SNRlogProb[2]);
                double A3 = Double.parseDouble(SNRlogProb[3]);
                double[] e = {A0,A1,A2,A3};
                 
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
	


	/**
	 * The STSlogProb comes from LLsResults folder, which is formed by LLSExtractor.
	 * LLsExtractor : 
	 * Takes the trained parameters table values from audio of different noise levels and estimates the 
	 * LogLiklihood of each file in the test part. It stores  MDL_LLsxdB.csv table, where x=5:5:50 dB SNR 
	 * in the folder ~/LLsResults/
	 * Those files has the following arrangement:
	 * 				S_1	S_2	S_3	S_4
	 * 				——	——	——	——
	 * 		file_1
	 * 		file_2
	 * 		.
	 * 		.
	 * 		.
	 * 		last file in test
	 * 
	 * Where S_i = {<10, 15 ,20 , Clean or >20} AM. 
	 * 
	 * 
	 * @param csvFile: the absolute path of the MDL_LLsxdB.csv file
	 * @return 
	 * @author Azhar Sabah Abdulaziz
	 * @param w 
	 */
	public static ArrayList<double[]> getLinearSTSlogProb(String csvFile, float w) {
		LogMath logMath  = LogMath.getLogMath();
		String line = "";
        String cvsSplitBy = ",";
        //double[] STSlogProb = null;
        ArrayList<double[]> STSlogProb = new ArrayList<double[]>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] SNRlogProb = line.split(cvsSplitBy);
                double A0 = Double.parseDouble(SNRlogProb[0]);
                double A1 = Double.parseDouble(SNRlogProb[1]);
                double A2 = Double.parseDouble(SNRlogProb[2]);
                double A3 = Double.parseDouble(SNRlogProb[3]);
                
                
                double A0_linear = logMath.logToLinear(logMath.log10ToLog((float)A0/w));
                double A1_linear = logMath.logToLinear(logMath.log10ToLog((float)A1/w));
                double A2_linear = logMath.logToLinear(logMath.log10ToLog((float)A2/w));
                double A3_linear = logMath.logToLinear(logMath.log10ToLog((float)A3/w));
                
                
               /* double A0_linear = logMath.log10ToLog((float)A0)/1000;
                double A1_linear = logMath.log10ToLog((float)A1)/1000;
                double A2_linear = logMath.log10ToLog((float)A2)/1000;
                double A3_linear = logMath.log10ToLog((float)A3)/1000;
               */ 
                double[] e = {A0_linear,A1_linear,A2_linear,A3_linear};
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
