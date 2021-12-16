package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.ArrayList;
import java.util.Queue;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {

    private Student student;
    public StudentService(String name, Student student) {
        super(name);
        this.student = student;


        subscribeBroadcast(PublishConferenceBroadcast.class, publish -> {
            ConfrenceInformation confrenceInformation = publish.getConfrenceInformation();
            // loop publications
            for (Model model: confrenceInformation.getPublications()) {
                // don't read his own publication
                if (model.getStudent() != student){
                    student.addPapersRead();
                }
            }
        });
        //
    }

    @Override
    protected void initialize() {


        /** What to think about
        @side_1:
         student sends all the models together, without waiting for results
         @negatives: OS can give this student full rights, making all other students wait

        @side_2:
         student sends model, waits for result, and then sends another in loop
         @negatives: can have useless GPU's

        */



        // loop untrained models
        for (Model model: student.getModels()) {
            try {
                // train model
                TrainModelEvent trainModelEvent = new TrainModelEvent(model);
                Future futureTrain = sendEvent(trainModelEvent);
                model = (Model)futureTrain.get();
                // test model
                TestModelEvent testModelEvent = new TestModelEvent(model);
                Future futureTest = sendEvent(testModelEvent);
                model = (Model)futureTest.get();
                //
                if (model.getResults() == Model.Results.Good){
                  PublishResultsEvent publishResultsEvent = new PublishResultsEvent(model);
                  sendEvent(publishResultsEvent);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
