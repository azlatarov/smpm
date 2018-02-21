package custom.utils.math.extended;

import java.util.Arrays;

public class jvuMathExtended {
	
	private jvuMathExtended(){}
	
	public static int findMedian (int[] in) {
		Arrays.sort(in);
		int length = in.length;
		return (length % 2 == 0) ? in[length/2] : in[(length-1)/2];
	}
	
}
