package algorithms.collageGenerators.impl;

import java.awt.Color;

import algorithms.ForIndexAlgorithm;
import algorithms.collageGenerators.CollageAlgorithmUtils;
import algorithms.collageGenerators.template.CollageAlgorithm;
import editor.data.ImageRefList;
import editor.data.ResultXML;

public class CheckAll extends ForIndexAlgorithm{
	private CollageAlgorithm colAlg;

	/** Switch to false to terminate the loop at the end of the current iteration */
	public boolean continueLooping = false;
	
	private int[][] imgData;
	private double[][] imgErrorSq;
	
	public CheckAll(ResultXML result, ImageRefList refList){
		super(0, result.grid.size.w*result.grid.size.h, 1);	
		colAlg = new CollageAlgorithm(refList,result);
		
		reInit();
	}

	@Override
	protected void doLoopIteration(int cell) {
		int x = cell%colAlg.result.grid.size.w;
		int y = (cell-x)/colAlg.result.grid.size.w;
		System.out.print("("+this.getFormattedProgress()+") ("+x+","+y+")");
		Color targetCol = colAlg.result.grid.elements[x][y].color;
		//Compute the initial guess fitness:
		colAlg.result.grid.elements[x][y].imgId = colAlg.keyList.get(0);
		imgErrorSq[x][y] = CollageAlgorithmUtils.errorSq(targetCol,colAlg.refList.getImage(colAlg.keyList.get(0)).getColor(),colAlg.result);
		//Try and find a better fit:
		for(int imgID = 1; imgID<colAlg.keyList.size(); imgID++){ //loop over all possible images to be used at (x,y)
			Color testCol = colAlg.refList.getImage(colAlg.keyList.get(imgID)).getColor();
			double errorSq = CollageAlgorithmUtils.errorSq(targetCol,testCol,colAlg.result);
			//If it is better, update the image
			if(errorSq<imgErrorSq[x][y]){
				colAlg.result.grid.elements[x][y].imgId = colAlg.keyList.get(imgID);
				imgErrorSq[x][y] = errorSq;
			}
		}
		System.out.println(" Achieved ErrorSq: " + imgErrorSq[x][y] + "; Execution time = " + this.executionTime + ";");
		if(shouldTerminate()){
			return;
		}
	}

	@Override
	protected boolean shouldTerminate() {
		return !continueLooping;
	}

	@Override
	public void reInit() {
		super.reInit();
		colAlg.reInit();
		imgData = new int[colAlg.result.grid.size.w][colAlg.result.grid.size.h];
		imgErrorSq = new double[colAlg.result.grid.size.w][colAlg.result.grid.size.h];
	}

	@Override
	protected void onAlgorithmBegin() {
		//Clear an eventual previous result
		for(int x = 0; x<imgData.length; x++){ //loop horizontally over the target image
			for(int y = 0; y<imgData[x].length; y++){ //loop vertically over the target image
				colAlg.result.grid.elements[x][y].imgId = -1;				
			}
		}
		
		continueLooping = true;
	}

	@Override
	protected void onAlgorithmFinish() {
		System.out.println("(CheckAll) finished. Execution time = " + this.executionTime + ";");
	}

	@Override
	protected void onAlgorithmTerminate() {
		System.out.println("(CheckAll) terminated. Execution time = " + this.executionTime + "; Progress = " + this.getFormattedProgress() + ";");
	}
	
}
