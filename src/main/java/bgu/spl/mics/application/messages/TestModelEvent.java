package bgu.spl.mics.application.messages;

import bgu.spl.mics.application.objects.Model;

public class TestModelEvent {
    Model model;
    public TestModelEvent(Model model){
        this.model = model;
    }
}
