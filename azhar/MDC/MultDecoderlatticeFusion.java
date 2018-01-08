package azhar.MDC;
import java.io.FileWriter;
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
		String expName = "TIMIT";   // Could be variable
		String snr = "Results/Clean/" ;  // Should be variable
		String baseDir = "/Users/Azhar/Desktop/MDC_Experiments/"+ expName + "/";
		
		
		ArrayList<String> FinalResult = new ArrayList<>();
		BatchMultiLattFusion x = new	BatchMultiLattFusion();
		
		for(int snrVal=0;snrVal<55;snrVal+=5) {
			if(snrVal == 0) snr = "Results/Clean/";
			else snr = "Results/Noisy_"+Integer.toString(snrVal) +"db/";
			
			System.out.println("Start batch decoding " + expName + " in " + snr + " dB ");
			FinalResult = x.Start(expName, snr, baseDir);
			
			//Runtime.getRuntime().exec("/usr/bin/perl /src/azhar/MDV/word_align.pl");
		}
		
	}


	

}
