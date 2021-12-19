package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class OutputFile {
    ConfrenceInformation[] Conferences;
    Student[] Students;
    int cpuTimeUsed;
    int gpuTimeUsed;
    int batchesProcessed;

    public void setConferences(ConfrenceInformation[] conferences) {
        this.Conferences = conferences;
    }

    public void setStudents(Student[] students) {
        for (Student student: students) {
            student.removeUntrained();
        }
        this.Students = students;
    }

    public void setBatchesProcessed(int batchesProcessed) {
        this.batchesProcessed = batchesProcessed;
    }

    public void setGpuTimeUsed(int gpuTimeUsed) {
        this.gpuTimeUsed = gpuTimeUsed;
    }

    public void setCpuTimeUsed(int cpuTimeUsed) {
        this.cpuTimeUsed = cpuTimeUsed;
    }
}
