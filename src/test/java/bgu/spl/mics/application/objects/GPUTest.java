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
    private Model model3;

    @BeforeEach
    void setUp() {
        Data data1 = new Data(100000, Data.Type.Images);
        Data data2 = new Data(10000, Data.Type.Tabular);
        Data data3 = new Data(200000, Data.Type.Text);
        Student student = new Student("roy","CS", Student.Degree.MSc);

        Model model1 = new Model("roy's model 1", data1, student);
        Model model2 = new Model("roy's model 2", data2, student);
        Model model3 = new Model("roy's model 3", data3, student);
    }

    @Test
    void initProcess() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.RTX2080);
        gpu.initProcess(model1);
        //
        assertEquals(gpu.getStatus(), Model.Status.Training);
        //
        gpu.clean();
        model3.setStatus(Model.Status.Trained);
        gpu.initProcess(model3);
        assertEquals(gpu.getStatus(), Model.Status.Tested);
    }

    @Test
    void processTick() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.RTX2080);
        gpu.initProcess(model1);


    }

    @Test
    void getStatus() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.RTX2080);
        gpu.initProcess(model1);
        //
        assertEquals(gpu.getStatus(), Model.Status.Training);
        //
        model3.setStatus(Model.Status.Trained);
        assertEquals(gpu.getStatus(), Model.Status.Tested);
    }

    @Test
    void clean() {
        GPU gpu = new GPU(Cluster.getInstance(), GPU.Type.RTX2080);
        gpu.initProcess(model1);
        gpu.clean();

        assertNull(gpu.);
    }
}