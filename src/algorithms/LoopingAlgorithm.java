package algorithms;

/**
 * Base-class for an algorithm which may be written as a loop:
 * 
 * onAlgorithmBegin()
 * while(running){
 * 	doLoopIteration()
 * 	"checkTerminationCriterion"
 * }
 * onAlgorithmFinish()/onAlgorithmTerminate() (depending on how it was terminated)
 * 
 * Built-in termination criteria are the maximum number of iterations and the maximum execution time.
 * 
 * @author Kevin van As
 * 
 */
public abstract class LoopingAlgorithm extends Algorithm {

	private int iterNumber;
	protected int maxNumIter = -1;
	protected float maxExecTime = -1;

/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ CONSTRUCTORS $$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	
	public LoopingAlgorithm(){
		super();
	}

/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ IMPLEMENTED METHODS $$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	
	@Override
	protected boolean startAlgorithm(){		
		boolean converged = false;
		boolean terminate = false;
		iterNumber=0;
		while(!converged && !terminate){
			iterNumber++;
			doLoopIteration();
			
			//Check for termination:
			updateExecutionTime();
			converged = isConverged();
			terminate = shouldTerminate()
					|| (maxNumIter != -1 && iterNumber >= maxNumIter ) 
					|| (maxExecTime != -1 && executionTime >= maxExecTime);
		}
		return converged;
	}
	
	/**
	 * Returns an integer equal to the currently executing iteration number.
	 * I.e., it starts at 1, when the first iteration has not yet finished.
	 * Once the algorithm terminates, the IterNumber will linger at its final value, until the algorithm is rerun.
	 * 
	 * @return the current iteration number
	 * @author Kevin van As
	 */
	public int getCurrentIterNumber(){
		return iterNumber;
	}
	
	/**
	 * Sets the maximum execution time of the algorithm.
	 * Once the algorithm terminates an iteration, it will "terminate" if its
	 * execution time becomes longer than the specified 'maxExecTime'.
	 * 
	 * A value of "-1" means "do not check for maximum execution time".
	 * 
	 * The onAlgorithmTerminate() method will be called, not the onAlgorithmFinish().
	 * 
	 * @author Kevin van As
	 * @param maxExecTime
	 */
	public void setMaxExecTime(float maxExecTime){
		this.maxExecTime = maxExecTime;
	}
	
	/**
	 * Sets the maximum number of iterations of the algorithm.
	 * Once the algorithm terminates an iteration, it will "terminate" if
	 * it has reached its maximum number of iterations.
	 * 
	 * A value of "-1" means "do not check for maximum number of iterations".
	 * 
	 * The onAlgorithmTerminate() method will be called, not the onAlgorithmFinish().
	 * 
	 * @author Kevin van As
	 * @param maxNumIter
	 */
	public void setMaxNumIter(int maxNumIter){
		this.maxNumIter = maxNumIter;
	}
	
	public int getMaxNumIter(){
		return this.maxNumIter;
	}
	
	public float getMaxExecTime(){
		return this.maxExecTime;
	}
	
	@Override
	public void reInit(){}
	
/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ ABSTRACT METHODS $$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	
	/**
	 * This is the workhorse of the algorithm: the actual loop iterations.
	 * This method is called once, at the start of each iteration.
	 * 
	 * @author Kevin van As
	 * @return true if success, false if terminated early by an error or by the user
	 */
	protected abstract void doLoopIteration();

	/**
	 * Method to check whether the algorithm has converged;
	 * 
	 * The algorithm will finish using the "onLoopFinish()"-method.
	 * 
	 * @author Kevin van As
	 * @return true if the algorithm should finish
	 */
	protected abstract boolean isConverged();
	/**
	 * Method to check whether the algorithm should terminate (while it is not converged).
	 * I.e., when the algorithm has encountered an error or when the user terminates it early.
	 * 
	 * The algorithm will then finish using the "onLoopTerminate()"-method.
	 * 
	 * @author Kevin van As
	 * @return
	 */
	protected abstract boolean shouldTerminate();
	
}
