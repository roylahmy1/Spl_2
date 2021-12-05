package bgu.spl.mics.application.objects;

import bgu.spl.mics.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GPUTest {

    private Data data1;
    private Data data2 ;
    private Data data3;
    private Student student ;

    private Model model1;
    private Model model2;
    private Model modelSmall;

    @BeforeEach
    void setUp() {
        data1 = new Data(100000, Data.Type.Images);
        data2 = new Data(50000, Data.Type.Tabular);
        student = new Student("roy","CS", Student.Degree.MSc);

        model1 = new Model("roy's model 1", data1, student);
        model2 = new Model("roy's model 2", data2, student);
    }

    @Test
    void TrainModel() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.RTX2080);
        gpu.TrainModel(model1);
        //
        assertEquals(gpu.getStatus(), Model.Status.Training);
    }
    @Test
    void gpuRun() {
        // run tests on all gpu's with 2 type's of data
        // first run when data is exactly the size of the gpu VRAM
        // second run when data is exactly twice the size of the gpu VRAM

        gpuRunRTX3090();
        gpuRunRTX2080();
        gpuRunGTX1080();
    }
    @Test
    void gpuThreadedRun() {
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

    @Test
    void gpuRunRTX3090() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.RTX3090);
        final int expectedIterations = 32 * 1; // 1 for 3090

        Data dataRTX3090 = new Data(32000, Data.Type.Text);
        Model modelRTX3090 = new Model("RTX3090", dataRTX3090, student);
        gpuRunRegularSize(gpu, modelRTX3090, expectedIterations);

        dataRTX3090 = new Data(32000 * 2, Data.Type.Images);
        modelRTX3090 = new Model("RTX3090", dataRTX3090, student);
        gpuRunDoubleSize(gpu, modelRTX3090, expectedIterations);
    }
    @Test
    void gpuRunRTX2080() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.RTX2080);
        final int expectedIterations = 16 * 2; // 2 for 2080

        Data dataRTX2080 = new Data(16000, Data.Type.Text);
        Model modelRTX2080 = new Model("TX2080", dataRTX2080, student);
        gpuRunRegularSize(gpu, modelRTX2080, expectedIterations);

        dataRTX2080 = new Data(16000 * 2, Data.Type.Images);
        modelRTX2080 = new Model("TX2080", dataRTX2080, student);
        gpuRunDoubleSize(gpu, modelRTX2080, expectedIterations);
    }
    @Test
    void gpuRunGTX1080() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.GTX1080);
        final int expectedIterations = 8 * 4; // 4 for 1080

        Data dataGTX1080 = new Data(8000 * 2, Data.Type.Text);
        Model modelGTX1080 = new Model("GTX1080", dataGTX1080, student);
        gpuRunRegularSize(gpu, modelGTX1080, expectedIterations);

        dataGTX1080 = new Data(8000 * 2, Data.Type.Images);
        modelGTX1080 = new Model("GTX1080", dataGTX1080, student);
        gpuRunDoubleSize(gpu, modelGTX1080, expectedIterations);
    }

    private void gpuRunRegularSize(GPU gpu, Model model, int expectedIterations) {
        gpu.TrainModel(model);

        // ASSUME THIS PART IS WORKING
        // let us assume Cpu finished processing
        DataBatch[] dataBatches = modelSmall.getData().getDataBatches();
        for (int i = 0; i < dataBatches.length; i++) {
            dataBatches[i].finished();
            Cluster.getInstance().storeProcessedData(dataBatches[i]);
        }

        // assume all data is processed to this point
        assertFalse(gpu.checkVRAM());
        gpu.fillVRAM(); // should fill the VRAM without any left
        //
        for (int i = 0; i < expectedIterations; i++){
            assertTrue(gpu.checkVRAM());
            gpu.processTick();
        }
        assertFalse(gpu.checkVRAM());
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

        // ASSUME THIS PART IS WORKING
        // let us assume Cpu finished processing
        DataBatch[] dataBatches = modelSmall.getData().getDataBatches();
        for (int i = 0; i < dataBatches.length; i++) {
            dataBatches[i].finished();
            Cluster.getInstance().storeProcessedData(dataBatches[i]);
        }

        // assume all data is processed to this point
        assertFalse(gpu.checkVRAM());
        gpu.fillVRAM(); // should fill the VRAM without any left
        //
        for (int i = 0; i < expectedIterations; i++){
            assertFalse(gpu.checkVRAM());
            gpu.processTick();
        }
        assertTrue(gpu.checkVRAM());
        gpu.fillVRAM();
        for (int i = 0; i < expectedIterations; i++){
            assertFalse(gpu.checkVRAM());
            gpu.processTick();
        }
        assertTrue(gpu.checkVRAM());
        assertTrue(gpu.isCompleted());
        //
        gpu.clean();
        assertNull(gpu.getModel());
    }

    @Test
    void getStatus() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.RTX2080);
        gpu.TrainModel(model1);
        //
        assertEquals(gpu.getStatus(), Model.Status.Training);
        //
        model1.setStatus(Model.Status.Trained);
        assertEquals(gpu.getStatus(), Model.Status.Tested);
    }

    @Test
    void clean() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.RTX2080);
        gpu.TrainModel(model1);
        gpu.clean();

        assertNull(gpu.getModel());
    }

    @Test
    void testModel() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.RTX2080);
        model1.setStatus(Model.Status.Trained);
        gpu.testModel(model1);
        assertEquals(model1.getStatus(), Model.Status.Tested);
        assertNotEquals(model1.getResults(), Model.Results.None);
    }
}