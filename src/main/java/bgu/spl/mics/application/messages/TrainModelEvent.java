package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.GPU;

public class TrainModelEvent implements Event {
    Model model;
    GPU senderGpu;
    public TrainModelEvent(Model model, GPU gpu){
        this.model = model;
        this.senderGpu = gpu;
    }
    public Model getModel() {
        return model;
    }

    public GPU getSenderGpu() {
        return senderGpu;
    }
}
