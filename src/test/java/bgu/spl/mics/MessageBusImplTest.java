package bgu.spl.mics;

import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.GPU;
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
    CPUService cpuService1;
    GPUService gpuService;
    ExampleEvent testEvent;
    ExampleBroadcast testBroadcast;

    public void setUp() throws Exception {
        super.setUp();
//
//        CPU cpu = new CPU(8);
//        GPU gpu = new GPU(Cluster.getInstance(), .Type.RTX2080);

        messageBus = MessageBusImpl.getInstance();
//        CPUService cpuService = new CPUService("Cpu 1", cpu);
//        GPUService gpuService = new GPUService("Gpu 1", gpu);
        tickBroadcast = new TickBroadcast();
        testEvent = new ExampleEvent("Tick 1");
        testBroadcast = new ExampleBroadcast("Tick 2");
        messageBus.register(gpuService);
        messageBus.register(cpuService);
        messageBus.register(cpuService1);
    }

    public void testGetInstance() {
        MessageBusImpl msg1 = (MessageBusImpl) MessageBusImpl.getInstance();
        MessageBusImpl msg2 = (MessageBusImpl) MessageBusImpl.getInstance();
        assertEquals(msg1, msg2);
    }

    public void testSubscribeAndSendEvent() {
        // Subscribe our test Event and end test
        messageBus.subscribeEvent(testEvent.getClass(), cpuService1);
        messageBus.sendEvent(testEvent);

        // check CPU get event
        try {
            assertEquals(messageBus.awaitMessage(cpuService1), testEvent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        // check GPU not get event
//        try {
//            assertNull(messageBus.awaitMessage(gpuService));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    public void testSubscribeAndSendBroadcast() {
        MessageBusImpl.resetSingleton();
        messageBus.register(cpuService);
        messageBus.register(gpuService);
        messageBus.subscribeBroadcast(testBroadcast.getClass(), cpuService);
        messageBus.subscribeBroadcast(testBroadcast.getClass(), gpuService);
        // check a thread can get broadcast event from other threads
        Thread run1 = new Thread(new Runnable() {
            public void run() {
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
        MessageBusImpl.resetSingleton();
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
        Object exception = null;
        try {
            messageBus.awaitMessage(cpuService);
        } catch (Exception ex) {
            exception = ex;
        }
        assertNotNull(exception);
    }
}