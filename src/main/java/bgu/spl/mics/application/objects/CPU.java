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
    private int chunkIndex;
    private Cluster theCluster;

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
        // process DB and update cluster accordingly
    }
    /**
     get a DB chunk of unprocessed data from the cluster
     * @PRE:
     * none
     * @POST:
     * none
     */
    public void updateChunk(){

    }
    /**
     check if chunk in empty, or has information left to process
     */
    public boolean checkChunk(){
        if (unprocessedData == null)
            return false;
        return chunkIndex <= unprocessedData.getEndIndex() && chunkIndex >= unprocessedData.getStartIndex();
    }
}
