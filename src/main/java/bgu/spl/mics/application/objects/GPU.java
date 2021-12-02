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
    private DataBatch[] VRAM;
    private boolean isProcessing;
    public GPU(){
        isProcessing = false;
        // define the gpu
    }
    public synchronized void initProcess(Data data){
        isProcessing = true;
        // get Data, and start processing it
        // pass down to cluster
    }
    // process one tick (to train the model)
    public synchronized void processTick() {

    }
    // private - check and refill VRAM
    private boolean checkVRAM(){
        // check if VRAM has any more data left in VRAM
    }
    private boolean fillVRAM(){
        // if VRAM is empty (or close to it) then refill it
    }


    public synchronized boolean isComplete(){

    }
    // after the
    public synchronized Data getCompleteProcess(){

    }
    public synchronized void clean(){

    }

}
