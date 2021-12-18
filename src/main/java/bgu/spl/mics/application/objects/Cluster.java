package bgu.spl.mics.application.objects;


import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

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

	private LinkedList<GPU> gpuList;
	private LinkedList<CPU> cpuList;
	private int ModelsProceessed = 0;
	private int ModelsStarted = 0;
	private LinkedList<Model> startedTrainedModels;
	// stats
	private AtomicInteger batchesProcessed = new AtomicInteger(0);
	private AtomicInteger cpuTime = new AtomicInteger(0);
	private AtomicInteger gpuTime = new AtomicInteger(0);
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
		//
		startedTrainedModels = new LinkedList<>();
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
	public static void resetSingleton() {
		SingletonHolder.instance = new Cluster();
	}


	// GPU stores unprocessed data
	public void storeUnprocessedData(Data data) {
		unprocessedDataSets.add(data);
		synchronized (unprocessedDataSets){
			ModelsStarted++;
			startedTrainedModels.add(data.getHolderGpu().getModel());
		}
	}
	// CPU get data in chunks (according to need) of DB
	public Chunk getUnprocessedData() {
		// decide how much data a CPU should get
		Data data = unprocessedDataSets.poll();
		if (data == null)
			return null;
		Chunk chunk = data.getChunk(1);
		// re add data to the end of the queue
		if (!data.isCompleted())
			unprocessedDataSets.add(data);
		else {
			synchronized (unprocessedDataSets) {
				ModelsProceessed++;
			}
		}
		return chunk;
	}
	// CPU stores DB set
	public void storeProcessedData(DataBatch db) {
		//
		processedDataSets.get(db.getContainer().getHolderGpu()).add(db);
		//
		int currentBatchesProcessed;
		do {
			currentBatchesProcessed = batchesProcessed.get();
		}
		while(!batchesProcessed.compareAndSet(currentBatchesProcessed, currentBatchesProcessed + 1));
	}


	// GPU get DB set
	public DatabatchQueue getProcessedData(GPU gpu, int count) {
		//
		//System.out.println("fill VRAM with " + count);
		DatabatchQueue gpuQueue = processedDataSets.get(gpu);
		DatabatchQueue resultQueue = new DatabatchQueue();

		DataBatch db = null;
		// since only one gpu will remove element from queue, then no concurrent memory access
		while (count > 0 && !gpuQueue.isEmpty()){
			// pull the next element, if null will check at next loop
			db = gpuQueue.pop();
			count--;
			resultQueue.add(db);
		}
		//System.out.println("finished filling VRAM with " + count);
		//
		return resultQueue;
	}


	///
	public void increaseCpuTime() {
		int currentCpuTime;
		do {
			currentCpuTime = cpuTime.get();
		}
		while(!cpuTime.compareAndSet(currentCpuTime, currentCpuTime + 1));

	}
	public void increaseGpuTime() {
		int currentGpuTime;
		do {
			currentGpuTime = gpuTime.get();
		}
		while(!gpuTime.compareAndSet(currentGpuTime, currentGpuTime + 1));

	}
	public int getBatchesProcessed(){
		return batchesProcessed.get();
	}
	public int getCpuTime(){
		return cpuTime.get();
	}
	public int getGpuTime(){
		return gpuTime.get();
	}



}
