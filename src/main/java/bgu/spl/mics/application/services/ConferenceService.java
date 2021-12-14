package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

import java.util.ArrayList;
import java.util.Collection;

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
    //Collection<Event> eventmodel= new ArrayList<Event>();
    Model model;
    public ConferenceService(String name, int date, ConfrenceInformation confrenceInformation, Model model) {
        super(name);
      //  ConfrenceInformation confrenceInformation = new ConfrenceInformation(name, date);
        this.confrenceInformation=confrenceInformation;
        this.model=model;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tick -> {
            confrenceInformation.addPublication(model);

        });
        sendBroadcast(PublishConferenceBroadcast.class, publish -> {
            for(Model model: confrenceInformation.getPublications()) {
                if (model.getResults()== Model.Results.Good) {
                    confrenceInformation.publish();
                }
            }

        });

    }
}
