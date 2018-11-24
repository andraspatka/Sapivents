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
 * TODO: update the model to its final version (when Firebase is integrated)
 */
public class Event implements Comparable<Event>, Serializable {
    //Static id field, gets incremented every time a new object is created
    //This ensures that each object has a unique identifier
    private static int sId;

    private int id;
    private String title;
    private String description;
    private DateTime date;
    private String location;
    private ArrayList<String> images = new ArrayList<>();
    private String authorId;
    private ArrayList<String> attendants;
    private boolean published;


    public Event(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Event(String title, String description, DateTime date, String location, ArrayList<String> images, String authorId, ArrayList<String> attendants, boolean published) {
        this.id = sId;
        ++sId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.images = images;
        this.authorId = authorId;
        this.attendants = attendants;
        this.published = published;
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

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
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

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
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

    @Override
    public int compareTo(@NonNull Event o) {
        return getTitle().compareTo(o.getTitle());
    }
}
