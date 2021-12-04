package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;
    private DataBatch[] dataBatches;
    public Data(int size, Type type){
        processed = 0;
        dataBatches = new DataBatch[size];
        for (int i = 0; i < size; i++) {
            dataBatches[i] = new DataBatch(this, i * 1000);
        }
    }
    public synchronized void batchCompleted(){
        processed++;
    }
    public synchronized DataBatch[] getChunk(){
        return ;
    }
    // when processed++ will not cause damage if isCompleted
    public boolean isCompleted(){
        return processed >= size;
    }
}

