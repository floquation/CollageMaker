package util;

public abstract class ArrayUtils {
	
	/**
	 * Finds the minimum value in the array.
	 * 
	 * @throws NullPointerException if the array has null elements or is null itself.
	 * @param array
	 * @return min(array)
	 */
	public static double min(double[][] array){
		double min = array[0][0];
		for(int i = 0; i<array.length; i++){
			min = Math.min(min, min(array[i]));
		}
		return min;
	}

	/**
	 * Finds the minimum value in the array.
	 * 
	 * @throws NullPointerException if the array has null elements or is null itself.
	 * @param array
	 * @return min(array)
	 */
	public static double min(double[] array){
		double min = array[0];
		for(int i = 0; i<array.length; i++){
			min = Math.min(min, array[i]);
		}
		return min;
	}

	/**
	 * Finds the minimum value in the array, ignore the value '0'.
	 * 
	 * @throws NullPointerException if the array has null elements or is null itself.
	 * @param array
	 * @return min(array)
	 */
	public static double minExclZero(double[][] array){
		double min = Double.MAX_VALUE;
		for(int i = 0; i<array.length; i++){
			min = Math.min(min, minExclZero(array[i]));
		}
		return min;
	}

	/**
	 * Finds the minimum value in the array, ignore the value '0'.
	 * 
	 * @throws NullPointerException if the array has null elements or is null itself.
	 * @param array
	 * @return min(array)
	 */
	public static double minExclZero(double[] array){
		double min = Double.MAX_VALUE;
		for(int i = 0; i<array.length; i++){
			if(array[i]!=0)
				min = Math.min(min, array[i]);
		}
		return min;
	}

	/**
	 * Finds the maximum value in the array.
	 * 
	 * @throws NullPointerException if the array has null elements or is null itself.
	 * @param array
	 * @return max(array)
	 */
	public static double max(double[][] array){
		double max = array[0][0];
		for(int i = 0; i<array.length; i++){
			max = Math.max(max, max(array[i]));
		}
		return max;
	}

	/**
	 * Finds the maximum value in the array.
	 * 
	 * @throws NullPointerException if the array has null elements or is null itself.
	 * @param array
	 * @return max(array)
	 */
	public static double max(double[] array){
		double max = array[0];
		for(int i = 0; i<array.length; i++){
			max = Math.max(max, array[i]);
		}
		return max;
	}

	/**
	 * Sums all elements in the double[][] array.
	 * 
	 * @author Kevin van As
	 * @param array
	 * @return
	 */
	public static double sum(double[][] array){
		double sum = 0;
		for(int i = 0; i<array.length; i++){
			sum += sum(array[i]);
		}
		return sum;
	}
	/**
	 * Sums all elements in the double[] array.
	 * 
	 * @author Kevin van As
	 * @param array
	 * @return
	 */
	public static double sum(double[] array){
		double sum = 0;
		for(int i = 0; i<array.length; i++){
			sum += array[i];
		}
		return sum;
		
	}
	
	/**
	 * Returns a 2D representation of "array",
	 * assuming it is a data structure representing a "w" times "array.length/w" 2D array.
	 * 
	 * @author Kevin van As
	 * @param array
	 * @param w
	 * @param formatLength: formats each number to the specified length
	 * @return
	 */
	public static String toString(Integer[] array, int w, int formatLength){
		String str = "";		
		for(int j = 0; j<array.length/w; j++){
			str += "[";
			for(int i = 0; i<w; i++){
				str += String.format("%"+formatLength+"d",array[i+j*w]) + ", ";
			}
			str = str.substring(0, str.length()-2)+"]\n";
		}
		return str.substring(0, str.length()-1);
	}
	
	/**
	 * Returns a 2D representation of "array",
	 * assuming it is a data structure representing a "w" times "array.length/w" 2D array.
	 * 
	 * @author Kevin van As
	 * @param array
	 * @param w
	 * @return
	 */
	public static String toString(Integer[] array, int w){
		String str = "";		
		for(int j = 0; j<array.length/w; j++){
			str += "[";
			for(int i = 0; i<w; i++){
				str += array[i+j*w] + ", ";
			}
			str = str.substring(0, str.length()-2)+"]\n";
		}
		return str.substring(0, str.length()-1);
	}
	
	/**
	 * Returns a 2D representation of "array".
	 * 
	 * @author Kevin van As
	 * @param array
	 * @return
	 */
	public static String toString(double[][] array){
		String str = "";
		for(int j = 0; j<array.length; j++){
			str += "[";
			for(int i = 0; i<array.length; i++){
				str += array[i][j] + ", ";
			}
			str = str.substring(0, str.length()-2)+"]\n";
		}
		return str.substring(0, str.length()-1);
	}

		

	/**
	 * Finds the minimum value in the array.
	 * 
	 * @throws NullPointerException if the array has null elements or is null itself.
	 * @param array
	 * @return min(array)
	 */
	public static int min(int[][] array){
		int min = array[0][0];
		for(int i = 0; i<array.length; i++){
			min = Math.min(min, min(array[i]));
		}
		return min;
	}

	/**
	 * Finds the minimum value in the array.
	 * 
	 * @throws NullPointerException if the array has null elements or is null itself.
	 * @param array
	 * @return min(array)
	 */
	public static int min(int[] array){
		int min = array[0];
		for(int i = 0; i<array.length; i++){
			min = Math.min(min, array[i]);
		}
		return min;
	}
	

	/**
	 * Finds the maximum value in the array.
	 * 
	 * @throws NullPointerException if the array has null elements or is null itself.
	 * @param array
	 * @return max(array)
	 */
	public static int max(int[][] array){
		int max = array[0][0];
		for(int i = 0; i<array.length; i++){
			max = Math.max(max, max(array[i]));
		}
		return max;
	}

	/**
	 * Finds the maximum value in the array.
	 * 
	 * @throws NullPointerException if the array has null elements or is null itself.
	 * @param array
	 * @return max(array)
	 */
	public static int max(int[] array){
		int max = array[0];
		for(int i = 0; i<array.length; i++){
			max = Math.max(max, array[i]);
		}
		return max;
	}

	/**
	 * Sums all elements in the int[][] array.
	 * 
	 * @author Kevin van As
	 * @param array
	 * @return
	 */
	public static int sum(int[][] array){
		int sum = 0;
		for(int i = 0; i<array.length; i++){
			sum += sum(array[i]);
		}
		return sum;
	}
	/**
	 * Sums all elements in the int[] array.
	 * 
	 * @author Kevin van As
	 * @param array
	 * @return
	 */
	public static int sum(int[] array){
		int sum = 0;
		for(int i = 0; i<array.length; i++){
			sum += array[i];
		}
		return sum;
		
	}
	

	
}
