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

    public Model(){
        status = Status.PreTrained;
        results = Results.None;
    }
}
