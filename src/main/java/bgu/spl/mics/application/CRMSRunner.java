package bgu.spl.mics.application;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {


        ///
        /// change inputFilePath to args[0]
        ///


        Gson g = new Gson();

        //String inputFilePath = "C:\\Users\\segalyon\\Desktop\\spl2\\src\\main\\java\\bgu\\spl\\mics\\example\\exampleInput.json";//"C:\\Users\\lahmy\\my\\Spl_2\\src\\main\\java\\bgu\\spl\\mics\\example\\exampleInput.json";//args[0];
        String inputFilePath = "C:\\Users\\lahmy\\my\\Spl_2\\src\\main\\java\\bgu\\spl\\mics\\example\\exampleInput.json";
        try {
            String file = readFile(inputFilePath);
            InputFile input = g.fromJson(file, InputFile.class);

            // init all the models
            for (Student student : input.Students) {
                for (Model model : student.getModels()) {
                    model.init(student);
                }
            }
            // set global vars
            InputFile.setTimer(input.TickTime, input.Duration);
            ////

            ArrayList<MicroService> serviceList = new ArrayList<MicroService>();

            // create services
            // CPU's
            int counter = 0;
            for (int cpuCores : input.getCPUS()) {
                counter++;
                MicroService service = new CPUService("CPU service " + counter, cpuCores);
                serviceList.add(service);
            }
            // GPU's
            counter = 0;
            for (String type : input.getGPUS()) {
                counter++;
                MicroService service = new GPUService("CPU service " + counter, GPU.Type.valueOf(type));
                serviceList.add(service);
            }
            // Students
            counter = 0;
            for (Student student : input.getStudents()) {
                counter++;
                MicroService service = new StudentService("Student service " + counter, student);
                serviceList.add(service);
            }
            // Conferences
            counter = 0;
            for (ConfrenceInformation conference : input.getConferences()) {
                counter++;
                MicroService service = new ConferenceService("Conference service " + counter, conference);
                serviceList.add(service);
            }
            // time service
            TimeService timeService = new TimeService(input.getDuration(), input.getTickTime());
            serviceList.add(timeService);
            //timeService.run();


            // loop all services and init them
            for (MicroService service : serviceList) {
                Thread run = new Thread(new Runnable() {
                    public void run() {
                        service.run();
                    }
                });
                run.start();
            }

            System.out.println("Hello World!");
            } catch(IOException | InterruptedException e){
                e.printStackTrace();
            }
        }

        public static String readFile(String path) throws IOException {
        Scanner scanner = new Scanner( new File(path) );
        String text = scanner.useDelimiter("\\A").next();
        scanner.close(); // Put this call in a finally block
        return  text;
    }
}

//class InputFile {
//    ConfrenceInformation[] Conferences;
//    Student[] Students;
//    String[] GPUS;
//    int[] CPUS;
//    int TickTime;
//    int Duration;
//}
//puclass OutputFile {
//    ConfrenceInformation[] Conferences;
//    Student[] Students;
//    int cpuTimeUsed;
//    int gpuTimeUsed;
//    int batchesProcessed;
//}