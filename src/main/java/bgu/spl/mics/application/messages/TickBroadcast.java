package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    int tickNumber;
    public TickBroadcast(int tickNumber){
        this.tickNumber = tickNumber;
    }

    public int getCurrentTime() {
        return tickNumber;
    }
}
