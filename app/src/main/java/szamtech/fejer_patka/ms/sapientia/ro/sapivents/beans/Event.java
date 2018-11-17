package szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Model class for the Events
 * Not complete yet, so far it's made only for the SharedPreference data source
 * Implements Serializable so this class can be parse to Json
 * Implements Comparable an array of this class type can be sorted
 * TODO: update the model to its final version (when Firebase is integrated)
 */
public class Event implements Comparable<Event>, Serializable {
    private int id;
    //Placeholder image
    //TODO: replace this
    private String image="https://cdn.pixabay.com/photo/2013/04/06/11/50/image-editing-101040_960_720.jpg";
    private String description;
    private String name="";
    //TODO: add author which is a User object
    // private User author;

    public Event(int id,String name, String image, String description) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.description = description;
    }

    public Event(int id,String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull Event o) {
        return this.getName().compareTo(o.getName());
    }
}
