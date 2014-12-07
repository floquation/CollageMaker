package algorithms.collageGenerators.template;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import algorithms.LoopingAlgorithm;

/**
 * Generics for a genetic algorithm.
 * 
 * @author Kevin van As
 *
 * @param <T> is the dataType, for example, if your chromosome consists of an array of floats, then "T=Float".
 */
public abstract class GeneticAlgorithm<T> extends LoopingAlgorithm {
	
	private T[] sampleArray;
	/**
	 * Struct yielding the chromosome data. It is trusted that no external application can reach this class.
	 * 
	 * @author Kevin van As
	 *
	 */
	public class Chromosome{
		public T[] data;
		/** Fitness of the chromosome. It should be > 0.
		 * If rounding errors get it to 0, that's okay. Then it simply has zero probability to multiply itself. */
		private double fitness = 0;
		
		public void setFitness(double f){
			fitness = Math.max(0d, f);
		}
		public double getFitness(){
			return fitness;
		}
		
		public Chromosome(){
			data = Arrays.copyOf(sampleArray, getChromLength());
		}
		
		public String toString(){
			return "Chromosome("+fitness+", "+Arrays.toString(data)+")";
		}
		
		/**
		 * @author Kevin van As
		 * @return an exact copy of this chromosome
		 */
		public Chromosome duplicate(){
			Chromosome copy = new Chromosome();
			copy.data = this.data.clone();
			copy.fitness = this.fitness;
			return copy;
		}
	}
	
	/** Sum({@code chromosome.fitness}). Should be re-computed at the very end of each iteration.*/
	private double fitnessSum = 0;
	/** list holding the current population of {@code Chromosome}s */
	private Set<Chromosome> chromosomes;
	/** temporary list for constructing the new population of {@code Chromosome}s*/
	private Set<Chromosome> chromosomesNew;
	
	private int chromLength;
	protected int populationSize;
	protected float pCrossover;
	protected float pMutate;
	protected int nElitism;
	
	protected double fitnessGoal;
	
	protected double bestFitness = Double.MIN_VALUE;
	
/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ CONSTRUCTORS $$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	
	/**
	 * Prepares the parameters of the Genetic Algorithm and corrects invalid values automatically.
	 * Warnings are raised when invalid parameters are supplied.
	 * 
	 * @author Kevin van As
	 * @param chromLength
	 * @param populationSize
	 * @param pCrossover
	 * @param pMutate
	 * @param nElitism
	 */
	public GeneticAlgorithm(int chromLength, int populationSize, float pCrossover,
			float pMutate, int nElitism, T[] sampleArray, double fitnessGoal){
		chromosomes = new HashSet<Chromosome>();
		chromosomesNew = new HashSet<Chromosome>();
		this.chromLength = Math.max(0, chromLength);
		this.populationSize = Math.max(0, populationSize);
		this.pCrossover = Math.max(0f, Math.min(1f, pCrossover));
		this.pMutate = Math.max(0f, Math.min(1f, pMutate));
		this.nElitism = Math.max(0, Math.min(nElitism,populationSize));
		if(sampleArray == null) throw new NullPointerException("\"sampleArray\" must not be null!");
		this.sampleArray = sampleArray;
		this.fitnessGoal = fitnessGoal;
		
		//Sanity warnings:
		if(this.pCrossover != pCrossover) System.err.println("GeneticAlgorithm <<WARNING>>: Corrected 'pCrossover' from " + pCrossover + " to " + this.pCrossover + ". A probability should be bounded by 0 and 1.");
		if(this.pCrossover != pCrossover) System.err.println("GeneticAlgorithm <<WARNING>>: Corrected 'pCrossover' from " + pMutate + " to " + this.pMutate + ". A probability should be bounded by 0 and 1.");
		if(this.nElitism != nElitism) System.err.println("GeneticAlgorithm <<WARNING>>: Corrected 'nElitism' from " + nElitism + " to " + this.nElitism + ". It must be between '0' and 'populationSize'. As a heuristic, 3 < nElitism << populationSize for the best performance of the algorithm.");
		if(this.getChromLength() == 0) System.err.println("GeneticAlgorithm -> \"Chromosome length\" is set to 0. The algorithm is meaningless if ran with effectively no chromosomes.\n" +
				"This would automatically imply we need the maximum number of iterations to achieve nothing.");
		if(this.populationSize == 0) System.err.println("GeneticAlgorithm -> \"Population size\" is set to 0. The algorithm is meaningless if ran with effectively no chromosomes.\n" +
				"This would automatically imply we need the maximum number of iterations to achieve nothing.");
		
	}

/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ GENETIC ALGORITHM-SPECIFIC METHODS $$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	
	/**
	 * Creates an initial population with user-specified initial data via the
	 * {@code initChromosome(Chromosome chrom)} method.
	 * And calculates the Total Fitness to be used for the Roulette Wheel selection.
	 * 
	 * @author Kevin van As
	 */
	protected void initPopulation(){
		Chromosome chrom;
		for(int i = 0; i<populationSize; i++){
			chrom = new Chromosome();
			initChromosome(chrom);
			chrom.fitness = fitness(chrom);
			this.addChromosome(chrom);
		}
		updateTotalFitness();
	}
		
