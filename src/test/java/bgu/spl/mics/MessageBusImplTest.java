package bgu.spl.mics;

import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import junit.framework.TestCase;

public class MessageBusImplTest extends TestCase {

    MessageBus messageBus;
    TickBroadcast tickBroadcast;
    CPUService cpuService;
    GPUService gpuService;
    ExampleEvent testEvent;
    ExampleBroadcast testBroadcast;

    public void setUp() throws Exception {
        super.setUp();

        messageBus = MessageBusImpl.getInstance();
        CPUService cpuService = new CPUService("Cpu 1");
        GPUService gpuService = new GPUService("Gpu 1");
        tickBroadcast = new TickBroadcast();
        testEvent = new ExampleEvent("Tick 1");
        testBroadcast = new ExampleBroadcast("Tick 2");
        messageBus.register(gpuService);
        messageBus.register(cpuService);
    }

    public void testGetInstance() {
        MessageBusImpl msg1 = (MessageBusImpl) MessageBusImpl.getInstance();
        MessageBusImpl msg2 = (MessageBusImpl) MessageBusImpl.getInstance();
        assertEquals(msg1, msg2);
    }

    public void testSubscribeAndSendEvent() {
        // Subscribe our test Event and end test
        messageBus.subscribeEvent(testEvent.getClass(), cpuService);
        messageBus.sendEvent(testEvent);

        // check CPU get event
        try {
            assertEquals(messageBus.awaitMessage(cpuService), testEvent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // check GPU not get event
        try {
            assertNull(messageBus.awaitMessage(gpuService));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void testSubscribeAndSendBroadcast() {
        // check a thread can get broadcast event from other threads
        Thread run1 = new Thread(new Runnable() {
            public void run() {
                messageBus.register(cpuService);
                messageBus.subscribeBroadcast(testBroadcast.getClass(), cpuService);
                try {
                    Broadcast broadcast = (Broadcast) messageBus.awaitMessage(cpuService);
                    assertEquals(broadcast, testBroadcast);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        Thread run2 = new Thread(new Runnable() {
            @Override
            public void run() {
                messageBus.register(gpuService);
                messageBus.subscribeBroadcast(testBroadcast.getClass(), gpuService);
                try {
                    Broadcast broadcast = (Broadcast) messageBus.awaitMessage(gpuService);
                    assertEquals(broadcast, testBroadcast);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                messageBus.sendBroadcast(testBroadcast);
            }
        });

        run1.start();
        run2.start();
        sender.start();
    }

    public void testComplete() throws InterruptedException {
        final String val = "asdasd";
        messageBus.subscribeEvent(testEvent.getClass(), cpuService);
        Future<String> future = messageBus.sendEvent(testEvent);
        messageBus.complete(testEvent, val);
        assertEquals(future.get(), val);
        assertTrue(future.isDone());
    }

    public void testUnregister() {
        // check unregister
        messageBus.register(cpuService);
        messageBus.unregister(cpuService);
        try {
            assertNull(messageBus.awaitMessage(cpuService));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}