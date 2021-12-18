package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

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
    private Queue<Future> futures = new ConcurrentLinkedQueue<>();
    int modelIndex = 0;
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
        subscribeBroadcast(ExitBroadcast.class, exit -> {
            terminate();
        });
    }

    private void testAndPublishModel(Model model) throws InterruptedException {
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
        int i = 0;
        for (i = 0; i < student.getModels().length; i++) {
            // train model
            Model model = student.getModels()[i];
            TrainModelEvent trainModelEvent = new TrainModelEvent(model);
            Future futureTrain = sendEvent(trainModelEvent);
            try {
                Model trainedModel = (Model)futureTrain.get(5, TimeUnit.SECONDS);
                if (trainedModel != null){
                    System.out.println("Train model finished");
                    // test model
                    TestModelEvent testModelEvent = new TestModelEvent(trainedModel);
                    Future futureTest = sendEvent(testModelEvent);
                    Model testedModel = (Model)futureTest.get();
                    System.out.println("Test model finished");
                    //
                    if (model.getResults() == Model.Results.Good) {
                        PublishResultsEvent publishResultsEvent = new PublishResultsEvent(model);
                        sendEvent(publishResultsEvent);
                        System.out.println("publish model finished");
                    } else {
                        System.out.println(" model finished bad");
                    }
                }
                else{
                    if(trainModelEvent.getModel().isCompleted()){
                        modelIndex++;
                        System.out.println("model future not working " + modelIndex);
                    }
                    else{
                        int b = 1;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
