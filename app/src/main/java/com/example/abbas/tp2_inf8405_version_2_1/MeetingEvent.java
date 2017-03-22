package com.example.abbas.tp2_inf8405_version_2_1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;

/**
 * Created by Abbas on 3/19/2017.
 */

public class MeetingEvent extends Observable {
    String meetingName;
    String description;
    String encodedPhoto;
    List<EventPlace> Places = new ArrayList<EventPlace>();
    EventPlace FinalPlace = null;
    Calendar date;

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

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
        setChanged();
    }

    public List<EventPlace> getPlaces() {
        return Places;
    }

    public void setPlaces(List<EventPlace> places) {
        Places = places;
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
}


