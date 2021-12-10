package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;

/**
 * CPU service is responsible for handling the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    public CPUService(String name) throws InterruptedException {
        super(name);

        //
//        do {
//
//        }
//        while (MessageBusImpl.getInstance().awaitMessage(this)){
//
//        }

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
        // TODO Implement this

    }
}
