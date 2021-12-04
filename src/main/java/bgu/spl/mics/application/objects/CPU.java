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

    public CPU(int cores, Cluster theCluster) {
        this.cores = cores;
        this.theCluster = theCluster;
    }
    //
    public synchronized void processTick(){
        // process DB and update cluster accordingly
    }
    // get a DB set from the cluster
    public void updateChunk(){

    }
    // check if chunk in empty, or still running
    public boolean checkChunk(){
        return chunkIndex <= unprocessedData.getEndIndex() && chunkIndex >= unprocessedData.getStartIndex();
    }
}
