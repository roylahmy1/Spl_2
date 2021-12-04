package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
//
//    ○ name: string - name of the model.
//            ○ data: Data - the data the model should train on.
//○ student: Student - The student which created the model.
//            ○ status: enum - can be “PreTrained”, “Training”, “Trained”, “Tested”.
//            ○ results: enum - can be “None” (for

    //
    enum Status {PreTrained, Training, Trained, Tested}
    enum Results {None, Good, Bad}
    //
    private String stringName;
    private Data data;
    private Student student;
    private Results results;
    private Status status;
    private int Trained;

    public Model(String stringName, Data data, Student student){
        status = Status.PreTrained;
        results = Results.None;
        Trained = 0;
    }

    public synchronized Status getStatus() {
        return status;
    }

    public synchronized void setStatus(Status status) {
        this.status = status;
    }

    public synchronized Results getResults() {
        return results;
    }

    public synchronized void setResults(Results results) {
        this.results = results;
    }

    public Data getData() {
        return data;
    }

    public synchronized void increaseTrained() {
        Trained++;
    }
}
