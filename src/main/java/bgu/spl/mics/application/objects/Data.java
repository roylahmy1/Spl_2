package bgu.spl.mics.application.objects;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

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
    // the number of processed Db's
    private AtomicInteger processed;
    // the number of Db's acquired to chunks
    private AtomicInteger inProcessing;
    private GPU holderGpu;
    //
    private int size;
    private int dbSize;
    public Data(int size, Type type){
        processed = new AtomicInteger(0);
        this.size = size;
        // number of databatches in the data
        this.dbSize = size / 1000;
        this.type = type;
        this.inProcessing = new AtomicInteger(0);
    }
    public void setHolderGpu(GPU holderGpu) {
        this.holderGpu = holderGpu;
    }
    public GPU getHolderGpu() {
        return holderGpu;
    }
    //
    public void batchCompleted(){
        int currentProcessed;
        do{
            currentProcessed = processed.get();
        }
        while (!processed.compareAndSet(currentProcessed, currentProcessed + 1));
    }
    // get chunk concurrently using atomic inte
    public Chunk getChunk(int chunkSize){
        //
        int chunkStart;
        do {
            chunkStart = inProcessing.get();
        } while (!inProcessing.compareAndSet(chunkStart, chunkStart + chunkSize) && chunkStart < dbSize);

        //
        int chunkEnd = chunkStart + chunkSize - 1;
        if (chunkStart >= dbSize)
            return null;
        if (chunkEnd >= dbSize) { // no more batches to get after this
            chunkEnd = dbSize - 1;
        }
        Chunk chunk = new Chunk(this, chunkStart, chunkEnd);
        return chunk;
    }

    //
    public Type getType() {
        return type;
    }
    public int getSize() {
        return size;
    }

    // when processed++ will not cause damage if isCompleted
    public boolean isCompleted(){
        return processed.get() >= dbSize - 1;
    }
}

class Chunk {
    // the Data object it relates to
    private Data container;
    // the indexes of databatches "inside" the Data
    private final int startIndex;
    private final int endIndex;
    // current working index, inside the CPU
    private int currentIndex;
    // array of DB's
    private DataBatch[] dataBatches;
    public Chunk(Data container, int startIndex, int endIndex){
        //
        this.container = container;
        //
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        //
        this.currentIndex = 0;
        //
        this.dataBatches = new DataBatch[endIndex - startIndex + 1];
        for (int i = startIndex; i <= endIndex; i++) {
            dataBatches[i - startIndex] = new DataBatch(container, i * 1000);
        }
    }
    public Data getContainer() {
        return container;
    }
    public int getSize(){
            return dataBatches.length;
    }
    public DataBatch getNext() {
        if (currentIndex > endIndex)
            return null;
        DataBatch current = dataBatches[currentIndex];
        currentIndex = currentIndex + 1;
        return current;
    }
    public boolean isFinished() {
        return this.currentIndex >= dataBatches.length;
    }
}


class DatabatchQueue {

    // a vector, used to implement the queue
    private Vector<DataBatch> vec_;
    public DatabatchQueue() {
        vec_ = new Vector<DataBatch>();
    }

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