	/**
	 * Initializes the Chromosome's data to some value.
	 * The user may do this randomly, or using heuristics to make a decent initial guess.
	 * 
	 * The initial chromosome MUST necessarily satisfy the rules of the application
	 * (for example, the numbers must be unique if your application requires so).
	 * 
	 * @author Kevin van As
	 * @param chrom
	 * @return 
	 */
	protected abstract void initChromosome(Chromosome chrom);
	
	/**
	 * Adds the best 'nElitism' chromosomes from `chromosomes' to `chromosomesNew'.
	 * 
	 * @author Kevin van As
	 */
	protected void elitism(){
		if(nElitism > chromosomes.size() || nElitism < 0){
			throw new IllegalArgumentException("GeneticAlgorithm -> \"nElitism\" should be between 0 and the population size.");
		}
		
//		ArraySet<Chromosome> bestChroms = new ArraySet<Chromosome>(nElitism);
//		List<Chromosome> bestChroms = new ArrayList<Chromosome>(nElitism);
		
		Collection<Chromosome> remainder = new HashSet<Chromosome>();
		remainder.addAll(chromosomes);
		
		for(int i = 0; i<nElitism; i++){
			Chromosome best = getBestChromosome(remainder);
			remainder.remove(best);
			chromosomesNew.add(best);
		}
		
		remainder.clear();
	}
	
	/**
	 * Apply Roulette Wheel selection based on the Chromosomes' fitness.
	 * Applies a uniform selection if fitnessSum = 0.
	 * 
	 * @author Kevin van As
	 * @return the selected Chromosome
	 */
	protected Chromosome rouletteWheel(){		
		Iterator<Chromosome> it = chromosomes.iterator();
		double rnd = Math.random();
		
		if(fitnessSum <= 0){
			//Raise a warning and use an uniform selection
			System.err.println("GeneticAlgorithm <<WARNING>>: the total fitness of the population is " + fitnessSum + ", while it should be >0 for a proper fitness function.\n" +
					"Using a uniform selection instead.");
			int chromID = (int)(rnd*chromosomes.size());
			for(int i = 0; it.hasNext(); i++){
				Chromosome next = it.next();
				if(i == chromID) return next;
			}
			//If we arrive at this line, I have encountered an inexplicable error.
			//The above for-loop should terminate guaranteedly, since we selected a termination ID
			// which is non-negative and less than the number of items in the iterator.
			throw new Error("(GeneticAlgorithm) Cannot arrive at this line???");
		}else{
			//Roulette Wheel selection
			rnd *= fitnessSum; //random number \in{0,fitnessSum}
			double accumulator = 0;
			while(it.hasNext()){
				Chromosome next = it.next();
				accumulator += next.fitness;
//				System.out.println("accumulator/fitnessSum = " + accumulator + "/" + fitnessSum);
				if(rnd <= accumulator){
					//This is the winner:
					return next;
				}//else try the next chromosome
			}
			//Not even a rounding error can get us here, can it???
			throw new Error("(GeneticAlgorithm) Cannot arrive at this line???");
		}
				
	}

