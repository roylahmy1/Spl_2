package bgu.spl.mics.application.objects;


/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	/**
     * Retrieves the single instance of this class.
     * @PRE: none
     * @POST: none
     */

	/**
	 --- SINGLETON ---
	 array of GPU'S
	 array of CPU's

	 unprocessed queue of data (belongs to a GPU) - unlimited
	 processed queue of data (belongs to a GPU) -- limited

	 unprocessed queue of data  (for one CPU) -- unlimited

	 //

	 */


	public static Cluster getInstance() {
		//TODO: Implement this
		return null;
	}

}
