package bgu.spl.mics;

import java.security.Provider;
import java.util.*;
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
	HashMap<MicroService, Queue<Message>> registeredServices;
	// hold events to the future they sent
	private HashMap<Event<?>, Future<?>> futures;

	private static MessageBusImpl singletonInstance = new MessageBusImpl();
	private static class  SingletonHolder {
		private static MessageBusImpl  instance = new MessageBusImpl() ;
	}

	private MessageBusImpl() {
		Subscriptions = new HashMap<Class<? extends Message>, Subscription>();
		registeredServices = new HashMap<MicroService, Queue<Message>>();
		futures = new HashMap<Event<?>, Future<?>>();
	}

	/**
	 * Retrieves the singleton instance
	 */
	public static MessageBus getInstance() {
		return SingletonHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		Subscription sub = new Subscription(m);
		Subscriptions.put(type, sub);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub
		Subscription sub=new Subscription(m);
		Subscriptions.put(type,sub);

	}

	@Override
	public <T> void complete(Event<T> e, T result) {

		Future future = futures.get(e);
		future.resolve(result);
//
//		// TODO Auto-generated method stub
//		result = (T) Subscriptions.get(e);
//		futures.put(e, (Future<?>) result);
//		this.notify();
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub
		Subscription sub = Subscriptions.get(b);
		ArrayList<MicroService> services = sub.getAllServices();

		for (MicroService service: services) {
			Queue<Message> queue = registeredServices.get(b);
			synchronized (queue){
				queue.add(b);
			}
		}

	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		Subscription sub = Subscriptions.get(e);
		MicroService m = sub.getNextService();

		Queue<Message> queue = registeredServices.get(m);
		synchronized (queue){
			queue.add(e);
			notifyAll(); // notify new message
		}

		//
		Future<T> future = new Future<T>();
		futures.put(e, future);
		return future;
	}

	@Override
	public void register(MicroService m) {
		//
		Queue<Message> queue = new PriorityQueue<Message>();
		registeredServices.put(m, queue);
	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub
		registeredServices.remove(m);

		// remove all subs of this service
		for (Subscription sub: Subscriptions.values()) {
			if (sub.isServiceExist(m)) // n
				sub.removeService(m);
		}

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(!registeredServices.containsKey(m))
			throw new IllegalStateException("service not registered");
		//
		Queue<Message> q=registeredServices.get(m);
		//
		while (q.isEmpty()){
			wait();
		}
		// should not have any memory safety, as nothing should remove element from queue
		synchronized (q) {
			Message r=q.remove();
			return r;
		}

		//return null;
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
		public boolean isServiceExist(MicroService service){
			return services.contains(service);
		}
		public synchronized void removeService(MicroService service){
			services.remove(service);
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
