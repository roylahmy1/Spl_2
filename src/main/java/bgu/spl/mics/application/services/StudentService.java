package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
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
    }

    @Override
    protected void initialize() {

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


        // loop untrained models
        for (Model model: student.getModels()) {
            TrainModelEvent trainModelEvent = new TrainModelEvent(model);
            Future future = sendEvent(trainModelEvent);
        }

    }
}
