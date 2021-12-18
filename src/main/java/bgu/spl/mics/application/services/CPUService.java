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
    int tickCounter = 0;
    CPU cpu;
    public CPUService(String name, int cores) throws InterruptedException {
        super(name);
        this.cpu = new CPU(cores, Cluster.getInstance());


        // tick
        subscribeBroadcast(TickBroadcast.class, tick -> {
//            tickCounter++;
//            if (tickCounter % 50 == 0){
//                System.out.println("ticker alive: " + cpu.toString());
//            }
            // update chunk if null
            if(cpu.isEmptyChunk()){
                cpu.updateChunk();
                //System.out.println("cpu update chunk: " + cpu.toString());
            }
            // if chunk is not empty then process a tick
            if(!cpu.isEmptyChunk()) {
                cpu.processTick();
                Cluster.getInstance().increaseCpuTime();
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

    @Override
    protected void initialize() {

    }
}
