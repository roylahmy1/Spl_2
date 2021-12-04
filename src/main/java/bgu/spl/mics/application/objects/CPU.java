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

    public CPU(int cores) {
        this.cores = cores;
    }
    //
    public synchronized void processTick(){

    }
    // get a DB set from the cluster
    private void getChunk(){

    }
}
