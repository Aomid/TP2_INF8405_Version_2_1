package com.example.abbas.tp2_inf8405_version_2_1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 * Created by Abbas on 3/19/2017.
 */

@IgnoreExtraProperties
class MeetingEvent extends Observable {
    private String ID;
    private String meetingName;
    private String description;
    private String encodedPhoto;
    private User organizer;
    private Map<String, EventPlace> places = new HashMap<String, EventPlace>();
    private Map<String, User> members = new HashMap<String, User>();
    private EventPlace FinalPlace = null;
    private Calendar date;


    public MeetingEvent (){
    }

    public MeetingEvent(String name){
        meetingName = name;
    }

    @JsonIgnore
    public void setDecodedPhoto(Bitmap image){
        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.WEBP, 100, bYtE); //On pourrait essayer d'autre CompressFormat si jamais le décodage marche pas.
        //image.recycle();
        byte[] byteArray = bYtE.toByteArray();
        String imageFile = com.firebase.client.utilities.Base64.encodeBytes(byteArray);
        encodedPhoto = imageFile;
        setChanged();
    }
    @JsonIgnore
    public Bitmap getGetDecodedImage(){
        try {
            byte[] decodedByte = com.firebase.client.utilities.Base64.decode(encodedPhoto);
            Bitmap bitmap= BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            return bitmap;
        } catch(Exception e) {
            return null;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        setChanged();
    }

    public String getEncodedPhoto() {
        return encodedPhoto;
    }

    public void setEncodedPhoto(String encodedPhoto) {
        this.encodedPhoto = encodedPhoto;
    }

    public Map<String, EventPlace> getPlaces() {
        return places;
    }

    public void setPlaces(Map<String, EventPlace> places) {
        this.places = places;
        setChanged();
    }

    public EventPlace getFinalPlace() {
        return FinalPlace;
    }

    public void setFinalPlace(EventPlace finalPlace) {
        FinalPlace = finalPlace;
        setChanged();
    }

    public void ConfirmEvent(){

    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
        setChanged();
    }

    public String print() {
        return "Meeting Name = "+ meetingName;
    }

    public void addMember(User instance) {
        if (members.size() == 0)
            organizer = instance;
        if (!members.containsValue(instance)) {
            members.put(instance.emailString, instance);
            setChanged();
        }

    }

    public void addPlace(EventPlace instance) {
        if (!places.containsValue(instance)) {
            places.put("Place " + (places.size() + 1), instance);
            setChanged();
        }
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDetails() {
        String retString = "";
        retString = "Id : " + ID + "\n" +
                "Name : " + meetingName + "\n" +
                "";
        int index = 1;
        for (EventPlace ep : places.values())
            retString = retString +
                    "Place " + (index++) + " : " + ep.getName() + "(" + ep.getLatitude() + "," + ep.getLongitude() + ")\n";
        index = 1;
        for (User user : members.values())
            retString = retString +
                    "Member " + (index++) + " : " + user.emailString + "\n";
        return retString;
    }


    public Map<String, User> getMembers() {
        return members;
    }

    public void setMembers(Map<String, User> members) {
        this.members = members;
        setChanged();
    }


}


