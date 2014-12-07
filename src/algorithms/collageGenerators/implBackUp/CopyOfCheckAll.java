package algorithms.collageGenerators.implBackUp;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import editor.data.ImageRefList;
import editor.data.ResultXML;

public class CopyOfCheckAll {

	private long timeIni = -1;
	
	/** Switch to false to terminate the loop at the end of the current iteration */
	public boolean continueLooping = true;
	
	private int[][] imgData;
	private double[][] imgErrorSq;
	
	private ResultXML result;
	private ImageRefList refList;
	private List<Integer> keyList;
	
	public CopyOfCheckAll(ResultXML result, ImageRefList refList){
		this.refList = refList;
		this.result = result;
		
		imgData = new int[result.grid.size.w][result.grid.size.h];
		imgErrorSq = new double[result.grid.size.w][result.grid.size.h];

		keyList = new LinkedList<Integer>();
		keyList.addAll(refList.keySet());
	}
	
	/**
	 * Brute-force over the entire image and all possibilities.
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
		
		timeIni = System.nanoTime();
		for(int x = 0; x<imgData.length; x++){ //loop horizontally over the target image
			for(int y = 0; y<imgData[x].length; y++){ //loop vertically over the target image
				System.out.print("("+x+","+y+")");
				Color targetCol = result.grid.elements[x][y].color;
				//Compute the initial guess fitness:
				result.grid.elements[x][y].imgId = keyList.get(0);
				imgErrorSq[x][y] = errorSq(targetCol,refList.getImage(keyList.get(0)).getColor(),result);
				//Try and find a better fit:
				for(int imgID = 1; imgID<keyList.size(); imgID++){ //loop over all possible images to be used at (x,y)
					Color testCol = refList.getImage(keyList.get(imgID)).getColor();
					double errorSq = errorSq(targetCol,testCol,result);
					//If it is better, update the image
					if(errorSq<imgErrorSq[x][y]){
						result.grid.elements[x][y].imgId = keyList.get(imgID);
						imgErrorSq[x][y] = errorSq;
					}
				}
				System.out.println(" Achieved ErrorSq: " + imgErrorSq[x][y] + "; Execution time = " + ((System.nanoTime()-timeIni)/1000000000d) + ";");
				if(!continueLooping){
					onTerminate();
					return;
				}
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
		System.out.println("(CheckAll) finished. Execution time = " + ((System.nanoTime()-timeIni)/1000000000d) + ";");
		//TODO: final statements
	}
	
	/**
	 * Method is called when the user terminates the loop by using "continueLooping=false".
	 * 
	 * @author Kevin van As
	 */
	protected void onTerminate(){
		continueLooping = true;
		
		System.out.println("(CheckAll) terminated. Execution time = " + ((System.nanoTime()-timeIni)/1000000000d) + ";");
		//TODO: final statements
	}
	
}
