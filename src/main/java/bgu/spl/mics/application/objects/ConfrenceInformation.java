package bgu.spl.mics.application.objects;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Queue;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private ArrayList<Model> publications;
    private boolean hasPublishConference = false;

    public ConfrenceInformation(String name, int date){
        this.date = date;
        this.name = name;
        publications = new ArrayList<Model>();
    }

    public int getDate() {
        return date;
    }

    public void addPublication(Model model){
        if(publications == null){
            publications = new ArrayList<Model>();
        }
        publications.add(model);
    }
    public ArrayList<Model> getPublications(){
        if(publications == null){
            return new ArrayList<Model>();
        }
        return publications;
    }
    public Boolean hasPublished(){
        return hasPublishConference;
    }
    public void publish(){
        hasPublishConference = true;
    }
}
