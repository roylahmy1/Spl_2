package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

public class PublishResultsEvent implements Event {

    Model trainedModel;
    public void PublishConferenceBroadcast(Model trainedModel){
        this.trainedModel = trainedModel;
    }
    public Model getTrainedModel() {
        return trainedModel;
    }
}
