package algorithms.collageGenerators.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.Iterator;

import util.ArrayUtils;

import algorithms.Algorithm;
import algorithms.collageGenerators.CollageAlgorithmUtils;
import algorithms.collageGenerators.template.CollageAlgorithm;
import algorithms.collageGenerators.template.GeneticAlgorithm;
import algorithms.collageGenerators.template.GeneticAlgorithm.Chromosome;
import editor.data.ImageRefList;
import editor.data.ResultXML;

/**
 * Overrides CollageGA's mutate method for a smarter mutation (tho more expensive!)
 * 
 * The smart mutation uses RouletteWheel selection on all genes to determine which
 * gene is mutated, instead of mutating a random gene.
 * This should significantly reduce the low-quality genes, while maintaining the high-quality genes.
 * 
 * @author Kevin van As
 *
 */
public class CollageGA_indi extends CollageGA {
	
	private double[] caErrorArray;
	private boolean initPhase = true;
	//TODO: Run with parameters above to see what works
	
  //Extinction:
	private int nIniExtinctionWipes;
	private float pExtinction;
	/** Should we apply relative extinction? Note: this requires the checkAll algorithm to be executed before CollageGA_indi is executed. Consequently, this parameter cannot be changed after construction.*/
	private boolean applyRelativeExtinction;
	private double extinctErrorIniThreshold;// = 1E-6;
	private double extinctErrorThreshold;// = 1E-5;
	private double extinctErrorRelThreshold;// = 1E-3;
	
	public CollageGA_indi(int chromLength, int populationSize, float pCrossover, float lCrossover,
			float pMutate, float pMutateRepeat, int nElitism, double fitnessGoal, float pExtinction,
			boolean applyRelativeExtinction, double extinctErrorIniThreshold, double extinctErrorThreshold,
			double extinctErrorRelThreshold, int nIniExtinctionWipes,
			ImageRefList refList, ResultXML result) {
		super(chromLength, populationSize, pCrossover, lCrossover, pMutate, pMutateRepeat,
				nElitism, fitnessGoal, refList, result);
		
		this.extinctErrorIniThreshold = extinctErrorIniThreshold;
		this.extinctErrorRelThreshold = extinctErrorRelThreshold;
		this.extinctErrorThreshold = extinctErrorThreshold;
		this.pExtinction = pExtinction;
		this.applyRelativeExtinction = applyRelativeExtinction;
		this.nIniExtinctionWipes = nIniExtinctionWipes;
	}
		
	@Override
	protected void mutate(Chromosome chrom) {
		double[] errorArray = CollageAlgorithmUtils.constructErrorVector(chrom.data, colAlg);
//		System.out.println("Mutating " + chrom);
		
		//Possibly repeat the process:
		do{
			int randomID = rouletteWheel(errorArray); //random (Roulette Wheel) mutation spot
			chrom.data[randomID] = (int)(Math.random()*colAlg.keyList.size()); //change to a random image (possibly the same, but those odds are extremely small anyway)
		}while(shouldWe(this.pMutateRepeat));
		
//		System.out.println("@" + randomID + "; changed to " + chrom.data[randomID]);
//		System.out.println("Result   " + chrom);
	}
	
	/**
	 * Applies RouletteWheel selection to the given array, returning the winning element (index).
	 * 
	 * @author Kevin van As
	 * @param selectionArray
	 * @return the "winning" index
	 */
	protected int rouletteWheel(double[] selectionArray){
		double rnd = Math.random();
		double probSum = ArrayUtils.sum(selectionArray);
		
		if(probSum <= 0){
			System.err.println("GeneticAlgorithm (CollagaGA_indi) <<WARNING>>:" +
					" the total sum of the selectionArray is " + probSum +
					", while it should be >0 to apply RouletteWheel selection\n" +
					"Using uniform selection instead.");
			return (int)(rnd*selectionArray.length);
		}
		//Roulette Wheel selection
		rnd *= probSum; //random number \in{0,probSum}
		double accumulator = 0;
		for(int i = 0; i<selectionArray.length; i++){
			accumulator += selectionArray[i];
			if(rnd <= accumulator){
				//This is the winner:
				return i;
			}//else try the next index
		}
		//Not even a rounding error can get us here, can it???
		throw new Error("(CollageGA_indi.rouletteWheel) Cannot arrive at this line???");
		
				
	}
	
