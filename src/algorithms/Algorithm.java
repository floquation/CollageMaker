package algorithms;

/**
 * Base-class for any algorithm which may be executed by a single method call: {@code run()}.
 * This class does account for execution time.
 * Use the implementation-specific methods to define termination criteria etc.
 * 
 * @author Kevin van As
 * 
 */
public abstract class Algorithm {
	
	/**
	 * Parameter holding the current Execution Time of the algorithm.
	 * It is continuously updated at the end of every iteration.
	 * Changing it from the outside doesn't do anything.
	 * On termination, the value will persist the final execution time (unless changed from the outside, or calling "run()" again).
	 */
	public float executionTime = 0;
	private long timeIni = -1;

/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ CONSTRUCTORS $$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	
	public Algorithm(){}

/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ IMPLEMENTED METHODS $$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	
	protected void updateExecutionTime(){
		if(timeIni == -1)
			executionTime = 0;
		executionTime = (System.nanoTime()-timeIni)/1000000000f;
	}
	
/*$$$$$$$$$$$$$$$$$$$$$$$$$$$ ABSTRACT METHODS $$$$$$$$$$$$$$$$$$$$$$$$$$$*/
	
	/**
	 * Initializes the algorithm, should be called MANUALLY in the constructor.
	 * 
	 * Upon recalling, it should re-initialize the algorithm, thus refreshing the required data.
	 * 
	 * @author Kevin van As
	 */
	public abstract void reInit();
	
	/**
	 * Starts the execution of the algorithm.
	 * 
	 * Checklist:
	 *  - Have you set the termination criteria? Otherwisely you have an infinite loop.
	 * 
	 * If not, default values are used.
	 * 
	 * 
	 * <synchronized> ensures that the algorithm cannot be executed twice simultaneously.
	 * TODO: Is this the correct way to do it?
	 * 
	 * @author Kevin van As
	 */
	public synchronized void run(){
		timeIni = System.nanoTime();
		onAlgorithmBegin();
		
		boolean converged = startAlgorithm();

		if(converged){
			onAlgorithmFinish();
		}else{
			onAlgorithmTerminate();
		}
		timeIni = -1;
	}
	
	/**
	 * The first (and only) method called by run(): this method should contain the algorithm.
	 * 
	 * @return true if converged, false if terminated early
	 * @author Kevin van As
	 */
	protected abstract boolean startAlgorithm();
	/**
	 * Method called before the loop begins.
	 * Eventual initialisations should happen here.
	 * 
	 * @author Kevin van As
	 */
	protected abstract void onAlgorithmBegin();
	/**
	 * Method called when the loop successfully ends
	 * 
	 * @author Kevin van As
	 */
	protected abstract void onAlgorithmFinish();
	/**
	 * Method called when the algorithm terminates early,
	 * e.g. due to an error, user interference or reaching a maximum number of iterations.
	 * 
	 * This method is not called when "onLoopFinish()" is called during this iteration as well:
	 * "onLoopFinish()" has the preference in case of a simultaneous satisfaction of the
	 * termination condition and the convergence condition.
	 * 
	 * @author Kevin van As
	 */
	protected abstract void onAlgorithmTerminate();
	
}
