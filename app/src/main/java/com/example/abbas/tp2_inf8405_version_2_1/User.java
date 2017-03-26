package com.example.abbas.tp2_inf8405_version_2_1;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Abbas on 3/13/2017.
 */

public class User implements  MyMarker{
    // Identifiant de l'utilisateur
    public String emailString;
    public String passString;
    public String profileImage;
    @Exclude
    public Marker marker;
    @Exclude
    public ValueEventListener valueListener;

    public void setEmailString(String emailString) {
        this.emailString = emailString;
    }

    public void setPassString(String passString) {
        this.passString = passString;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String lastUpdate = null;
    public Double latitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double longitude;

    public User() {}

   /* public User(String emailString, String profileImage)
    {
        this.emailString=emailString;
        this.profileImage=profileImage;
        this.passString=null;
    }*/

    public User(String emailString,String passString, String profileImage)
    {
        this.emailString=emailString;
        this.passString=passString;
        this.profileImage=profileImage;
    }

    public String getEmailString() {
        return emailString;
    }

    public String getPassString() {
        return passString;
    }

    public String getProfileImage() {
        return profileImage;
    }



    @Override
    public boolean equals(Object o){
        boolean retVal = false;
        if (o instanceof User){
            User another = (User) o;
            retVal = another.emailString.compareToIgnoreCase(emailString) == 0;
        }
        return retVal;
    }

    @Override
    public MarkerOptions provideMarkerOptions() {
         return new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                //  .draggable(true)
                .title(emailString)
                //.snippet(lastUpdate)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                //.icon(BitmapDescriptorFactory.fromBitmap(ImageConverter.decodeIntoBitmap(icon)))
                ;
    }

    @Override
    public Marker retrieveMarker() {
        return marker;
    }

    @Override
    public void setMarker(Marker marker) {
        if(this.marker != null){
            marker.remove();
        }
        this.marker=marker;
        marker.setTag(this);
    }

    public void setValueListener(ValueEventListener valueListener) {
        this.valueListener = valueListener;
    }

    public ValueEventListener findEventListener() {
        return valueListener;
    }
}
