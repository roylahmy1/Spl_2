package bgu.spl.mics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class FutureTest {

    Future<Integer> future;
    @BeforeEach
    void setUp() {
        future = new Future<>();
    }

    @Test
    void getAndResolve() {
        // check if the future can wait for proper results
        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                Integer result = 0;
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

    @Test
    void isDone() {
        assertFalse(future.isDone());
        future.resolve(1221);
        assertTrue(future.isDone());
    }

    @Test
    void getAndResolve2() {
        // check that after X time the thread dies due to being unresolved
        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                Integer result = 0;
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
    @Test
    void getAndResolve2IsAlive() {
        // check that after X time the thread dies due to being unresolved
        Thread runTest2 = new Thread(new Runnable() {
            @Override
            public void run() {
                future.get(2, TimeUnit.SECONDS);
            }
        });
        runTest2.start();
        try {
            Thread.sleep(2*1000);
            assertFalse(runTest2.isAlive());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}