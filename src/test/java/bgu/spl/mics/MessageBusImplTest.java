package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    MessageBusImpl messageBusImpl;
    ExampleEvent testEvent;
    ExampleBroadcast testBroadcast;

    @BeforeEach
    public void setUp(){
        messageBusImpl = messageBusImpl.getInstance();
        testEvent = new ExampleEvent(m1.getName());
        testBroadcast = new ExampleBroadcast(m1.getName());
        messageBroker.register(m1);

    }

    @Test
    void getInstance() {
    }

    @Test
    void subscribeEvent() {
    }

    @Test
    void subscribeBroadcast() {
    }

    @Test
    void complete() {
    }

    @Test
    void sendBroadcast() {
    }

    @Test
    void sendEvent() {
    }

    @Test
    void register() {
    }

    @Test
    void unregister() {
    }
}