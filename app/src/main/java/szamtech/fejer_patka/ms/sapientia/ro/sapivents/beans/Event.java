package szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Removed setId()
 * Id is automatically set when a new object is created
 *
 * Model class for the Events
 * Not complete yet, so far it's made only for the SharedPreference data source
 * Implements Serializable so this class can be parse to Json
 * Implements Comparable an array of this class type can be sorted
 */
public class Event implements Comparable<Event>, Serializable {

    private int id;
    private String title;
    private String description;
    private String eventDate;
    private String location;
    private ArrayList<String> images = new ArrayList<>();
    private String author;
    private ArrayList<String> attendants;
    private boolean published;
    private String key; //this is for identifying in database


    public Event(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Event(String title, String description, String date, String location, ArrayList<String> images, String authorId, ArrayList<String> attendants, boolean published) {
        this.title = title;
        this.description = description;
        this.eventDate = date;
        this.location = location;
        this.images = images;
        this.author = authorId;
        this.attendants = attendants;
        this.published = published;
        this.attendants.add("");
    }

    public Event(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String date) {
        this.eventDate = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public void clearImages(){
        this.images = new ArrayList<>();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String authorId) {
        this.author = authorId;
    }

    public ArrayList<String> getAttendants() {
        return attendants;
    }

    public void setAttendants(ArrayList<String> attendants) {
        this.attendants = attendants;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public void addNewImageURL(String imageUrl){
        images.add(imageUrl);
    }

    public void setKey(String key){ this.key = key; }

    public String getKey(){ return this.key; }

    @Override
    public int compareTo(@NonNull Event o) {
        return getTitle().compareTo(o.getTitle());
    }
}
