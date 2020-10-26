package com.mwongela.randomizer;

public class Merry {
    private String randomNumber, displayName, profilePhoto, time, date;

    Merry(String randomNumber, String profilePhoto, String displayName, String time, String date) {
        this.randomNumber = randomNumber;
        this.profilePhoto = profilePhoto;
        this.displayName = displayName;
        this.time = time;
        this.date = date;
    }

    public Merry() {

    }

    public void setRandomNumber(String randomNumber) {
        this.randomNumber = randomNumber;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRandomNumber() {
        return randomNumber;
    }

    public String getDisplayName() {
        return displayName;
    }


    public String getProfilePhoto() {
        return profilePhoto;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

}
