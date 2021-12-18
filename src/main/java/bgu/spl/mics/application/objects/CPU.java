package bgu.spl.mics.application.objects;

import java.util.Queue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    private int cores;
    private Chunk unprocessedData;
    private Cluster theCluster;

    //
    private int chunkIndex = 0;
    DataBatch current;
    int currentProgress = 0; // current number of ticks to current db
    int currentNeededTicks = 0; // the current batch amount of needed ticks

    /**
     * @INV:
     * theCluster != null
     */

    public CPU(int cores, Cluster theCluster) {
        this.cores = cores;
        this.theCluster = theCluster;
        theCluster.insertCpu(this);
    }
    /**
     process 1 tick in the CPU
     * @PRE:
     * checkChunk() == true
     * @POST:
     * none
     */
    public synchronized void processTick(){
        // get next if empty
        if (current == null) {
            if (unprocessedData != null)
                current = unprocessedData.getNext();
            currentProgress = 0;
        }

        // process DB and update cluster accordingly
        if (current != null) {
            currentProgress++;
            theCluster.increaseCpuTime();
        }

        // when finished update to the data batch
        // NOTICE - always should have another db to get, as when is last db get another chunk
        if (currentProgress >= currentNeededTicks){
            chunkIndex++;
            // if prev was not null, then set finished
            if (current != null) {
                current.finished();
                theCluster.storeProcessedData(current);
            }
            // remove current when finished
            current = null;
        }

    }
    private int calculateCurrentNeededTicks(Chunk chunk){
        // Images - (32 / number of cores) * 4 ticks.
        // Text - (32 / number of cores) * 2 ticks.
        // Tabular - (32 / number of cores) * 1 ticks.

        int coefficient;
        if (chunk.getContainer().getType() == Data.Type.Images){
            coefficient = 4;
        }
        else if (chunk.getContainer().getType() == Data.Type.Tabular){
            coefficient = 2;
        }
        else {
            coefficient = 1;
        }
        return coefficient * (32 / cores);
    }
    /**
     get a DB chunk of unprocessed data from the cluster
     * @PRE:
     * none
     * @POST:
     * none
     */
    public void updateChunk(){
        unprocessedData = theCluster.getUnprocessedData();
        if (unprocessedData != null)
            currentNeededTicks = calculateCurrentNeededTicks(unprocessedData);
        chunkIndex = 0;
    }
    /**
     check if chunk in empty, or has information left to process
     // update chunk if
     */
    public boolean isEmptyChunk(){
        if (current != null) // still processing, chunk might be empty but "fully"
            return false;
        if (unprocessedData == null)
            return true;
        return chunkIndex >= unprocessedData.getSize();
    }
}
