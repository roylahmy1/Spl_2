package bgu.spl.mics;

import java.security.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	// A (Subscriptions)
	// subscribers hash map (lock (1) for adding event - to prevent 2 added msgTypes the same time)
	// msgType (subscription) -> services, lastCalled, lock (2)
	// msgType (subscription) -> services, lastCalled, lock (2)
	//

	// read from A by msgType locked - only when updating the lastCalled (2)
	// write to A by msgType locked (2)

	// B (registeredServices)
	// services hash map (lock (3) for adding service - to prevent 2 added/removed services the same time) / for safety reasons - will not be a problem
	// service -> queue = { events }, lock (4)
	// service -> queue = {}, lock (4)
	// service -> queue = {}, lock (4)

	// adding and removing from a queue should be interchangeably locked (4 - will be a monitor of the service itself)

	// functions:
	// getMsgType - get (services, lastCalled, lock) by the msgType

	HashMap<Class<? extends Message>, Subscription> Subscriptions;
	HashMap<MicroService, Queue<MicroService>> registeredServices;

	private static MessageBusImpl singletonInstance = new MessageBusImpl();

	private MessageBusImpl() {
		Subscriptions = new HashMap<Class<? extends Message>, Subscription>();
		registeredServices = new HashMap<MicroService, Queue<MicroService>>();
	}

	/**
	 * Retrieves the singleton instance
	 */
	public static MessageBus getInstance() {
		return singletonInstance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {


	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(MicroService m) {
//		Map<, MicroService>

	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		while (){
			wait();
		}


		return null;
	}

	private class Subscription {
		private int lastCalled = 0;
		private ArrayList<MicroService> services = new ArrayList<MicroService>();
		public Subscription(MicroService initService) {
			services.add(initService);
		}
		public synchronized void addService(MicroService service){
			services.add(service);
		}
		public synchronized MicroService getNextService() {
			MicroService service = services.get(lastCalled);
			// update last called according to RoundRobin Rules
			lastCalled = (lastCalled + 1) % services.size();
			return  service;
		}
		public synchronized ArrayList<MicroService> getAllServices(){
			return services;
		}
	}

}
