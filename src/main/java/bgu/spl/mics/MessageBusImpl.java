package bgu.spl.mics;

import java.security.Provider;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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

	ConcurrentHashMap<Class<? extends Message>, Subscription> Subscriptions;
	ConcurrentHashMap<MicroService, Queue<Message>> registeredServices;
	// hold events to the future they sent
	private ConcurrentHashMap<Event<?>, Future<?>> futures;

	private static MessageBusImpl singletonInstance = new MessageBusImpl();
	private static class  SingletonHolder {
		private static MessageBusImpl  instance = new MessageBusImpl() ;
	}

	private MessageBusImpl() {
		Subscriptions = new ConcurrentHashMap<Class<? extends Message>, Subscription>();
		registeredServices = new ConcurrentHashMap<MicroService, Queue<Message>>();
		futures = new ConcurrentHashMap<Event<?>, Future<?>>();
	}

	/**
	 * Retrieves the singleton instance
	 */
	public static MessageBus getInstance() {
		return SingletonHolder.instance;
	}

	public static void resetSingleton() {
		SingletonHolder.instance = new MessageBusImpl();
		getInstance().clear();
	}
	public void clear() {
		Subscriptions = new ConcurrentHashMap<Class<? extends Message>, Subscription>();
		registeredServices = new ConcurrentHashMap<MicroService, Queue<Message>>();
		futures = new ConcurrentHashMap<Event<?>, Future<?>>();
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized(type) {
			Subscription sub = Subscriptions.get(type);
			if (sub == null)
				sub = new Subscription(m);
			else
				sub.addService(m);
			Subscriptions.put(type,sub);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub
		synchronized(type) {
			Subscription sub = Subscriptions.get(type);
			if (sub == null)
				sub = new Subscription(m);
			else
				sub.addService(m);
			Subscriptions.put(type,sub);
		}

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future future = futures.get(e);
		if (future.isDone()){
			System.out.println("is done");
		}
		else
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
		Subscription sub = Subscriptions.get(b.getClass());
		ArrayList<MicroService> services = sub.getAllServices();

		for (MicroService service: services) {
			Queue<Message> queue = registeredServices.get(service);
//			if(queue == null) {
//				int a = 1;
//			}

			if (queue != null){
				synchronized (queue){
					queue.add(b);
					queue.notifyAll(); // notify new message
				}
			}
		}

	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		Subscription sub = Subscriptions.get(e.getClass());
		if(sub != null && sub.getAllServices().size() <= 0)
			return null;
		//System.out.println("event: " + e.toString());
		//System.out.println("subs number: " + sub.getAllServices().size());
		MicroService m = sub.getNextService();

		Queue<Message> q = registeredServices.get(m);
		synchronized (q){
			q.add(e);
			q.notifyAll(); // notify new message
		}

		//
		Future<T> future = new Future<T>();
		futures.put(e, future);
		return future;
	}

	@Override
	public void register(MicroService m) {
		//
		synchronized (m) {
			Queue<Message> queue = new ConcurrentLinkedQueue<Message>();
			registeredServices.put(m, queue);
		}
	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub
		synchronized (m) {
			registeredServices.remove(m);

			// remove all subs of this service
			for (Subscription sub : Subscriptions.values()) {
				if (sub.isServiceExist(m)) // n
					sub.removeService(m);
			}
		}

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if(!registeredServices.containsKey(m))
			throw new IllegalStateException("service not registered");
		//
		Queue<Message> q=registeredServices.get(m);
		//
		synchronized (q) {
			while (q.isEmpty()){
				q.wait();
			}
			// should not have any memory safety, as nothing should remove element from queue
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
