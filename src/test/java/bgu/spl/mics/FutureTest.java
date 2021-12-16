package bgu.spl.mics;

import junit.framework.TestCase;
import java.util.concurrent.TimeUnit;

public class FutureTest extends TestCase {

    Future<Integer> future;
    public void setUp() throws Exception {
        super.setUp();
        future = new Future<>();
    }

    public void testGetAndResolve() {
        // check if the future can wait for proper results
        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                int result = 0;
                try {
                    result = future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                assertEquals(result, 1234);
            }
        });
        Thread writer = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                assertFalse(future.isDone());
                future.resolve(1234);
                assertTrue(future.isDone());
            }
        });
        reader.start();
        writer.start();
    }

    public void testIsDone() {
        assertFalse(future.isDone());
        future.resolve(1234);
        assertTrue(future.isDone());
    }

    public void testGetAndResolve2() {
        // check that after X time the thread dies due to being unresolved
        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                int result = 0;
                result = future.get(2, TimeUnit.SECONDS);
                assertEquals(result, 1234);
            }
        });
        Thread writer = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                assertFalse(future.isDone());
                future.resolve(1234);
                assertTrue(future.isDone());
            }
        });
        reader.start();
        writer.start();

    }

    public void GetAndResolve2IsAlive() {
        // check that after X time the thread dies due to being unresolved
        Thread runTest2 = new Thread(new Runnable() {
            @Override
            public void run() {
                assertNull(future.get(100, TimeUnit.MILLISECONDS));
            }
        });
        runTest2.start();
        try {
            Thread.sleep(3*100);
            assertFalse(runTest2.isAlive());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}