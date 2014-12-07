package algorithms.collageGenerators.implBackUp;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import editor.data.ImageRefList;
import editor.data.ResultXML;

public class CopyOfCheckAllRO_Unique {

	private long timeIni = -1;
	
	/** Switch to false to terminate the loop at the end of the current iteration */
	public boolean continueLooping = true;
	
	private int[][] imgData;
	private double[][] imgErrorSq;
	
	private ResultXML result;
	private ImageRefList refList;
	private List<Integer> keyList;
	private Set<Integer> availableImages;	
	
	public CopyOfCheckAllRO_Unique(ResultXML result, ImageRefList refList){
		this.refList = refList;
		this.result = result;

		keyList = new LinkedList<Integer>();
		keyList.addAll(refList.keySet());
		
		if(keyList.size() < result.grid.size.w*result.grid.size.h){
			throw new IllegalArgumentException("There are too few images ("+keyList.size()+") loaded to generate a solution consisting of solely unique images. " +
					"There are AT LEAST " + (result.grid.size.w*result.grid.size.h) + " images required - usually many more images are required to generate a nice collage.");
		}

		availableImages = new HashSet<Integer>();
		
		imgData = new int[result.grid.size.w][result.grid.size.h];
		imgErrorSq = new double[result.grid.size.w][result.grid.size.h];

	}
	
	/**
	 * Brute-force over the entire image and all possibilities.
	 * Unicity is ensured by not brute-forcing over previously-used images:
	 * 	This may create big errors for the later images; But the algorithm is extremely cheap!
	 * 
	 * The solution is written on-the-fly.
	 * Early termination will give a partially generated image.
	 * 
	 * @author Kevin van As
	 */
	public void loop(){
		//Clear an eventual previous result
		for(int x = 0; x<imgData.length; x++){ //loop horizontally over the target image
			for(int y = 0; y<imgData[x].length; y++){ //loop vertically over the target image
				result.grid.elements[x][y].imgId = -1;				
			}
		}
		availableImages.clear();
		
		timeIni = System.nanoTime();
		availableImages.addAll(keyList);
	  //Shuffle the cells to obtain a random order
		int nCells = result.grid.size.w*result.grid.size.h;
		List<Double> rndArray = new LinkedList<Double>();
		for(int i = 0; i<nCells; i++){
			rndArray.add(Math.random());
		}
		Double[] sortedArray = rndArray.toArray(new Double[0]);
		Arrays.sort(sortedArray);
	  //Determine best fit for the cells in their random order
		for(int cellIndex = 0; cellIndex<nCells; cellIndex++){
			int cell = rndArray.indexOf(sortedArray[cellIndex]);
			int x = cell%result.grid.size.w;
			int y = (cell-x)/result.grid.size.w;
			
			System.out.print("("+cell+":"+x+","+y+")");
			Color targetCol = result.grid.elements[x][y].color;
			//Compute the initial guess fitness:
			imgErrorSq[x][y] = Double.MAX_VALUE;
			//Try and find a better fit:
			int bestImgID = -1;
			for(int imgID : availableImages){ //loop over all possible images to be used at (x,y)
				Color testCol = refList.getImage(imgID).getColor();
				double errorSq = errorSq(targetCol,testCol,result);
				//If it is better, update the image
				if(errorSq<imgErrorSq[x][y]){
					bestImgID = imgID;
					result.grid.elements[x][y].imgId = imgID;
					imgErrorSq[x][y] = errorSq;
				}
			}
//			System.out.print(" targetCol = " + targetCol.toString() + "; testCol = " + refList.getImage(bestImgID).getColor());
			availableImages.remove(bestImgID); //This image is taken, the next cell may not use it
			System.out.println(" Achieved ErrorSq: " + imgErrorSq[x][y] + "; Execution time = " + ((System.nanoTime()-timeIni)/1000000000d) + ";");
			if(!continueLooping){
				onTerminate();
				return;
			}
		}
		onFinish();
	}

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
	private static double errorSq(Color color1, Color color2, ResultXML resultXML){	
		double scale = 1e-3/(2700d*resultXML.grid.size.w*resultXML.grid.size.h);
		
		double greenError = color1.getGreen()-color2.getGreen();
		double redError = color1.getRed()-color2.getRed();
		double blueError = color1.getBlue()-color2.getBlue();
		
		return (greenError*greenError+redError*redError+blueError*blueError)*scale;
	}
	
	/**
	 * Method is called when the loop successfully terminates without interference.
	 * 
	 * @author Kevin van As
	 */
	protected void onFinish(){
		System.out.println("(CheckAllRO_Unique) finished. Execution time = " + ((System.nanoTime()-timeIni)/1000000000d) + ";");
		//TODO: final statements
	}
	
	/**
	 * Method is called when the user terminates the loop by using "continueLooping=false".
	 * 
	 * @author Kevin van As
	 */
	protected void onTerminate(){
		continueLooping = true;
		
		System.out.println("(CheckAllRO_Unique) terminated. Execution time = " + ((System.nanoTime()-timeIni)/1000000000d) + ";");
		//TODO: final statements
	}
	
}
