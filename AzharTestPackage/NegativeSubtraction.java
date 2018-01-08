package AzharTestPackage;

public class NegativeSubtraction {
	public static void main(String[] args) {
		double x = -12;
		double y = -10;
		double z = DiffBelowZero(x,y);
		double z1 = x-y;
		System.out.println("Subtract without the functio: "+ Double.toString(z1));
		System.out.println("Result is: " + Double.toString(z));
		
	}

	/**
	 * This method gives the difference of two negative double numbers (Below zero).
	 * 
	 * @param x 
	 * @param y
	 * @return
	 * @author Azhar Sabah Abdulaziz
	 */
	private static double DiffBelowZero(double x, double y) {
		// TODO Auto-generated method stub
		boolean  sign_x = (x>0 ? true:false);
		boolean sign_y = (y>0 ? true:false);
		double abs_x = Math.abs(x);
		double abs_y = Math.abs(y);
		double abs_result = (abs_x > abs_y ? abs_x - abs_y : abs_y-abs_x);
		boolean sign_result = (abs_x > abs_y ? sign_x : sign_y);
		double result = abs_result;
		if(!sign_result) result = -1*result; // only if sign is negative
		if(abs_result == 0) result = 0;
		return result;
	}

}
