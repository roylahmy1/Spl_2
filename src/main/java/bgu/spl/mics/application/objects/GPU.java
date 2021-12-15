package bgu.spl.mics.application.objects;

import java.util.Random;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}
    private Type type;
    private Cluster theCluster;
    private Model model;
    private DatabatchQueue VRAM;
    private int VRAMSize;
    //
    private int ticksPerDataBatch;
    private DataBatch currentDataBatch;
    private int currentProgress = 0;

    /**
     * @INV:
     * theCluster != null
     */

    public GPU(Cluster theCluster, Type type){
        this.type = type;
        this.theCluster = theCluster;
        theCluster.insertGpu(this);

        if (type == Type.GTX1080) {
            VRAMSize = 8;
            ticksPerDataBatch = 4;
        }
        if (type == Type.RTX2080) {
            VRAMSize = 16;
            ticksPerDataBatch = 2;
        }
        if (type == Type.RTX3090) {
            VRAMSize = 32;
            ticksPerDataBatch = 1;
        }
    }

    /**
     * @PRE:
     * getModel().getStatus() == Model.Status.PreTrained
     * @POST:
     * getModel().getStatus() == Model.Status.Training
     * getModel() != null
     */
    public synchronized void TrainModel(Model model){
        this.model = model;

        // get Data, and start processing it
        // pass down to cluster
        if (model.getStatus() == Model.Status.PreTrained){
            Cluster.getInstance().storeUnprocessedData(model.getData(), this);
            model.setStatus(Model.Status.Training);
        }
    }
    /**
     * process one tick (to train the model)
     * @PRE:
     * model != null
     * checkVRAM() == true
     * @POST:
     * none
     */
    public synchronized void processTick() {
        //
        if (currentDataBatch != null)
            currentProgress++;

        //
        if (currentProgress >= ticksPerDataBatch){
            if (currentDataBatch != null) {
                model.increaseTrained();
            }
            currentDataBatch = VRAM.pop();
            currentProgress = 0;
        }

    }
    // check if VRAM need's refill
    public synchronized boolean checkVRAM(){
        return VRAM.isEmpty();
    }
    // if VRAM is empty (or close to it) then refill it
    public synchronized void fillVRAM(){
        VRAM = Cluster.getInstance().getProcessedData(this, VRAMSize);
    }

    public synchronized boolean isCompleted() {
        return model.isCompleted();
    }
    /**
     * @PRE:
     * getModel().getStatus() == Model.Status.Trained
     * @POST:
     * getModel() != null
     * getModel().getStatus() == Model.Status.Tested
     * getModel().getResults() != Model.Results.None
     */
    public synchronized void testModel(Model model) {
        //
        Random rnd = new Random();
        float randomizer = rnd.nextFloat(); // get random float in [0 , 1)
        if (randomizer <= 0.6 && model.getStudent().getStatus() == Student.Degree.MSc){
            model.setResults(Model.Results.Good);
        }
        else if (randomizer <= 0.8 && model.getStudent().getStatus() == Student.Degree.PhD){
            model.setResults(Model.Results.Good);
        }
        else {
            model.setResults(Model.Results.Bad);
        }
        model.setStatus(Model.Status.Tested);
    }

    // get current model status
    public synchronized Model.Status getStatus(){
        return model.getStatus();
    }

    /**
     * @PRE:
     * none
     * @POST:
     * getModel() == null
     */
    public synchronized void clean(){
        this.model = null;
    }
    public Model getModel() {
        return model;
    }
}
