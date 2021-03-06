package bgu.spl.mics.application.objects;

import java.util.ArrayList;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private Model[] models;
    private int publications;
    private int papersRead;
    private Object papersReadLock;

    public Student (String name, String department, Degree status){
        this.name = name;
        this.department = department;
        this.status = status;
        publications = 0;
        papersRead = 0;
        papersReadLock = new Object();
    }

    public Degree getStatus() {
        return status;
    }

    public Model[] getModels() {
        return models;
    }

    public int getPapersRead() {
//        synchronized(papersReadLock){
            return papersRead;
//        }
    }

    public void addPapersRead(){
//        synchronized(papersReadLock){
            papersRead = papersRead + 1;
//        }
    }

    public String getName() {
        return name;
    }
    public void removeUntrained(){
        ArrayList<Model> newModels = new ArrayList<Model>();
        for (Model model: models) {
            model.setStudent(null);
            model.getData().setHolderGpu(null);
            if (model.isCompleted())
                newModels.add(model);
        }
        models = newModels.toArray(new Model[0]);
    }
}
