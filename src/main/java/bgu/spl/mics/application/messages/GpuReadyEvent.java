package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.GPU;

public class GpuReadyEvent implements Event {
    GPU sender;
    public GpuReadyEvent(GPU gpu){
        sender = gpu;
    }
    public GPU getSender() {
        return sender;
    }
}
