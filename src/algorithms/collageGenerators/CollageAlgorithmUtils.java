package algorithms.collageGenerators;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import algorithms.collageGenerators.template.CollageAlgorithm;
import algorithms.collageGenerators.template.GeneticAlgorithm.Chromosome;

import editor.data.ImageRefList;
import editor.data.ResultXML;

public abstract class CollageAlgorithmUtils {

	/**
	 * Returns the square error between the two given colors.
	 * Used as a representative for the fitness of the solution.
	 * 
	 * @author Kevin van As
	 * @param color1
	 * @param color2
	 * @param resultXML; used to scale the result depending on grid size
	 * @return (double) square error
	 */
	public static double errorSq(Color color1, Color color2, ResultXML resultXML){	
		double scale = 1e-3/(2700d*resultXML.grid.size.w*resultXML.grid.size.h);
		
		double greenError = color1.getGreen()-color2.getGreen();
		double redError = color1.getRed()-color2.getRed();
		double blueError = color1.getBlue()-color2.getBlue();
		
		return (greenError*greenError+redError*redError+blueError*blueError)*scale;
	}
	
	/**
	 * Computes an array filled with the error of each individual image.
	 * 
	 * It is static, such that we can compute it based on solely resultXML's colorMap:
	 * No algorithm-specific things are needed!
	 * 
	 * @author Kevin van As
	 * @param resultXML : contains the generated result and the target color
	 * @param refList : to find the color of the fit images
	 * @return errorSq: Array of squared errors
	 */
	public static double[][] getErrorArray(ResultXML resultXML, ImageRefList refList) {		
		double[][] errorSq = new double[resultXML.grid.size.w][resultXML.grid.size.h];
		
		//Compare the color for each gridElement
		for(int y = 0; y<resultXML.grid.size.h; y++){
			for(int x = 0; x<resultXML.grid.size.w; x++){
				
				Color colorFit = refList.getImage(resultXML.grid.elements[x][y].imgId).getColor(); //TODO: null-pointer if the refList is changed concurrently such that the requested ID no longer exists.
				Color colorTarget = resultXML.grid.elements[x][y].color;
				
				
				errorSq[x][y] = errorSq(colorFit, colorTarget, resultXML);
			}			
		}
			
		return errorSq;
	}
	
	/**
	 * Computes a vector containing the errors of the images represented in {@code data}.
	 * {@code colAlg.result} is used for the size of the grid (to convert 1D vector to 2D array format) and to read the ColorMap of the target image.
	 * {@code colAlg.refList} is used to obtain image colors
	 * {@code colAlg.keyList} is a fixed-order List which converts the indices in data to imgIDs to be read by {@code colAlg.refList}.
	 * And thus {@code data} contains indices of the {@code colAlg.keyList} array, not actual imgIDs.
	 * 
	 * @param data
	 * @param colAlg
	 * @return
	 */
	public static double[] constructErrorVector(Integer[] data, CollageAlgorithm colAlg){
		double[] errorArray = new double[data.length];
		
		//Compare the color for each gridElement
		for(int i = 0; i<data.length; i++){
			int x = i%colAlg.result.grid.size.w;
			int y = (i-x)/colAlg.result.grid.size.w;
		
			Color colorFit = colAlg.refList.getImage(colAlg.keyList.get(data[i])).getColor(); //TODO: null-pointer if the refList is changed concurrently such that the requested ID no longer exists.
			Color colorTarget = colAlg.result.grid.elements[x][y].color;
			
			errorArray[i] = CollageAlgorithmUtils.errorSq(colorFit, colorTarget, colAlg.result);
		}
		return errorArray;
	}

	
	/**
	 * Computes an array filled with the error of each individual image.
	 * 
	 * It is static, such that we can compute it based on solely resultXML's colorMap:
	 * No algorithm-specific things are needed!
	 * 
	 * @author Kevin van As
	 * @param resultXML : contains the generated result and the target color
	 * @param refList : to find the color of the fit images
	 * @return errorSq: Vector of squared errors
	 */
	public static double[] getErrorVector(ResultXML resultXML, ImageRefList refList) {		
		double[] errorSq = new double[resultXML.grid.size.w*resultXML.grid.size.h];
		
		//Compare the color for each gridElement
		for(int y = 0; y<resultXML.grid.size.h; y++){
			for(int x = 0; x<resultXML.grid.size.w; x++){
				int cell = x+resultXML.grid.size.w*y;
				
				Color colorFit = refList.getImage(resultXML.grid.elements[x][y].imgId).getColor(); //TODO: null-pointer if the refList is changed concurrently such that the requested ID no longer exists.
				Color colorTarget = resultXML.grid.elements[x][y].color;
				
				
				errorSq[cell] = errorSq(colorFit, colorTarget, resultXML);
			}			
		}
			
		return errorSq;
	}

	/**
	 * Counts how frequently each image from the <result> has been used in <result>.
	 * Writes it to an array of size <grid.size>, representing the unicity of each element in the <result>.
	 * 
	 * @param result
	 * @param refList
	 * @return int[result.grid.size.w][result.grid.size.h] of occurence frequencies of the given element
	 */
	public static int[][] getGlobalUnicityArray(ResultXML result, ImageRefList refList){
		if(result.grid.elements == null) return null;
		
		//Get the counting map
		Map<Integer,Integer> countMap = getGlobalUnicityMap(result,refList);
		
		//Construct array of counts
		int[][] array = new int[result.grid.size.w][result.grid.size.h];
		for(int y = 0; y<result.grid.size.h; y++){
			for(int x = 0; x<result.grid.size.w; x++){
				int key = result.grid.elements[x][y].imgId; //get the imageID (key for map)
				array[x][y] = countMap.get(key); //copy count to array position
			}
		}
		
		return array;
	}
	
	/**
	 * Counts how frequently each image in <refList> has been used in <result>.
	 * Writes it to a HashMap with keys equal to the imageIDs.
	 * 
	 * @param result
	 * @param refList
	 * @return Map<Integer,Integer> containing imageIDs as keys and the frequency of occurence as value.
	 */
	public static Map<Integer,Integer> getGlobalUnicityMap(ResultXML result, ImageRefList refList){
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		
		//Prepare with zero counts
		Set<Integer> keySet = refList.keySet();
		for(Integer i : keySet){
			map.put(i, 0);
		}
				
		//Count:
		for(int y = 0; y<result.grid.size.h; y++){
			for(int x = 0; x<result.grid.size.w; x++){
				int key = result.grid.elements[x][y].imgId;
				Integer oldVal = map.get(key);
				map.remove(key);
				map.put(key, oldVal+1);
			}
		}
		
		return map;
	}
	
}