	/**
	 * Computes the sum of the Chromosomes' fitness.
	 * It should be called at the very end of each iteration.
	 * 
	 * @author Kevin van As
	 */
	protected void updateTotalFitness(){
		fitnessSum = 0;
		for(Chromosome chrom : chromosomes){
			fitnessSum += chrom.fitness;
		}
	}

	/**
	 * True if a uniformly generated number is smaller than "probability"
	 * 
	 * @author Kevin van As
	 * @param probability
	 * @return true with a probability 'probability'
	 */
	protected boolean shouldWe(float probability){
		double rnd = Math.random();
		return rnd<probability;		
	}
	
	/**
	 * @author Kevin van As
	 * @return true if we should mutate now
	 */
	protected boolean shouldMutate(){
		return shouldWe(pMutate);
	}
	
	/**
	 * @author Kevin van As
	 * @return true if we should crossover now
	 */
	protected boolean shouldCrossover(){
		return shouldWe(pCrossover);
	}
	
	/**
	 * Adds a chromosome to the 'chromosomes' list, including a safety-check.
	 * 
	 * @author Kevin van As
	 * @param chrom
	 */
	protected void addChromosome(Chromosome chrom){
		if(chrom.data.length != getChromLength())
			throw new IllegalArgumentException("GeneticAlgorithm <<ERROR>>: Illegal chromosome. It must have the pre-specified length: " + getChromLength() + ".");
		chromosomes.add(chrom);
	}
	/**
	 * Adds a chromosome to the 'chromosomesNew' list, including a safety-check.
	 * 
	 * @author Kevin van As
	 * @param chrom
	 */
	protected void addNewChromosome(Chromosome chrom){
		if(chrom.data.length != getChromLength())
			throw new IllegalArgumentException("GeneticAlgorithm <<ERROR>>: Illegal chromosome. It must have the pre-specified length: " + getChromLength() + ".");
		chromosomesNew.add(chrom);
	}
	
	/**
	 * Change the (in the constructor specified) fitness goal of the algorithm.
	 * 
	 * The algorithm successfully finishes once the fitness goal has been reached for its best chromosome.
	 * 
	 * @author Kevin van As
	 * @param fitnessGoal
	 */
	public void changeFitnessGoal(double fitnessGoal){
		this.fitnessGoal = fitnessGoal;
	}
	
/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ GETTERS $$$$$$$$$$$$$$$$$$$$$$$$$$$*/

	/**
	 * Returns the fitness of the fittest chromosome.
	 * 
	 * @author Kevin van As
	 * @return bestFitness
	 */
	public double getBestFitness(){
		return bestFitness;
	}
	
	public void updateBestFitness(){
		bestFitness = getBestChromosome(chromosomes).getFitness();
	}
	
	/**
	 * Returns a (copy of) the fittest chromosome of the current generation.
	 * Since it is a copy, altering it will not affect the algorithm.
	 * 
	 * @author Kevin van As
	 * @return the best chromosome of the current generation
	 */
	public Chromosome getBestChromosome(){
		return this.getBestChromosome(chromosomes).duplicate();
	}
	
	/**
	 * Returns the best chromosome in the given collection.
	 * If two chromosomes have the same fitness, first-come-first-served is returned.
	 * 
	 * Be careful with modifying it, as you may ruin our fittest fella!
	 * 
	 * 
	 * @author Kevin van As
	 * @param coll
	 * @return the best chromosome (no duplicate!)
	 */
	protected Chromosome getBestChromosome(Collection<Chromosome> coll){
		Iterator<Chromosome> it = coll.iterator();
		if(!it.hasNext()){
			return null;
		}
		
		//First chromosome:
		Chromosome best = it.next();
		//Compare the others to the current-best:
		while(it.hasNext()){
			Chromosome next = it.next();
			if(next.fitness > best.fitness){
				best = next;
			}
		}
		
		return best;
	}
	
	/**
	 * Provides an iterator over the {@code chromosomes} set, which contains all current chromosomes.
	 * (I.e., between iterations.)
	 * 
	 * @return Iterator<Chromosome>
	 */
	public Iterator<Chromosome> getChromosomesIterator(){
		return chromosomes.iterator();
	}
	
/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ OVERRIDEN METHODS $$$$$$$$$$$$$$$$$$$$$$$$$$$*/

	public int getChromLength() {
		return chromLength;
	}

