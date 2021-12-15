package bgu.spl.mics.application.services;

import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.ExitBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.*;

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
    Queue<Model> modelsQueue = new PriorityQueue<>();

    public GPUService(String name, GPU.Type type) {
        super(name);
        this.gpu = new GPU(Cluster.getInstance(), type);
    }
    @Override
    protected void initialize() {

        ///
        /// TODO: implement subscribeBroadcast for all 3 events/broadcasts (YONI)
        ///

        // save collection of events of the models


        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (gpu.getModel() != null){
                if (gpu.checkVRAM())
                    gpu.fillVRAM();
                if (!gpu.checkVRAM())
                    gpu.processTick();
                else{
                    if (gpu.isCompleted()) {
                        gpu.getModel().setStatus(Model.Status.Trained);
                        gpu.clean();
                        pullNextModel();
                    }
                    // else means the service is sitting idle, waiting for cpu to finish
                }
            }
            else {
                pullNextModel();
            }
        });
        subscribeEvent(TestModelEvent.class, event -> {
            gpu.testModel(event.getModel());
        });
        subscribeEvent(TrainModelEvent.class, event -> {
            modelsQueue.add(event.getModel());
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
    private void pullNextModel(){
        if (!modelsQueue.isEmpty()){
            gpu.TrainModel(modelsQueue.poll());
        }
    }
}
