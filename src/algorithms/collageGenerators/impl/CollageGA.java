package algorithms.collageGenerators.impl;

import java.awt.Color;

import algorithms.collageGenerators.CollageAlgorithmUtils;
import algorithms.collageGenerators.template.CollageAlgorithm;
import algorithms.collageGenerators.template.GeneticAlgorithm;
import editor.data.ImageRefList;
import editor.data.ResultXML;

public class CollageGA extends GeneticAlgorithm<Integer> {
	protected CollageAlgorithm colAlg;
		
	protected int crossoverWidth;
	protected int crossoverHeight;
	protected float pMutateRepeat;
	
	public CollageGA(int chromLength, int populationSize, float pCrossover, float lCrossover,
			float pMutate, float pMutateRepeat, int nElitism, double fitnessGoal, ImageRefList refList, ResultXML result) {
		super(chromLength, populationSize, pCrossover, pMutate, nElitism, new Integer[0], fitnessGoal);
		colAlg = new CollageAlgorithm(refList,result);
		this.pMutateRepeat = pMutateRepeat;
				
	  //TODO: user-specifiable:
		crossoverWidth = (int)Math.ceil(lCrossover*colAlg.result.grid.size.w);
		crossoverHeight = (int)Math.ceil(lCrossover*colAlg.result.grid.size.h);
		
		reInit();
	}

/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ GENETIC ALGORITHM-SPECIFIC METHODS $$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	
	@Override
	protected void initChromosome(Chromosome chrom) {
		for(int i = 0; i<getChromLength(); i++){
			chrom.data[i] = (int)(Math.random()*colAlg.keyList.size());
		}
		
//		System.out.println("(initChromosome): " + chrom);
	}

	/**
	 * Return the fitness as 1/(scale*(error_blue^2+error_green^2+error_red^2)),
	 * with scale a numerical constant which forces the typical fitness near 1.
	 * 
	 * @author Kevin van As
	 */
	@Override
	protected double fitness(Chromosome chrom) {
		double errorSq = 0;
		
		//Compare the color for each gridElement
		for(int i = 0; i<getChromLength(); i++){
			int x = i%colAlg.result.grid.size.w;
			int y = (i-x)/colAlg.result.grid.size.w;
		
			Color colorFit = colAlg.refList.getImage(colAlg.keyList.get(chrom.data[i])).getColor(); //TODO: null-pointer if the refList is changed concurrently such that the requested ID no longer exists.
			Color colorTarget = colAlg.result.grid.elements[x][y].color;
			
			errorSq += CollageAlgorithmUtils.errorSq(colorFit,colorTarget,colAlg.result);
		}
		
//		System.out.println("(CollageGA) Fitness = " + 1/errorSq);
		
		//Fitness is propto 1/error (ignore the sqrt).
		if(errorSq == 0)
			return Double.MAX_VALUE;
		return 1/errorSq;
	}

	@Override
	protected void mutate(Chromosome chrom) {
//		System.out.println("Mutating " + chrom);
		
		//Possibly repeat the process:
		do{
			int randomID = (int)(Math.random()*getChromLength()); //random mutation spot
			chrom.data[randomID] = (int)(Math.random()*colAlg.keyList.size()); //change to a random image (possibly the same, but those odds are extremely small anyway)
		}while(shouldWe(this.pMutateRepeat));
		
//		System.out.println("@" + randomID + "; changed to " + chrom.data[randomID]);
//		System.out.println("Result   " + chrom);
	}

	@Override
	protected void crossover(Chromosome chromA, Chromosome chromB) {
		//Take some data from chromB and copy it into chromA
		int x0 = (int)(Math.random()*colAlg.result.grid.size.w);
		int y0 = (int)(Math.random()*colAlg.result.grid.size.h);

//		System.out.println("before crossover = " + chromA);
//		System.out.println("crossover with   = " + chromB);
//		System.out.println("using (x0,y0) = (" + x0 + ", " + y0 + ");");
		
		for(int y_ = 0; y_ < crossoverHeight; y_++){
			int y = (y0 + y_)%colAlg.result.grid.size.h;
			for(int x_ = 0; x_ < crossoverWidth; x_++){
				int x = (x0 + x_)%colAlg.result.grid.size.w;
				int id = x+y*colAlg.result.grid.size.w;
				chromA.data[id] = chromB.data[id];
//				System.out.println("changing: id="+id+", x="+x+", y="+y);
			}
		}
		
//		System.out.println("after crossover = " + chromA);
		
	}

/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ OTHER OVERRIDES $$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	
	@Override
	protected void onAlgorithmFinish() {
		super.onAlgorithmFinish();
		writeResultToStruct();		
	}
	
	@Override
	protected void onAlgorithmTerminate() {
		super.onAlgorithmTerminate();
		writeResultToStruct();		
	}
	
	/**
	 * Writes the best chromosome's elements to result.
	 * 
	 * @author Kevin van As
	 */
	private void writeResultToStruct(){
		//Write data to result	
		Chromosome bestChrom = this.getBestChromosome();
		//Iterate over the data
		for(int i = 0; i<getChromLength(); i++){
			int x = i%colAlg.result.grid.size.w;
			int y = i/colAlg.result.grid.size.w;
			colAlg.result.grid.elements[x][y].imgId = colAlg.keyList.get(bestChrom.data[i]);
		}
	}
	
	@Override
	protected void onNewGenerationDone(int iterNumber, double bestFitness) {
		String bestFitnessStr = String.format("%.2f", bestFitness);
		System.out.println("ColGA("+iterNumber+"/"+super.maxNumIter+") BestFitness = " + bestFitnessStr + "/" + super.fitnessGoal + ". Execution time = " + this.executionTime + ".");
	}
	
	@Override
	protected void onIterEnd(){}
	
	@Override
	protected void onLoopBegin(){
		System.out.println("(CollageGA) Starting looping.");
	}

	@Override
	public void reInit() {colAlg.reInit();super.reInit();}

		
	
	
}
