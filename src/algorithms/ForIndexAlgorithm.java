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
public abstract class ForIndexAlgorithm extends Algorithm {

	private float progress = 0f;
	protected float maxExecTime = -1;
	
	private int iStart;
	private int iEnd;
	private int di;

/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ CONSTRUCTORS $$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	
	/**
	 * Algorithm which can be described in the form: 
	 * "for(int i = iStart; i<iEnd; i+=di)"
	 * 
	 * There are (iEnd-iStart) iterations.
	 * 
	 * @param iStart
	 * @param iEnd
	 * @param di
	 */
	public ForIndexAlgorithm(int iStart, int iEnd, int di){
		super();
		this.iStart = iStart;
		this.iEnd = iEnd;
		this.di = di;
	}

/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ IMPLEMENTED METHODS $$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	
	@Override
	protected boolean startAlgorithm(){
		boolean terminate = false;
		progress=0f;
		
		float iRange = (float)(iEnd-iStart);
		if(iRange<=0) return false;
		
		for(int i = iStart; i<iEnd; i+=di){
			doLoopIteration(i);
			
			progress = (i+di-iStart)/iRange;
			
			//Check for termination:
			updateExecutionTime();
			terminate = shouldTerminate()
					|| (maxExecTime != -1 && executionTime >= maxExecTime);
			if(terminate) break;
		}
		
		return !terminate;
	}
	
	/**
	 * Returns a float equal to the current progress (as a fraction between 0 and 1).
	 * 
	 * @return progress (float \in{0,1})
	 * @author Kevin van As
	 */
	public float getProgress(){
		return progress;
	}
	
	/**
	 * Returns a formatted String containing the progress of the form:
	 * 
	 * 'xx,xx%'
	 * 
	 * e.g.: " 2,20%" or "93,63%"
	 * 
	 * @author Kevin van As
	 * @return progress in % as a String
	 */
	public String getFormattedProgress(){
		return String.format("%5.2f", progress*100) + "%";
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
	 * 
	 * N.B.:
	 * It typically does not make any sense to use a maximum execution time for a "ForIndex" Algorithm! Use wisely!
	 * 
	 * 
	 * @author Kevin van As
	 * @param maxExecTime
	 */
	public void setMaxExecTime(float maxExecTime){
		this.maxExecTime = maxExecTime;
	}
	
	@Override
	public void reInit(){}
	
/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ ABSTRACT METHODS $$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	
	/**
	 * This is the workhorse of the algorithm: the actual loop iterations.
	 * This method is called once, at the start of each iteration.
	 * 
	 * @author Kevin van As
	 * @param index :: the current index of the loop.
	 * @return true if success, false if terminated early by an error or by the user
	 */
	protected abstract void doLoopIteration(int index);

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
