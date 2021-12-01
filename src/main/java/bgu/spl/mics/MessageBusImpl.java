package bgu.spl.mics;

import java.util.Map;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	// lock A
	// subscribers hash map
	// msgType -> services, lastCalled
	// msgType -> services, lastCalled
	//

	// lock B
	// services hash map
	// service -> queue = {}
	// service -> queue = {}
	// service -> queue = {}

	// adding service will not conflict
	// removing service should block pulling event from
	//

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
		Map<, MicroService>

	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
