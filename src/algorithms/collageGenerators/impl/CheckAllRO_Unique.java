package algorithms.collageGenerators.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import algorithms.ForIndexAlgorithm;
import algorithms.collageGenerators.CollageAlgorithmUtils;
import algorithms.collageGenerators.template.CollageAlgorithm;
import editor.data.ImageRefList;
import editor.data.ResultXML;

public class CheckAllRO_Unique extends ForIndexAlgorithm {
	private CollageAlgorithm colAlg;
	
	/** Switch to false to terminate the loop at the end of the current iteration */
	public boolean continueLooping = true;
	
	private int[][] imgData;
	private double[][] imgErrorSq;
	
	private Set<Integer> availableImages;	
	private Double[] sortedArray = null;
	private List<Double> rndArray = null;
	
	public CheckAllRO_Unique(ResultXML result, ImageRefList refList){
		super(0, result.grid.size.w*result.grid.size.h, 1);	
		colAlg = new CollageAlgorithm(refList,result);
		
		reInit();
		

	}
	
	public void reInit(){
		super.reInit();
		colAlg.reInit();
		if(colAlg.keyList.size() < colAlg.result.grid.size.w*colAlg.result.grid.size.h){
			throw new IllegalArgumentException("There are too few images ("+colAlg.keyList.size()+") loaded to generate a solution consisting of solely unique images. " +
					"There are AT LEAST " + (colAlg.result.grid.size.w*colAlg.result.grid.size.h) + " images required - usually many more images are required to generate a nice collage.");
		}

		availableImages = new HashSet<Integer>();
		
		imgData = new int[colAlg.result.grid.size.w][colAlg.result.grid.size.h];
		imgErrorSq = new double[colAlg.result.grid.size.w][colAlg.result.grid.size.h];
	}	
	
	/**
	 * Brute-force over the entire image and all possibilities, but then in a cheap simplified matter.
	 * 
	 * Unicity is ensured by not brute-forcing over previously-used images:
	 * 	This may create big errors for the later images/elements; But the algorithm is extremely cheap!
	 * 
	 * The solution is written on-the-fly.
	 * Early termination will give a partially generated image.
	 * 
	 * @author Kevin van As
	 */
	protected void doLoopIteration(int cellIndex){
	    //Determine best fit for the cells in their random order
		int cell = rndArray.indexOf(sortedArray[cellIndex]);
		int x = cell%colAlg.result.grid.size.w;
		int y = (cell-x)/colAlg.result.grid.size.w;
		
		System.out.print("("+this.getFormattedProgress()+") ("+cell+":"+x+","+y+")");
		Color targetCol = colAlg.result.grid.elements[x][y].color;
		//Compute the initial guess fitness:
		imgErrorSq[x][y] = Double.MAX_VALUE;
		//Try and find a better fit:
		int bestImgID = -1;
		for(int imgID : availableImages){ //loop over all possible images to be used at (x,y)
			Color testCol = colAlg.refList.getImage(imgID).getColor();
			double errorSq = CollageAlgorithmUtils.errorSq(targetCol,testCol,colAlg.result);
			//If it is better, update the image
			if(errorSq<imgErrorSq[x][y]){
				bestImgID = imgID;
				colAlg.result.grid.elements[x][y].imgId = imgID;
				imgErrorSq[x][y] = errorSq;
			}
		}
//			System.out.print(" targetCol = " + targetCol.toString() + "; testCol = " + refList.getImage(bestImgID).getColor());
		availableImages.remove(bestImgID); //This image is taken, the next cell may not use it
		System.out.println(" Achieved ErrorSq: " + imgErrorSq[x][y] + "; Execution time = " + this.executionTime + ";");
	}

	@Override
	protected void onAlgorithmBegin() {
		//Clear an eventual previous result
		for(int x = 0; x<imgData.length; x++){ //loop horizontally over the target image
			for(int y = 0; y<imgData[x].length; y++){ //loop vertically over the target image
				colAlg.result.grid.elements[x][y].imgId = -1;				
			}
		}
		availableImages.clear();
		
		availableImages.addAll(colAlg.keyList);
	  //Shuffle the cells to obtain a random order
		int nCells = colAlg.result.grid.size.w*colAlg.result.grid.size.h;
		rndArray = new LinkedList<Double>();
		for(int i = 0; i<nCells; i++){
			rndArray.add(Math.random());
		}
		sortedArray = rndArray.toArray(new Double[0]);
		Arrays.sort(sortedArray);
		
		continueLooping = true;
	}
	
	/**
	 * Method is called when the loop successfully terminates without interference.
	 * 
	 * @author Kevin van As
	 */
	@Override
	protected void onAlgorithmFinish() {
		System.out.println("(CheckAllRO_Unique) finished. Execution time = " + this.executionTime + ";");
		//TODO: final statements
	}
	
	/**
	 * Method is called when the user terminates the loop by using "continueLooping=false".
	 * 
	 * @author Kevin van As
	 */
	@Override
	protected void onAlgorithmTerminate() {
		continueLooping = true;
		
		System.out.println("(CheckAllRO_Unique) terminated. Execution time = " + this.executionTime + ";");
		//TODO: final statements
	}

	@Override
	protected boolean shouldTerminate() {
		return !continueLooping;
	}
	
	
}
