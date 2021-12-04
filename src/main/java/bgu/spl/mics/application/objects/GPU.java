package bgu.spl.mics.application.objects;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}
    private Type type;
    private Cluster theCluster;
    private Data unprocessedData;
    private DataBatch[] VRAM;
    private boolean isProcessing;

    public GPU(Cluster theCluster, Type type){
        this.type = type;
        this.theCluster = theCluster;
        isProcessing = false;
        // define the gpu
    }
    public synchronized void initProcess(Data data){
        this.unprocessedData = data;
        isProcessing = true;
        // get Data, and start processing it
        // pass down to cluster
    }
    // process one tick (to train the model)
    public synchronized void processTick() {
        //
    }
    // check if VRAM need's refill
    private boolean checkVRAM(){

    }
    // if VRAM is empty (or close to it) then refill it
    private boolean fillVRAM(){

    }


    public synchronized boolean isComplete(){

    }
    // after the
    public synchronized Data getCompleteProcess(){

    }
    public synchronized void clean(){

    }

}
