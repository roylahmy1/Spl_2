package bgu.spl.mics.application.services;

import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.ExitBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	int timer;
	int maxTime;
	int tickInterval;

	/**
	 * @param maxTime: the total time of the run
	 * @param tickInterval: tick interval size in milliseconds
	 */
	public TimeService(int maxTime, int tickInterval) {
		super("timer");
		timer = 0;
		this.tickInterval = tickInterval;
		this.maxTime = maxTime;
	}

	@Override
	protected void initialize() {
		int totalTicks = maxTime / tickInterval;

		for (int t = 0; t <= totalTicks; t++){
//			if (t % 50 == 0){
//				System.out.println("ticker alive");
//			}
			TickBroadcast tick = new TickBroadcast(t);
			sendBroadcast(tick);
			//
			try {
				Thread.sleep(tickInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		sendBroadcast(new ExitBroadcast());
	}
}
