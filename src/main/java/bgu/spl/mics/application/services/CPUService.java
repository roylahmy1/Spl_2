package bgu.spl.mics.application.services;

import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.ExitBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;

/**
 * CPU service is responsible for handling the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    CPU cpu;
    public CPUService(String name, CPU cpu) throws InterruptedException {
        super(name);
        this.cpu = cpu;
    }

    @Override
    protected void initialize() {

        // tick
        subscribeBroadcast(TickBroadcast.class, tick -> {
            // update chunk if null
            if(!cpu.checkChunk()){
                cpu.updateChunk();
            }
            // if chunk is not empty then process a tick
            if(cpu.checkChunk()) {
                cpu.processTick();
            }
        });

        // terminate the service
        subscribeBroadcast(ExitBroadcast.class, exit -> {
            terminate();
        });

        // Wait for event in loop
            // if event is tick
            // check Chunk
            // if no chunk, updateChunk
            // check chunk again
            // if again null then finish loop
            // else, process tick

    }
}
