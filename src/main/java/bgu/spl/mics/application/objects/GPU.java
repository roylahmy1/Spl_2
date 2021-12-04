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
    private Model model;
    private DatabatchQueue VRAM;
    private final int VRAMSize = 16;

    /**
     * @INV:
     * model != null
     */

    public GPU(Cluster theCluster, Type type){
        this.type = type;
        this.theCluster = theCluster;
        // define the gpu
    }

    /**
     * @PRE:
     * model == null
     * @POST:
     *
     */
    public synchronized void initProcess(Model model){
        this.model = model;

        if (model.getStatus() == Model.Status.PreTrained){
            Cluster.getInstance().storeUnprocessedData(model.getData(), this);
            model.setStatus(Model.Status.Training);
        }
        // get Data, and start processing it
        // pass down to cluster
    }
    // process one tick (to train the model)
    public synchronized void processTick() {
        //

        if (model.getStatus() == Model.Status.Training){
            //
        }
        if (model.getStatus() == Model.Status.Trained){
            // return random odds of success
        }
    }
    // check if VRAM need's refill
    public boolean checkVRAM(){
        return VRAM.isEmpty();
    }
    // if VRAM is empty (or close to it) then refill it
    public void fillVRAM(){
        VRAM = Cluster.getInstance().getProcessedData(this, VRAMSize);
    }

    public boolean isCompleted

    public synchronized Model.Status getStatus(){
        return model.getStatus();
    }
    public synchronized void clean(){
        this.model = null;
    }
    public Model getModel() {
        return model;
    }
}