	@Override
	protected void onNewGenerationDone(int iterNumber, double bestFitness) {
		super.onNewGenerationDone(iterNumber, bestFitness);
		if(shouldWe(pExtinction)){
			extinct();
		}
	}
	
	/**
	 * Takes all genes with a squared error above <error_threshold> and randomly mutates them.
	 * Also takes all genes which are a factor <error_rel_threshold> above the optimal gene
	 * (as computed by the brute-forcing CheckAll algorithm, which runs in O(NxM) time,
	 * where N is the number of target cells (e.g. 2500 for 50x50) and M the number of available images).
	 */
	protected void extinct(){
		double error_threshold;
		double error_rel_threshold;
		if(initPhase){
			error_threshold = this.extinctErrorIniThreshold;
			error_rel_threshold = 0;
		}else{
			error_threshold = this.extinctErrorThreshold;
			error_rel_threshold = this.extinctErrorRelThreshold;
		}
//		System.out.println("Extinction.");
//		int numExtinctions = chromLength/10+1; //TODO??: Implement
		/*Chromosome bestChrom = getBestChromosome();*/
		double bestFitness = getBestFitness();
		Iterator<Chromosome> it = super.getChromosomesIterator();
		while(it.hasNext()){
			Chromosome chrom = it.next();
//		for(Chromosome chrom : chromosomes){
			if(chrom.getFitness()==bestFitness/*bestChrom.getFitness()*/) continue; //elitism with the very best -> don't throw our best solution away
//			boolean best;
//			if(chrom == getBestChromosome(chromosomes)){
//				best = true;
//			}else{
//				best = false;
//			}
			double[] errorArray = CollageAlgorithmUtils.constructErrorVector(chrom.data, colAlg);
//			int[] rndOrder = new int[chromLength]; //TODO??: Random order; inspired by CheckAllRO_Unique; numExtinctions
			for(int cell = 0; cell<getChromLength(); cell++){
//				System.out.println(errorArray[cell]+ " rel>? " + caErrorArray[cell] + " - " + (errorArray[cell] * error_rel_threshold > caErrorArray[cell]));
				if(
						errorArray[cell] > error_threshold ||
						(this.applyRelativeExtinction && errorArray[cell] * error_rel_threshold > caErrorArray[cell])
				  ){ //Bad gene! Extinct it!
					chrom.data[cell] = (int)(Math.random()*colAlg.keyList.size()); //mutate randomly
//					if(best){
//						System.out.println("mutate with error " + errorArray[cell]);
//					}
				}
			}	
			chrom.setFitness(this.fitness(chrom));
		}
		updateTotalFitness();
		updateBestFitness();
		
		
		updateExecutionTime();
		String bestFitnessStr = String.format("%.2f", bestFitness);
		System.out.println("ColGA(extinct) BestFitness = " + bestFitnessStr + "/" + super.fitnessGoal + ". Execution time = " + this.executionTime + ".");
	
	}
	

	/**
	 * Initialises the population using <nWipes> extinctions.
	 * With proper extinction parameters, this allows us to start with a 1200-fitness instead of 80-fitness solution.
	 * 
	 * (The normal genetic algorithm would need several minutes to reach 1200 for a 50x50 target with 130 images.)
	 * 
	 */
	@Override
	protected void onLoopBegin(){
		super.onLoopBegin();
		
		initPhase = true;	
		for(int wipe = 0; wipe<nIniExtinctionWipes; wipe++){
			this.extinct();
		}
		initPhase = false;
	}
	

	@Override
	public void reInit() {
		super.reInit();
		
	}

	@Override
	public void onAlgorithmBegin(){
		if(this.applyRelativeExtinction){
			ResultXML resultCA = colAlg.result.clone();
			//Apply the check-all algorithm to obtain a reference of the "perfect" solution in each cell.
			Algorithm alg = new CheckAll(resultCA, colAlg.refList);
			alg.run();
			caErrorArray = CollageAlgorithmUtils.getErrorVector(resultCA, colAlg.refList);
		}
		super.onAlgorithmBegin();
	}
	
	
}
