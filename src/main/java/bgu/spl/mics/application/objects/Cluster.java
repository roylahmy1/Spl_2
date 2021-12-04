package bgu.spl.mics.application.objects;


import java.util.Map;
import java.util.Queue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster<object> {

	/**
     * Retrieves the single instance of this class.
     * @PRE: none
     * @POST: none
     */

	/**
	 --- SINGLETON ---
	 array of GPU'S
	 array of CPU's

	 // every request from the cpu for data will loop in round-robin mannar
	 unprocessed list of data (belongs to a GPU) - unlimited
	 int (Atomic - maybe?)
	 CPU - will have cache

	 processed queue of data (belongs to a GPU) -- limited

	 task queue - unprocessed queue of data  (for one CPU) -- unlimited

	 //

	 */

	GPU[] gpuArray;
	CPU[] cpuArray;

	Map<Integer, Data> unprocessedDataSets;
	int unprocessedIndex;
	object unprocessedIndexLock;
	Map<Integer, Queue<Data>> processedDataSets;

	// GPU stores unprocessed data
	public synchronized void storeUnprocessedData(Data data, Integer gpuIndex) {
		unprocessedDataSets.put(gpuIndex, data);
	}
	// CPU get data in chunks (according to need) of DB
	public DataBatch[] getUnprocessedData() {
		// decide how much data a CPU should get
		// loop unprocessedDataSets
		DataBatch[] chunk = unprocessedDataSets.get(unprocessedIndex).getChunk();
		synchronized (unprocessedIndexLock){
			unprocessedIndex++;
		}
		return chunk;
	}
	// CPU stores DB set
	public void storeProcessedData() {
		//

	}
	// GPU get DB set
	public void getProcessedData() {
		//

	}




	public static Cluster getInstance() {
		//TODO: Implement this
		return null;
	}

}
