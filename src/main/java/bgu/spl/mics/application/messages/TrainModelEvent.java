package bgu.spl.mics.application.messages;

import bgu.spl.mics.application.objects.Model;

public class TrainModelEvent {
    Model model;
    public TrainModelEvent(Model model){
        this.model = model;
    }
}
