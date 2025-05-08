package com.example.imessify.models;

// Renamed to avoid conflict with Kotlin version
public class ContactJava {
    private long id;
    private String name;
    private String phone;
    private int profilePicture;

    public ContactJava() {
        // Empty constructor
    }

    public ContactJava(long id, String name, String phone, int profilePicture) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.profilePicture = profilePicture;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(int profilePicture) {
        this.profilePicture = profilePicture;
    }
}
