package bgu.spl.mics.application;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        if (args.length != 2){
            throw new IllegalArgumentException("input args not valid");
        }
        // input file path
        // YONI C:\\Users\\segalyon\\Desktop\\spl2\\src\\main\\java\\bgu\\spl\\mics\\example\\exampleInput.json
        // ROY C:\\Users\\lahmy\\my\\Spl_2\\src\\main\\java\\bgu\\spl\\mics\\example\\exampleInput.json

        // output file path
        // ROY "C:\\Users\\lahmy\\my\\Spl_2\\src\\main\\java\\bgu\\spl\\mics\\example\\output.json";

        // "C:\\Users\\lahmy\\my\\Spl_2\\src\\main\\java\\bgu\\spl\\mics\\example\\exampleInput.json", "C:\\Users\\lahmy\\my\\Spl_2\\src\\main\\java\\bgu\\spl\\mics\\example\\output.json";

        String inputFilePath = args[0];
        String outputFilePath = args[1];

        Gson g = new Gson();
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

            /// create services
            ArrayList<MicroService> serviceList = new ArrayList<MicroService>();
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
                MicroService service = new GPUService("GPU service " + counter, GPU.Type.valueOf(type));
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

            // init all services
            ArrayList<Thread> threads = new ArrayList<Thread>();
            for (MicroService service : serviceList) {
                Thread run = new Thread(new Runnable() {
                    public void run() {
                        service.run();
                    }
                });
                threads.add(run);
                run.start();
            }

            // wait for all to finish
            for (Thread thread : threads) {
                thread.join();
            }

            // create output file
            OutputFile output = new OutputFile();
            output.setBatchesProcessed(Cluster.getInstance().getBatchesProcessed());
            output.setCpuTimeUsed(Cluster.getInstance().getCpuTime());
            output.setGpuTimeUsed(Cluster.getInstance().getGpuTime());
            output.setStudents(input.Students);
            output.setConferences(input.getConferences());

            // parse file to json
            String json = g.toJson(output);
            writeFile(json, outputFilePath);
            System.out.println("program finished!");
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
    public static void writeFile(String contents, String path) throws IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(path), "utf-8"))) {
            writer.write(contents);
        }
        Files.write(Paths.get(path), contents.getBytes());
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