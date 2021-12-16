package bgu.spl.mics.application.objects;


import junit.framework.TestCase;

public class CPUTest extends TestCase {

    private Data data1;
    private Data data2;
    private Data data3;

    private CPU cpu1;
    private CPU cpu2;
    private CPU cpu3;

    // assume gpu is tested for this
    private GPU gpu1;
    private GPU gpu2;
    private GPU gpu3;

    public void setUp() throws Exception {
        super.setUp();
        init();
    }
    private void init(){

        Cluster.resetSingleton();

        data1 = new Data(10000, Data.Type.Images);
        data2 = new Data(25000, Data.Type.Tabular);
        data3 = new Data(50000, Data.Type.Text);

        cpu1 = new CPU(32, Cluster.getInstance());
        cpu2 = new CPU(16, Cluster.getInstance());
        cpu3 = new CPU(8, Cluster.getInstance());

        gpu1 = new GPU(Cluster.getInstance(), GPU.Type.GTX1080);
        gpu2 = new GPU(Cluster.getInstance(), GPU.Type.RTX2080);
        gpu3 = new GPU(Cluster.getInstance(), GPU.Type.RTX3090);
    }

    public void testRunCPU() {
        init();
        // should take (32 / 32) * 4 ticks = 4 ticks per batch.
        runCPUTest(cpu1, gpu1, data1, 4 * 10, 10);
        // should take (32 / 16) * 2 ticks = 4 ticks per batch.
        runCPUTest(cpu2, gpu2, data2, 4 * 25, 25);
        // should take (32 / 8) * 1 ticks = 4 ticks per batch.
        runCPUTest(cpu3, gpu3, data3, 4 * 50, 50);
    }

    public void testRunThreadedCPU() throws InterruptedException {
        init();
        // like the sequential run, but we make sure no shared memory problems
        data1.setHolderGpu(gpu1);
        data2.setHolderGpu(gpu2);
        data3.setHolderGpu(gpu3);
        Cluster.getInstance().storeUnprocessedData(data1);
        Cluster.getInstance().storeUnprocessedData(data2);
        Cluster.getInstance().storeUnprocessedData(data3);

        Thread run1 = new Thread(new Runnable() {
            public void run() {
                runCPUTestThreaded(cpu1);
            }
        });
        Thread run2 = new Thread(new Runnable() {
            public void run() {
                runCPUTestThreaded(cpu2);
            }
        });
        Thread run3 = new Thread(new Runnable() {
            public void run() {
                runCPUTestThreaded(cpu3);
            }
        });
        run1.start();
        run2.start();
        run3.start();

        run1.join();
        run2.join();
        run3.join();
       // runCPUTestThreaded(cpu1);
        //runCPUTestThreaded(cpu2);
        //runCPUTestThreaded(cpu3);

        // should not have anymore chunks
        cpu1.updateChunk();
        assertTrue(cpu1.isEmptyChunk());
        cpu2.updateChunk();
        assertTrue(cpu2.isEmptyChunk());
        cpu3.updateChunk();
        assertTrue(cpu3.isEmptyChunk());
        // at the end there should be exactly the expected size
        DatabatchQueue databatchQueue1 = Cluster.getInstance().getProcessedData(gpu1, 1000); //
        DatabatchQueue databatchQueue2 = Cluster.getInstance().getProcessedData(gpu2, 1000); //
        DatabatchQueue databatchQueue3 = Cluster.getInstance().getProcessedData(gpu3, 1000); //
        assertEquals(databatchQueue1.size(), 10);
        assertEquals(databatchQueue2.size(), 25);
        assertEquals(databatchQueue3.size(), 50);
    }

    private void runCPUTest(CPU cpu, GPU gpu, Data data, int expectedTicks, int expectedSize) {
        data.setHolderGpu(gpu);
        Cluster.getInstance().storeUnprocessedData(data);

        cpu.updateChunk();
        for (int i = 0; i < expectedTicks; i++) {
            if (cpu.isEmptyChunk()){
                cpu.updateChunk();
            }
            cpu.processTick();
        }
        // should not be anymore chunks
        cpu.updateChunk();
        assertTrue(cpu.isEmptyChunk());
        // at the end there should be exactly the expected size
        DatabatchQueue databatchQueue = Cluster.getInstance().getProcessedData(gpu, expectedSize);
        assertEquals(databatchQueue.size(), expectedSize);
    }
    private void runCPUTestThreaded(CPU cpu) {
        cpu.updateChunk();
        while(!cpu.isEmptyChunk()){
            cpu.processTick();
            if (cpu.isEmptyChunk()){
                cpu.updateChunk();
            }
        }
    }

    public void testCheckAndUpdateChunk() {

        init();
        Cluster.getInstance().storeUnprocessedData(data1);

        // should take (32 / 32) * 4 ticks = 4 ticks per batch.

        assertTrue(cpu1.isEmptyChunk());
        cpu1.updateChunk();
        assertFalse(cpu1.isEmptyChunk());
    }
}