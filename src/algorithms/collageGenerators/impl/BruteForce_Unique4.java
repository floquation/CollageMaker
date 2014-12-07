package algorithms.collageGenerators.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import algorithms.collageGenerators.template.GeneticAlgorithm.Chromosome;


import editor.data.ImageRefList;
import editor.data.ResultXML;

public class BruteForce_Unique4 {

	private long timeIni = -1;
	
	/** Switch to false to terminate the loop at the end of the current iteration */
	public boolean continueLooping = true;
	
	private int[][] imgData;
	private int[][] imgDataBest;
	
	private ResultXML result;
	private ImageRefList refList;
	private List<Integer> keyList;
	private AvailableImages[] available;
	private Set<Integer> availableImages;
	
	/**
	 * Work-around class, such that we can make an array of a Set<Integer>
	 * 
	 * @author Kevin van As
	 */
	private class AvailableImages{
		protected Set<Integer> images;
		
		protected AvailableImages(){
			images = new HashSet<Integer>();
		}
	}
	
	public BruteForce_Unique4(ResultXML result, ImageRefList refList){
		this.refList = refList;
		this.result = result;
		
		keyList = new LinkedList<Integer>();
		keyList.addAll(refList.keySet());
		availableImages = new HashSet<Integer>();
		available = new AvailableImages[result.grid.size.w*result.grid.size.h];
		
		if(keyList.size() < result.grid.size.w*result.grid.size.h){
			throw new IllegalArgumentException("There are too few images ("+keyList.size()+") loaded to generate a solution consisting of solely unique images. " +
					"There are AT LEAST " + (result.grid.size.w*result.grid.size.h) + " images required - usually many more images are required to generate a nice collage.");
		}
		
		imgData = new int[result.grid.size.w][result.grid.size.h];

	}
	
	/**
	 * Brute-force over the entire image and all possibilities.
	 * The solution is written on-the-fly.
	 * Early termination will give a partially generated image.
	 *  
	 * @author Kevin van As
	 */
	public void loop(){
		//Clear an eventual previous result:
		currentBestError = Double.MAX_VALUE;
		
		//Prepare for the current loop:
		int nCells = result.grid.size.w*result.grid.size.h;
		for(int cell = 0; cell<nCells; cell++){
			available[cell] = new AvailableImages();
		}
		availableImages.addAll(keyList);
		timeIni = System.nanoTime();
		
	//(Note that the constructor ensures that there are enough images to create a unique solution)
				
		recursionLoop(nCells-1); //this method computes the best result (until termination)
		onFinish(); //this method writes to "result"
		
		//Dispose:
		for(int cell = 0; cell<nCells; cell++){
			available[cell].images.clear();
		}		
	}

	/**
	 * 
	 * @param cell
	 * @return shouldTerminate?
	 */
	private boolean recursionLoop(int cell){
		if(cell<0) return onLeafNode();
		
		int x = cell%result.grid.size.w;
		int y = (cell-x)/result.grid.size.w;
		
		if((cell+1)%3==0){
			System.out.print("Start looping for cell " + cell);
			System.out.print(" --- currentBestError = " + currentBestError);
			System.out.println(" --- Execution time = " + (System.nanoTime()-timeIni)/1000000000d + " [s].");
		}
		
		available[cell].images.clear();
		available[cell].images.addAll(this.availableImages);
		for(int imgID : available[cell].images){
			availableImages.remove(imgID);
			this.imgData[x][y] = imgID;
			//perform the recursion and terminate the algorithm if the leaf node says so
			if(recursionLoop(cell-1))return true;
			availableImages.add(imgID);
		}
		
		return false;
	}
	
	private double currentBestError;
	private boolean onLeafNode(){
		double currentError = totalError();
		if(currentError<currentBestError){
			currentBestError = currentError;
			imgDataBest = imgData.clone();
		}
		return shouldTerminate();
	}
	
	/**
	 * Used to determine whether a convergence criterion or a user-termination criterion has been reached.
	 * 
	 * @author Kevin van As
	 * @return true if the loop should terminate
	 */
	private boolean shouldTerminate(){
		return false;
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
	 * Return the total errorSq (scale*(error_blue^2+error_green^2+error_red^2),
	 * with scale a numerical constant which forces the typical fitness near 1.
	 * 
	 * @author Kevin van As
	 */
	private double totalError() {
		double errorSq = 0;
		
		//Compare the color for each gridElement
		for(int x = 0; x<imgData.length; x++){ //loop horizontally over the target image
			for(int y = 0; y<imgData[x].length; y++){ //loop vertically over the target image
			
				Color colorFit = refList.getImage(imgData[x][y]).getColor(); //TODO: null-pointer if the refList is changed concurrently such that the requested ID no longer exists.
				Color colorTarget = result.grid.elements[x][y].color;
				
				errorSq += errorSq(colorFit,colorTarget,result);
			}
		}
		
		return errorSq;
	}
	
	/**
	 * Method is called when the loop successfully terminates without interference.
	 * 
	 * @author Kevin van As
	 */
	protected void onFinish(){
		//Write the current result to the result file, provided one was generated.
		if(this.imgDataBest != null){
			for(int x = 0; x<result.grid.size.w; x++){
				for(int y = 0; y<result.grid.size.h; y++){
					result.grid.elements[x][y].imgId = this.imgDataBest[x][y];				
				}
			}
		}
		
		System.out.println("(BruteForce) finished. Execution time = " + ((System.nanoTime()-timeIni)/1000000000d) + ";");
	}
	
//	/**
//	 * Method is called when the user terminates the loop by using "continueLooping=false".
//	 * 
//	 * @author Kevin van As
//	 */
//	protected void onTerminate(){
//		continueLooping = true;
//		
//		System.out.println("(BruteForce) terminated. Execution time = " + ((System.nanoTime()-timeIni)/1000000000d) + ";");
//		//TODO: final statements
//	}
	
	
}
