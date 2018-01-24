package AzharTestPackage;

import edu.cmu.sphinx.util.LogMath;

public class TestShpinxLogMath {

	public static void main(String[] args) {
		LogMath logMath = LogMath.getLogMath();
		
		double x= -36.35;
		double y = -11182;
		double xy = x+y;
		System.out.println("Linear Value x:" + x+ "  LogMath = " + logMath.log10ToLog((float)x));

		System.out.println("Linear Value y:" + y+ "  LogMath = " + logMath.log10ToLog((float)y));
		
		System.out.println("Linear Value: x+y= " + xy + "  LogMath = " + logMath.log10ToLog((float)xy));
	}

}
