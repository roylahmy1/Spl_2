package bgu.spl.mics.application.objects;

import bgu.spl.mics.Future;
import junit.framework.TestCase;

public class GPUTest extends TestCase {

    private Data data1;
    private Data data2 ;
    private Data data3;
    private Student student ;

    private Model model1;
    private Model model2;
    private Model modelSmall;

    public void setUp() throws Exception {
        super.setUp();

        data1 = new Data(100000, Data.Type.Images);
        data2 = new Data(50000, Data.Type.Tabular);
        student = new Student("roy","CS", Student.Degree.MSc);

        model1 = new Model("roy's model 1", data1, student);
        model2 = new Model("roy's model 2", data2, student);
    }

    public void testTrainModel() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.RTX2080);
        gpu.TrainModel(model1);
        //
        assertEquals(gpu.getStatus(), Model.Status.Training);
        assertNotNull(gpu.getModel());
    }
    public void testGpuRun() {
        // run tests on all gpu's with 2 type's of data
        // first run when data is exactly the size of the gpu VRAM
        // second run when data is exactly twice the size of the gpu VRAM

        gpuRunRTX3090();
        gpuRunRTX2080();
        gpuRunGTX1080();
    }
    public void testGpuThreadedRun() {
        // run tests on all gpu's with 2 type's of data
        // first run when data is exactly the size of the gpu VRAM
        // second run when data is exactly twice the size of the gpu VRAM

        Thread run1 = new Thread(new Runnable() {
            public void run() {
                gpuRunRTX3090();
            }
        });
        Thread run2 = new Thread(new Runnable() {
            public void run() {
                gpuRunRTX2080();
            }
        });
        Thread run3 = new Thread(new Runnable() {
            public void run() {
                gpuRunGTX1080();
            }
        });
        run1.start();
        run2.start();
        run3.start();
    }

    public void gpuRunRTX3090() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.RTX3090);
        final int expectedIterations = 32 * 1; // 1 for 3090

        Data dataRTX3090 = new Data(32000, Data.Type.Text);
        Model modelRTX3090 = new Model("RTX3090", dataRTX3090, student);
        gpuRunRegularSize(gpu, modelRTX3090, expectedIterations);

        dataRTX3090 = new Data(32000 * 2, Data.Type.Images);
        modelRTX3090 = new Model("RTX3090", dataRTX3090, student);
        gpuRunDoubleSize(gpu, modelRTX3090, expectedIterations);
    }
    public void gpuRunRTX2080() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.RTX2080);
        final int expectedIterations = 16 * 2; // 2 for 2080

        Data dataRTX2080 = new Data(16000, Data.Type.Text);
        Model modelRTX2080 = new Model("TX2080", dataRTX2080, student);
        gpuRunRegularSize(gpu, modelRTX2080, expectedIterations);

        dataRTX2080 = new Data(16000 * 2, Data.Type.Images);
        modelRTX2080 = new Model("TX2080", dataRTX2080, student);
        gpuRunDoubleSize(gpu, modelRTX2080, expectedIterations);
    }
    public void gpuRunGTX1080() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.GTX1080);
        final int expectedIterations = 8 * 4; // 4 for 1080

        Data dataGTX1080 = new Data(8000, Data.Type.Text);
        Model modelGTX1080 = new Model("GTX1080", dataGTX1080, student);
        gpuRunRegularSize(gpu, modelGTX1080, expectedIterations);

        dataGTX1080 = new Data(8000 * 2, Data.Type.Images);
        modelGTX1080 = new Model("GTX1080", dataGTX1080, student);
        gpuRunDoubleSize(gpu, modelGTX1080, expectedIterations);
    }

    private void gpuRunRegularSize(GPU gpu, Model model, int expectedIterations) {
        gpu.TrainModel(model);

        // PREAPARE TEST
        // let us assume Cpu finished processing
        model.getData().setHolderGpu(gpu);
        Chunk chunk = model.getData().getChunk(model.getData().getSize());
        DataBatch db = chunk.getNext();
        while(db != null) {
            db.finished();
            Cluster.getInstance().storeProcessedData(db);
            db = chunk.getNext();
        }

        // EXECUTE TEST
        // assume all data is processed to this point
        assertTrue(gpu.isEmptyVRAM());
        gpu.fillVRAM(); // should fill the VRAM without any left
        //
        for (int i = 0; i < expectedIterations; i++){
            gpu.processTick();
        }
        assertTrue(gpu.isEmptyVRAM());
        assertTrue(gpu.isCompleted());
        //
        gpu.clean();
        assertNull(gpu.getModel());
    }
    private void gpuRunDoubleSize(GPU gpu, Model model, int expectedIterations) {
        gpu.TrainModel(model);

        //
        // check VRAM isn't taking more then it should
        //

        // PREAPARE TEST
        // let us assume Cpu finished processing
        model.getData().setHolderGpu(gpu);
        Chunk chunk = model.getData().getChunk(model.getData().getSize());
        DataBatch db = chunk.getNext();
        while(db != null) {
            db.finished();
            Cluster.getInstance().storeProcessedData(db);
            db = chunk.getNext();
        }

        // EXECUTE TEST

        // assume all data is processed to this point
        assertTrue(gpu.isEmptyVRAM());
        gpu.fillVRAM(); // should fill the VRAM without any left
        //
        for (int i = 0; i < expectedIterations; i++){
            gpu.processTick();
        }
        assertTrue(gpu.isEmptyVRAM());
        gpu.fillVRAM();
        for (int i = 0; i < expectedIterations; i++){
            gpu.processTick();
        }
        assertTrue(gpu.isEmptyVRAM());
        assertTrue(gpu.isCompleted());
        //
        gpu.clean();
        assertNull(gpu.getModel());
    }

    public void testGetStatus() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.RTX2080);
        gpu.TrainModel(model1);
        //
        assertEquals(gpu.getStatus(), Model.Status.Training);
        //
        gpu.testModel(model1);
        assertEquals(gpu.getStatus(), Model.Status.Tested);
    }

    public void testClean() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.RTX2080);
        gpu.TrainModel(model1);
        gpu.clean();

        assertNull(gpu.getModel());
    }

    public void testModel() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.RTX2080);
        model1.setStatus(Model.Status.Trained);
        gpu.testModel(model1);
        assertEquals(model1.getStatus(), Model.Status.Tested);
        assertNotSame(model1.getResults(), Model.Results.None);
        //assertNotNull(gpu.getModel());
    }
}