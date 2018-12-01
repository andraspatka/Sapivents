package szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans;

import android.app.Dialog;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable, Comparable<User> {
    //Temporary. only while using shared preferences
    private static int sId;

    private int id;
    private String phone;
    private String firstName;
    private String lastName;
    private String profileImage;
    private ArrayList<String> eventIds;

    //For Shared Preferences
    public User(String phone, String firstName, String lastName, String profileImage) {
        this.id = id;
        ++sId;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileImage = profileImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", firstName='" + firstName + '\'' +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull User o) {
        return (this.getFirstName().compareTo(o.getFirstName()));
    }
}
