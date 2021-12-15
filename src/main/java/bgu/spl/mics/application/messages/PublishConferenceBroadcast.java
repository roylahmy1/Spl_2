package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;

public class PublishConferenceBroadcast implements Broadcast {
    ConfrenceInformation confrenceInformation;
    public void PublishConferenceBroadcast(ConfrenceInformation confrenceInformation){
       this.confrenceInformation = confrenceInformation;
    }
    public ConfrenceInformation getConfrenceInformation() {
        return confrenceInformation;
    }
}
