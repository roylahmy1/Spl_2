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
    public enum Status {PreTrained, Training, Trained, Tested}
    public enum Results {None, Good, Bad}
    // init data from json
    private String name;
    private String type;
    private int size;
    //
    private Data data;
    private Student student;
    //
    private Results results;
    private Status status;
    private int Trained;

    public Model(String stringName, Data data, Student student){
        this.name = stringName;
        this.data = data;
        this.student = student;
        status = Status.PreTrained;
        results = Results.None;
        Trained = 0;
    }
    // init model after json parsing
    public void init(Student student){
        // init data
        this.data = new Data(size, Data.Type.valueOf(type));
        this.student = student;
        //
        results = Results.None;
        status = Status.PreTrained;
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

    public Student getStudent() {
        return student;
    }

    public Data getData() {
        return data;
    }

    public boolean isCompleted() {
        return Trained * 1000 >= data.getSize();
    }


    public void setStudent(Student student) {
        this.student = student;
    }
    public synchronized void increaseTrained() {
        Trained++;
    }
}
