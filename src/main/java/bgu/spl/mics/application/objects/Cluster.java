package bgu.spl.mics.application.objects;


import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	/**
	 * @INV:
     * Retrieves the single instance of this class.
     * @PRE: none
     * @POST: none
     */


	private static Cluster singletonInstance = new Cluster();

	LinkedList<GPU> gpuList;
	LinkedList<CPU> cpuList;
	//
	private int unprocessedIndex;
	private ConcurrentLinkedQueue<Data> unprocessedDataSets = new ConcurrentLinkedQueue<Data>();
	private HashMap<GPU, DatabatchQueue> processedDataSets = new HashMap<GPU, DatabatchQueue>();


	private Cluster() {
		gpuList = new LinkedList<GPU>();
		cpuList = new LinkedList<CPU>();
		//
		Map<GPU, Data> unprocessedDataSets;
		Map<GPU, DatabatchQueue> processedDataSets;

	}
	private static class  SingletonHolder {
		private static Cluster  instance = new Cluster() ;
	}
	public synchronized void insertGpu(GPU gpu){
		gpuList.add(gpu);
		processedDataSets.put(gpu, new DatabatchQueue());
	}
	public synchronized void insertCpu(CPU cpu){
		cpuList.add(cpu);
	}


	/**
	 * Retrieves the singleton instance
	 */
	public static Cluster getInstance() {
		return SingletonHolder.instance;
	}


	// GPU stores unprocessed data
	public void storeUnprocessedData(Data data) {
		unprocessedDataSets.add(data);
		// ??? maybe ???
		///
		// should NOT notify if data added
		///
	}
	// CPU get data in chunks (according to need) of DB
	public Chunk getUnprocessedData() {
		// decide how much data a CPU should get
		Data data = unprocessedDataSets.poll();
		if (data == null)
			return null;
		Chunk chunk = data.getChunk(16);
		// re add data to the end of the queue
		if (!data.isCompleted())
			unprocessedDataSets.add(data);
		return chunk;
//		synchronized (unprocessedIndexLock){
//			unprocessedIndex = (unprocessedIndex + 1) % unprocessedDataSets.size();
//			return chunk;
//		}
		// ??? maybe ???
		///
		// should NOT await if no data exists
		///
	}
	// CPU stores DB set
	public void storeProcessedData(DataBatch db) {
		//
		processedDataSets.get(db.getContainer().getHolderGpu()).add(db);

		// ??? maybe ???
		///
		// should NOT notify if data added
		///
	}
	// GPU get DB set
	public DatabatchQueue getProcessedData(GPU gpu, int count) {
		//
		DatabatchQueue gpuQueue = processedDataSets.get(gpu);
		DatabatchQueue resultQueue = new DatabatchQueue();

		// ??? maybe ???
		///
		// should NOT await if no data exists
		///

		DataBatch db = null;
		do {
			// pull the next element, if null will check at next loop
			db = gpuQueue.pop();
			resultQueue.add(db);
			count--;
		} while (count > 0 && db != null);
		//
		return resultQueue;
	}

}
