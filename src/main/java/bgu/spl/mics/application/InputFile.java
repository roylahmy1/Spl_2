package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Student;

public class InputFile {
    ConfrenceInformation[] Conferences;
    Student[] Students;
    String[] GPUS;
    int[] CPUS;
    int TickTime;
    int Duration;

    static int globalTickTime;
    static int globalDuration;
    public static void setTimer(int tickTime, int duration){
        globalTickTime = tickTime;
        globalDuration = duration;
    }

    public static int getGlobalDuration() {
        return globalDuration;
    }
    public static int getGlobalTickTime(){
        return globalTickTime;
    }

    public ConfrenceInformation[] getConferences() {
        return Conferences;
    }

    public int getDuration() {
        return Duration;
    }

    public Student[] getStudents() {
        return Students;
    }

    public int getTickTime() {
        return TickTime;
    }

    public int[] getCPUS() {
        return CPUS;
    }

    public String[] getGPUS() {
        return GPUS;
    }
}
