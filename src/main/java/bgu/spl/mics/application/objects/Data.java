package bgu.spl.mics.application.objects;

import java.util.Vector;

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
    //
    private int processed;
    private Object processedLock = new Object();
    private int inProcessing;
    private Object inProcessingLock = new Object();
    //
    private int size;
    private DataBatch[] dataBatches;
    public Data(int size, Type type){
        processed = 0;
        dataBatches = new DataBatch[size];
        for (int i = 0; i < size; i++) {
            dataBatches[i] = new DataBatch(this, i * 1000);
        }
    }
    public void batchCompleted(){
        synchronized(processedLock) {
            processed++;
        }
    }
    public synchronized Chunk getChunk(int chunkSize){
        synchronized(inProcessingLock) {
            int chunkEnd = inProcessing + chunkSize;
            if (chunkEnd > size) { // no more batches to get after this
                chunkEnd = size - 1;
            }
            Chunk chunk = new Chunk(this, inProcessing, chunkEnd + 1);
            inProcessing = chunkEnd + 1;
            return chunk;
        }
    }

    public DataBatch[] getDataBatches() {
        return dataBatches;
    }

    public int getSize() {
        return size;
    }

    // when processed++ will not cause damage if isCompleted
    public boolean isCompleted(){
        return processed >= size;
    }
}

class Chunk {
    private Data container;
    private final int startIndex;
    private final int endIndex;
    public Chunk(Data container, int startIndex, int endIndex){
        this.container = container;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
}


class DatabatchQueue {

    // a vector, used to implement the queue
    private Vector<DataBatch> vec_;
    public DatabatchQueue() { }

    public synchronized int size(){
        return vec_.size();
    }

    public synchronized void add(DataBatch e){
        vec_.add(e);
    }

    public synchronized DataBatch pop(){
        if (!isEmpty()) {
            DataBatch db = vec_.get(0);
            vec_.remove(0);
            return db;
        }
        return null;
    }
    public synchronized boolean isEmpty(){
        return vec_.size() == 0;
    }
}