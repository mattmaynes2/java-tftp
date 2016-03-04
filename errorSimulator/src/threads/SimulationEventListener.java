package threads;

public interface SimulationEventListener {

	/**
	 * Callback for a simulation completion
	 */
	public void simulationComplete();
	
	/**
	 * Callback for the start of a simulation
	 */
	public void simulationStarted();
}
