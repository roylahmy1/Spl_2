package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    ConfrenceInformation confrenceInformation;
    int i=0;
    ArrayList<Model> modelsQueue = new ArrayList<>();
    public ConferenceService(String name, ConfrenceInformation confrenceInformation) {
        super(name);
      //  ConfrenceInformation confrenceInformation = new ConfrenceInformation(name, date);
      //  this.confrenceInformation=new ConfrenceInformation(name,date);
        this.confrenceInformation=confrenceInformation;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if(!confrenceInformation.hasPublished())
            {
                confrenceInformation.publish();
                i++;
            }
            pullNextModel();
        });
        subscribeBroadcast(PublishConferenceBroadcast.class, publish -> {
            ArrayList<Model> publications=publish.getConfrenceInformation().getPublications();
            for(Model publication: publications)
            {
                modelsQueue.add(publication);
            }
        });
        subscribeBroadcast(ExitBroadcast.class, exit -> {
            terminate();
        });


    }
    private void pullNextModel(){
        if (!modelsQueue.isEmpty()){
            confrenceInformation.addPublication(modelsQueue.get(i));
        }
    }
}
