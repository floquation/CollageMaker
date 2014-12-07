package algorithms.collageGenerators.impl;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import util.ArrayUtils;

import algorithms.collageGenerators.CollageAlgorithmUtils;
import algorithms.collageGenerators.template.CollageAlgorithm;
import algorithms.collageGenerators.template.GeneticAlgorithm;
import editor.data.ImageRefList;
import editor.data.ResultXML;

public class CollageGA_Unique extends GeneticAlgorithm<Integer> {
	private CollageAlgorithm colAlg;
		
	private int crossoverWidth;
	private int crossoverHeight;
	private float pMutateRepeat;
	
	public CollageGA_Unique(int chromLength, int populationSize, float pCrossover,
			float pMutate, float pMutateRepeat, int nElitism, double fitnessGoal, ImageRefList refList, ResultXML result) {
		super(chromLength, populationSize, pCrossover, pMutate, nElitism, new Integer[0], fitnessGoal);
		colAlg = new CollageAlgorithm(refList,result);
		this.pMutateRepeat = pMutateRepeat;
				
	  //TODO: user-specifiable:
		crossoverWidth = (int)Math.ceil(0.3d*colAlg.result.grid.size.w);
		crossoverHeight = (int)Math.ceil(0.3d*colAlg.result.grid.size.h);
		
		reInit();
	}

/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ GENETIC ALGORITHM-SPECIFIC METHODS $$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	
	@Override
	protected void initChromosome(Chromosome chrom) {
		List<Integer> availableImages = new LinkedList<Integer>();
		availableImages.addAll(this.colAlg.keyList);
				
		for(int i = 0; i<getChromLength(); i++){
			int randomID_inAvailImages = (int)(Math.random()*availableImages.size());
			Integer theItem = availableImages.get(randomID_inAvailImages);
			int theID_inKeyList = colAlg.keyList.indexOf(theItem);
			chrom.data[i] = theID_inKeyList;
			
//			System.out.println("number="+chrom.data[i]+"index="+chrom.data[i]);
			availableImages.remove(theItem);
			
//			System.out.println("theItem="+theItem+"; getFromKeyList="+colAlg.keyList.get(chrom.data[i]));
		}
		
		System.out.println("(initChromosome): \n" + ArrayUtils.toString(chrom.data, colAlg.result.grid.size.w, 4));
		
		isUnique(chrom, "initChromosome(Chromosome chrom)"); //TODO: DEBUG TEST
	}
	
	/**
	 * DEBUG CLASS to determine whether a chromosome is truely unique.
	 * The test should never fail if the algorithm is correct.
	 * 
	 * @param chrom
	 * @param caller
	 */
	private void isUnique(Chromosome chrom, String caller){ //TODO: THIS IS A DEBUG CLASS
		for(int i = 0; i<getChromLength(); i++){
			for(int j = 0; j<getChromLength(); j++){
				if(i!=j && chrom.data[i] == chrom.data[j]){
					System.err.println("(COllageGA_Unique) <<ERROR>> NOT AN UNIQUE CHROMOSOME IN \""+caller+"\": " + chrom.data[i] + "@" + i + ","+j + "\n->INVALID ALGORITHM.");
					System.err.println(chrom.toString());
					System.exit(0);
				}
			}
		}
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
		
		//Possibly repeat the process (do-while):
		do{
			int randomID = (int)(Math.random()*getChromLength()); //random mutation spot
			int newGen = (int)(Math.random()*colAlg.keyList.size()); //change to a random image (possibly the same, but those odds are extremely small anyway)
			
			//See if 'newGen' is Unique
			for(int i = 0; i<chrom.data.length; i++){
				if(newGen == chrom.data[i]){ //newGen already exists at spot i
					chrom.data[i] = chrom.data[randomID]; //copy the to-be-mutated-gen to i
					break; //If the chromosome was unique, one match is the only match
				}
			}			
			chrom.data[randomID] = newGen; //unique!
		}while(shouldWe(this.pMutateRepeat));
		
//		System.out.println("@" + randomID + "; changed to " + chrom.data[randomID]);
//		System.out.println("Result   " + chrom);
		

		isUnique(chrom, "mutate(Chromosome chrom)"); //TODO: DEBUG TEST
	}

	@Override
	protected void crossover(Chromosome chromA, Chromosome chromB) {		
		//Take some data from chromB and copy it into chromA
		int x0 = (int)(Math.random()*colAlg.result.grid.size.w);
		int y0 = (int)(Math.random()*colAlg.result.grid.size.h);

//		System.out.println("before crossover = \n" + ArrayUtils.toString(chromA.data, colAlg.result.grid.size.w, 4));
//		System.out.println("crossover with   = \n" + ArrayUtils.toString(chromB.data, colAlg.result.grid.size.w, 4));
//		System.out.println("using (x0,y0) = (" + x0 + ", " + y0 + ");");
		
		for(int y_ = 0; y_ < crossoverHeight; y_++){
			int y = (y0 + y_)%colAlg.result.grid.size.h;
			for(int x_ = 0; x_ < crossoverWidth; x_++){
				int x = (x0 + x_)%colAlg.result.grid.size.w;
				int id = x+y*colAlg.result.grid.size.w;
				
				int newGen = chromB.data[id]; //the new gen (from chromB) for chromA

				//See if 'newGen' is Unique
				for(int i = 0; i<chromA.data.length; i++){
					if(newGen == chromA.data[i] && i != id){ //newGen already exists at spot i
//						System.out.println("id = " + i + "; chrom.data[i]= " + chromA.data[i] + " is replaced with " + chromA.data[id]);
						chromA.data[i] = chromA.data[id]; //copy the to-be-changed-gen to i
						break; //If the chromosome was unique, one match is the only match
					}
				}	
				
				chromA.data[id] = newGen; //change the to-be-changed-gen. Still unique!
//				System.out.println("changing: id="+id+", x="+x+", y="+y);
			}
		}
		
		isUnique(chromA, "crossover(Chromosome chromA, Chromosome chromB)"); //TODO: DEBUG TEST
		
//		System.out.println("after crossover = \n" + ArrayUtils.toString(chromA.data, colAlg.result.grid.size.w, 4));
		
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
	public void reInit() {
		super.reInit();
		colAlg.reInit();
		
		if(colAlg.keyList.size() < colAlg.result.grid.size.w*colAlg.result.grid.size.h){
			throw new IllegalArgumentException("There are too few images ("+colAlg.keyList.size()+") loaded to generate a solution consisting of solely unique images. " +
					"There are AT LEAST " + (colAlg.result.grid.size.w*colAlg.result.grid.size.h) + " images required - usually many more images are required to generate a nice collage.");
		}
		
	}

		
	
	
}
