//package algorithms.collageGenerators.implBackUp;
//
//import java.awt.Color;
//import java.util.Arrays;
//import java.util.LinkedList;
//import java.util.List;
//
//import algorithms.collageGenerators.impl.GeneticAlgorithm;
//import algorithms.collageGenerators.impl.GeneticAlgorithm.Chromosome;
//
//import editor.data.ImageRefList;
//import editor.data.ResultXML;
//
//public class CopyOfCollageGA extends GeneticAlgorithm<Integer> {
//	
//	private long timeIni = -1;
//
//	ImageRefList refList;
//	ResultXML resultXML;
//	List<Integer> keyList;
//	
//	private int crossoverWidth;
//	private int crossoverHeight;
//	private float pMutateRepeat;
//	
//	public CopyOfCollageGA(int chromLength, int populationSize, float pCrossover,
//			float pMutate, float pMutateRepeat, int nElitism, ImageRefList refList, ResultXML resultXML) {
//		super(chromLength, populationSize, pCrossover, pMutate, nElitism, new Integer[0]);
//		this.refList = refList;
//		this.resultXML = resultXML;
//		this.pMutateRepeat = pMutateRepeat;
//				 		
//		keyList = new LinkedList<Integer>();
//		keyList.addAll(refList.keySet());
//				
//		//TODO: user-specifiable
//		crossoverWidth = (int)Math.ceil(0.3d*resultXML.grid.size.w);
//		crossoverHeight = (int)Math.ceil(0.3d*resultXML.grid.size.h);
//	}
//
//	@Override
//	protected void initChromosome(Chromosome chrom) {
//		for(int i = 0; i<chromLength; i++){
//			chrom.data[i] = (int)(Math.random()*keyList.size());
//		}
//		
//		System.out.println("(initChromosome): " + chrom);
//	}
//
//	/**
//	 * Return the fitness as 1/(scale*(error_blue^2+error_green^2+error_red^2)),
//	 * with scale a numerical constant which forces the typical fitness near 1.
//	 * 
//	 * @author Kevin van As
//	 */
//	@Override
//	protected double fitness(Chromosome chrom) {
//		double errorSq = 0;
//		
//		//Compare the color for each gridElement
//		for(int i = 0; i<chromLength; i++){
//			int x = i%resultXML.grid.size.w;
//			int y = (i-x)/resultXML.grid.size.w;
//		
//			Color colorFit = refList.getImage(keyList.get(chrom.data[i])).getColor(); //TODO: null-pointer if the refList is changed concurrently such that the requested ID no longer exists.
//			Color colorTarget = resultXML.grid.elements[x][y].color;
//			
//			errorSq += errorSq(colorFit,colorTarget,resultXML);
//		}
//		
////		System.out.println("(CollageGA) Fitness = " + 1/errorSq);
//		
//		//Fitness is propto 1/error (ignore the sqrt).
//		if(errorSq == 0)
//			return Double.MAX_VALUE;
//		return 1/errorSq;
//	}
//
//	@Override
//	protected void mutate(Chromosome chrom) {
////		System.out.println("Mutating " + chrom);
//		
//		//Possibly repeat the process:
//		do{
//			int randomID = (int)(Math.random()*chromLength); //random mutation spot
//			chrom.data[randomID] = (int)(Math.random()*keyList.size()); //change to a random image (possibly the same, but those odds are extremely small anyway)
//		}while(shouldWe(this.pMutateRepeat));
//		
////		System.out.println("@" + randomID + "; changed to " + chrom.data[randomID]);
////		System.out.println("Result   " + chrom);
//	}
//
//	@Override
//	protected void crossover(Chromosome chromA, Chromosome chromB) {
//		//Take some data from chromB and copy it into chromA
//		int x0 = (int)(Math.random()*resultXML.grid.size.w);
//		int y0 = (int)(Math.random()*resultXML.grid.size.h);
//
////		System.out.println("before crossover = " + chromA);
////		System.out.println("crossover with   = " + chromB);
////		System.out.println("using (x0,y0) = (" + x0 + ", " + y0 + ");");
//		
//		for(int y_ = 0; y_ < crossoverHeight; y_++){
//			int y = (y0 + y_)%resultXML.grid.size.h;
//			for(int x_ = 0; x_ < crossoverWidth; x_++){
//				int x = (x0 + x_)%resultXML.grid.size.w;
//				int id = x+y*resultXML.grid.size.w;
//				chromA.data[id] = chromB.data[id];
////				System.out.println("changing: id="+id+", x="+x+", y="+y);
//			}
//		}
//		
////		System.out.println("after crossover = " + chromA);
//		
//	}
//
//	@Override
//	protected void onFinish() {
//		//Write data to resultXML	
//		Chromosome bestChrom = this.getBestChromosome();
//		//Iterate over the data
//		for(int i = 0; i<chromLength; i++){
//			int x = i%resultXML.grid.size.w;
//			int y = i/resultXML.grid.size.w;
//			resultXML.grid.elements[x][y].imgId = keyList.get(bestChrom.data[i]);
//		}
//		
//	}
//	
//	@Override
//	protected void onIterEnd(int iterNumber, double bestFitness) {
//		float execTime = (System.nanoTime()-timeIni)/1000000000f;
//		String bestFitnessStr = String.format("%.2f", bestFitness);
//		System.out.println("ColGA("+iterNumber+"/"+maxIter+") BestFitness = " + bestFitnessStr + "/" + fitnessGoal + ". Execution time = " + execTime + ".");
//	}
//	
//	@Override
//	protected void onLoopBegin(){
//		timeIni = System.nanoTime();
//		System.out.println("(CollageGA) Starting looping.");
//	}
//	
//	/**
//	 * Returns the square error between the two given colors
//	 * 
//	 * @author Kevin van As
//	 * @param color1
//	 * @param color2
//	 * @param resultXML; used to scale the result depending on grid size
//	 * @return (double) square error
//	 */
//	private static double errorSq(Color color1, Color color2, ResultXML resultXML){	
//		double scale = 1e-3/(2700d*resultXML.grid.size.w*resultXML.grid.size.h);
//		
//		double greenError = color1.getGreen()-color2.getGreen();
//		double redError = color1.getRed()-color2.getRed();
//		double blueError = color1.getBlue()-color2.getBlue();
//		
//		return (greenError*greenError+redError*redError+blueError*blueError)*scale;
//	}
//	
//	/**
//	 * It is static, such that we can compute it based on solely resultXML's colorMap:
//	 * No chromosomes are needed anyway!
//	 * Useful to compute it when the file is loaded --> chromosomes are lost.
//	 * 
//	 * @author Kevin van As
//	 * @param resultXML : contains the generated result and the target color
//	 * @param refList : to find the color of the fit images
//	 * @return errorSq: Array of squared errors
//	 */
//	public static double[][] getErrorArray(ResultXML resultXML, ImageRefList refList) {		
//		double[][] errorSq = new double[resultXML.grid.size.w][resultXML.grid.size.h];
//		
//		//Compare the color for each gridElement
//		for(int y = 0; y<resultXML.grid.size.h; y++){
//			for(int x = 0; x<resultXML.grid.size.w; x++){
//				
//				Color colorFit = refList.getImage(resultXML.grid.elements[x][y].imgId).getColor(); //TODO: null-pointer if the refList is changed concurrently such that the requested ID no longer exists.
//				Color colorTarget = resultXML.grid.elements[x][y].color;
//				
//				
//				errorSq[x][y] = errorSq(colorFit, colorTarget, resultXML);
//			}			
//		}
//			
//		return errorSq;
//	}
//	
//	
//	
//}
