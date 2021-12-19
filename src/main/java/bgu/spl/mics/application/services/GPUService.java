package bgu.spl.mics.application.services;

import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    TrainModelEvent currentEvent;
    Queue<TrainModelEvent> modelsQueue = new ConcurrentLinkedQueue<>();

    public GPUService(String name, GPU.Type type) {
        super(name);
        this.gpu = new GPU(Cluster.getInstance(), type);

        //

        ///
        /// TODO: implement subscribeBroadcast for all 3 events/broadcasts (YONI)
        ///

        // save collection of events of the models


        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (gpu.getModel() != null){
                //
                if (gpu.isEmptyVRAM()) {
                    gpu.fillVRAM();
                    //System.out.println(" GPU fill VRAM" + tick.getCurrentTime());
                }
                if (!gpu.isEmptyVRAM())
                {
                    gpu.processTick();
                    Cluster.getInstance().increaseCpuTime();
                }
                else{
                    if (gpu.isCompleted()) {
                        System.out.println(" GPU COMPLETED: at time" + tick.getCurrentTime());
                        synchronized (gpu.getModel()) {
                            if (gpu.getModel().getStatus() == Model.Status.Training)
                                gpu.getModel().setStatus(Model.Status.Trained);
                        }
                        complete(currentEvent, gpu.getModel());
                        gpu.clean();
                        //gpuReady();
                    }
                    //System.out.println(" GPU idel, no VRAM" + tick.getCurrentTime());
                    // else means the service is sitting idle, waiting for cpu to finish
                }
            }
            else {
                //gpuReady();
            }
        });
        subscribeEvent(TestModelEvent.class, testEvent -> {
            synchronized (testEvent.getModel()) {
                gpu.testModel(testEvent.getModel());
            }
            complete(testEvent, testEvent.getModel());
        });
        subscribeEvent(TrainModelEvent.class, event -> {
            if (gpu.getModel() != null) {
                // prevent busy wait
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendEvent(event);
            }
            else{
                currentEvent = event;
                gpu.TrainModel(event.getModel());
            }
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
    @Override
    protected void initialize() {

    }
    private void gpuReady(){
        if (!modelsQueue.isEmpty()){
            currentEvent = modelsQueue.poll();
            gpu.TrainModel(currentEvent.getModel());
        }
    }
}
