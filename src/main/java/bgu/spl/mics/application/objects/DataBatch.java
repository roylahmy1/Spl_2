package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {

    private Data container;
    private boolean processed;
    private int startIndex;
    public DataBatch(Data container, int startIndex){
        this.startIndex = startIndex;
        this.container = container;
        processed = false;
    }
    public Data getContainer() {
        return container;
    }
    public boolean isProcessed() {
        return processed;
    }
    public void finished() {
        processed = true;
        container.batchCompleted();
    }
}