	@Override
	protected boolean shouldTerminate() {
		return false;
	}

	@Override
	protected boolean isConverged(){
		return bestFitness >= fitnessGoal;
	}
	
	@Override
	protected void onAlgorithmBegin() {
		initPopulation();
		onLoopBegin();
	}

	@Override
	protected void onAlgorithmFinish() {
		System.out.println("GeneticAlgorithm has finished using " + this.getCurrentIterNumber() + "/" + this.maxNumIter + " iterations " +
				"and achieving an accuracy of " + bestFitness + " with a goal of " + fitnessGoal + ".");		
	}

	@Override
	protected void onAlgorithmTerminate() {
		System.out.println("GeneticAlgorithm has terminated using " + this.getCurrentIterNumber() + "/" + this.maxNumIter + " iterations " +
			"and achieving an accuracy of " + bestFitness + " with a goal of " + fitnessGoal + ".");		
	}
	
	/**
	 * Single iteration of the Genetic Algorithm:
	 * 
	 * 1) Elitism
	 * 2) Select pairs of chromosomes using the rouletteWheel algorithm
	 * 3) Apply crossover with probability 'pCrossover' to the pair of chromosomes.
	 * 4) Apply mutation with probability 'pMutate' to the two chromosomes individually.
	 * 
	 * @author Kevin van As
	 * @return
	 */
	protected void doLoopIteration(){
		Chromosome chromA, chromB, chromC;
		
		elitism();
		while(chromosomesNew.size() < populationSize){
			//Natural selection (fitness has been calculated in the previous iteration)
			chromA = rouletteWheel();
			//New chromosome for the child population: (we cannot use overwriting of the parents, since we still require the parents for natural selection).
			chromC = chromA.duplicate();
			chromA = null; //safety: we may not use it
	
			if(shouldCrossover()){
				chromB = chromA;
				while(chromB==chromA) chromB = rouletteWheel(); //Obtain another parent (not chromA)
				crossover(chromC, chromB);
			}
			if(shouldMutate()) mutate(chromC);
			//Update fitness & add to the new list:
			chromC.setFitness(fitness(chromC));
			this.addNewChromosome(chromC);
		}
		onIterEnd();
		
		//From this line onward, we now have a new population!
		
		//Prepare for the next iteration
		chromosomes.clear();
		chromosomes.addAll(chromosomesNew);
		chromosomesNew.clear();
		
		updateTotalFitness(); //uses the 'chromosomes' set and thus must be called last
		updateBestFitness();
		
		onNewGenerationDone(this.getCurrentIterNumber(), bestFitness);
	}

	@Override
	public void reInit() {}
	
/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ ABSTRACT METHODS $$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	
	/**
	 * @author Kevin van As
	 * @param chromosome
	 * @return The fitness of "chromosome". A higher number is a 'better' chromosome.
	 */
	protected abstract double fitness(Chromosome chrom);
	/**
	 * You should mutate the given chromosome
	 * 
	 * @author Kevin van As
	 */
	protected abstract void mutate(Chromosome chrom);
	/**
	 * You should apply crossover between chromo1 and chromo2 and write the result to chromo1.
	 * 
	 * DO NOT change chromo2. He is still in-use to produce off-spring!
	 * 
	 * chromo1 is a duplicate of the original chromosome, hence you can write to it.
	 * 
	 * @author Kevin van As
	 */
	protected abstract void crossover(Chromosome chromo1, Chromosome chromo2);
		
	/**
	 * Method is called at the end of every iteration. Use it to gather statistics or ignore it.
	 * The new generation has already overwritten the old generation,
	 * and the `totalFitness' has been computed.
	 *  
	 * @author Kevin van As
	 * @param iterNumber 
	 * @param bestFitness 
	 */
	protected abstract void onNewGenerationDone(int iterNumber, double bestFitness);
		
	/**
	 * Method is called right after every iteration.
	 * I.e., when the new generation is still new (and has not yet replaced the old generation),
	 * and before the totalFitness is computed.
	 * 
	 * @author Kevin van As
	 */
	protected abstract void onIterEnd();
	
	/**
	 * Method is called right before the first iteration.
	 * 
	 * @author Kevin van As
	 */
	protected abstract void onLoopBegin();
		
}
