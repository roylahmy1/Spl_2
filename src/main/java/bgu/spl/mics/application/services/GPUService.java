package bgu.spl.mics.application.services;

import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.ExitBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Queue;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    GPU gpu;
    Collection<Model> eventmodel= new ArrayList<Model>();
    public GPUService(String name,GPU gpu) {
        super(name); this.gpu=gpu;
    }
    @Override
    protected void initialize() {

        ///
        /// TODO: implement subscribeBroadcast for all 3 events/broadcasts (YONI)
        ///

        // save collection of events of the models


        subscribeBroadcast(TickBroadcast.class, tick -> {
            eventmodel.add(gpu.getModel());

        });
        subscribeEvent(TestModelEvent.class, test -> {
            for(Model e:eventmodel) {
                if (gpu.checkVRAM())
                    gpu.fillVRAM();
                if (gpu.checkVRAM())
                    if (gpu.isCompleted()) {
                        e.setStatus(Model.Status.Trained);
                        gpu.clean();
                    }
            }
        });
        subscribeEvent(TrainModelEvent.class, train -> {
            if(gpu.getModel().getStatus()== Model.Status.Trained)
                eventmodel.add(gpu.getModel());
               else gpu.testModel(gpu.getModel());
        });
        subscribeBroadcast(ExitBroadcast.class, exit -> {
           terminate();

        });
        // Queue - ONLY events

        // Wait for event in loop
            // if event is tick
            // check VRAM
            // if empty try filling
            // check is empty again
            // if still empty check is completed, and resolve training event and clean, and pull next event
            // else just continue for next event
            // process tick
            // if start training model, call init train if empty, else push to queue
            // if test, call test if empty, else push to queue

    }
}
