package szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans;

import android.app.Dialog;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable, Comparable<User> {
    //Temporary. only while using shared preferences
    private static int sId;

    private int id;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private List<String> eventAthored;
    private List<String> eventAttendance;

    //For Shared Preferences
    public User(String firstName, String lastName) {
        this.id = id;
        ++sId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = "";
        eventAthored = new ArrayList<>();
        eventAttendance = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfileImage() {
        return profilePicture;
    }

    public void setProfileImage(String profileImage) {
        this.profilePicture = profileImage;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", profileImage='" + profilePicture + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull User o) {
        return (this.getFirstName().compareTo(o.getFirstName()));
    }
}
