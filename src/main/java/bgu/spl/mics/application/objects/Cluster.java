package bgu.spl.mics.application.objects;


import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;

import java.util.*;

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
	private Object unprocessedIndexLock = new Object();
	private Map<GPU, Data> unprocessedDataSets;
	private Map<GPU, DatabatchQueue> processedDataSets;


	private Cluster() {
		gpuList = new LinkedList<GPU>();
		cpuList = new LinkedList<CPU>();
		//
		Map<GPU, Data> unprocessedDataSets;
		Map<GPU, DatabatchQueue> processedDataSets;

	}
	public synchronized void insertGpu(GPU gpu){
		gpuList.add(gpu);
		unprocessedDataSets.put(gpu, null);
		processedDataSets.put(gpu, null);
	}
	public synchronized void insertCpu(CPU cpu){
		cpuList.add(cpu);
	}


	/**
	 * Retrieves the singleton instance
	 */
	public static Cluster getInstance() {
		return singletonInstance;
	}


	// GPU stores unprocessed data
	public void storeUnprocessedData(Data data, GPU gpu) {
		synchronized (unprocessedIndexLock) {
			unprocessedIndex = 0;
			unprocessedDataSets.put(gpu, data);
		}
	}
	// CPU get data in chunks (according to need) of DB
	public Chunk getUnprocessedData() {
		// decide how much data a CPU should get
		Chunk chunk = unprocessedDataSets.get(unprocessedIndex).getChunk(16);
		synchronized (unprocessedIndexLock){
			unprocessedIndex = unprocessedIndex % unprocessedDataSets.size();
		}
		return chunk;
	}
	// CPU stores DB set
	public void storeProcessedData(DataBatch db) {
		//
		processedDataSets.get(db.getContainer()).add(db);
	}
	// GPU get DB set
	public DatabatchQueue getProcessedData(GPU gpu, int count) {
		//
		DatabatchQueue gpuQueue = processedDataSets.get(gpu);
		DatabatchQueue resultQueue = new DatabatchQueue();

		//
		DataBatch db = gpuQueue.pop();
		while (count > 0 && db != null){
			resultQueue.add(db);
			count--;
			// pull the next element, if null will check at next loop
			db = gpuQueue.pop();
		}
		//
		return resultQueue;
	}

}
