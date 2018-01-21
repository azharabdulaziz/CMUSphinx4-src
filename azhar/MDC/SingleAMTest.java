package azhar.MDC;
import java.io.IOException;
import java.util.ArrayList;


public class SingleAMTest {
	/*
	 * 
	 */
	public static void main(String[] args) throws IOException {
		String expName = "an4";   // Could be variable
		String snr = "Results/Clean/" ;  // Should be variable
		String baseDir = "/Users/Azhar/Desktop/MDC_Experiments/"+ expName + "/";
		
		
		ArrayList<String> FinalResult = new ArrayList<>();
		BatchMultiLattFusion x = new	BatchMultiLattFusion();
		// For 5-50 dB input audio
		int snrVal = 5;
		snr = "Results/Noisy_"+Integer.toString(snrVal) +"db/";
		
		// For clean input audio
		//snr = "Results/Clean/";
		
		
		System.out.println("Start batch decoding " + expName + " in " + snr + " dB ");
		
		String AM_Type = "clean";
		float lmWeight = 1;
		FinalResult = x.StartForSingleAM(expName, snr, baseDir, AM_Type, lmWeight);
		
		
		/*for(int snrVal=0;snrVal<55;snrVal+=5) {
			if(snrVal == 0) snr = "Results/Clean/";
			else snr = "Results/Noisy_"+Integer.toString(snrVal) +"db/";
			
			System.out.println("Start batch decoding " + expName + " in " + snr + " dB ");
			FinalResult = x.StartForSingleAM(expName, snr, baseDir, "clean");
			
			//Runtime.getRuntime().exec("/usr/bin/perl /src/azhar/MDV/word_align.pl");
		}
		*/
		
		
	}


	

}
