package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.GPU;

public class TrainModelEvent implements Event {
    Model model;
    public TrainModelEvent(Model model){
        this.model = model;
    }
    public Model getModel() {
        return model;
    }
}
