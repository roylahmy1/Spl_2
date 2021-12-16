package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.InputFile;
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
    int timer = 0;
    ArrayList<Model> modelsQueue = new ArrayList<>();
    public ConferenceService(String name, ConfrenceInformation confrenceInformation) {
        super(name);
        this.confrenceInformation = confrenceInformation;

        //
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (timer <= confrenceInformation.getDate())
            {
                timer += InputFile.getGlobalTickTime();
            }
            // publish all results
            if (timer > confrenceInformation.getDate()) {
                confrenceInformation.publish();
                PublishConferenceBroadcast publishConferenceBroadcast = new PublishConferenceBroadcast(confrenceInformation);
                sendBroadcast(publishConferenceBroadcast);
                terminate();
            }
        });
        subscribeEvent(PublishResultsEvent.class, publish -> {
            confrenceInformation.addPublication(publish.getModel());
        });
        subscribeBroadcast(ExitBroadcast.class, exit -> {
            terminate();
        });
    }

    @Override
    protected void initialize() {
        //

    }
}
