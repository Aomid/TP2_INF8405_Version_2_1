package com.example.abbas.tp2_inf8405_version_2_1;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.Exclude;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Abbas on 3/19/2017.
 */

public class EventPlace implements MyMarker, Comparable<EventPlace> {
    public View view;
    private Map<String, Long> Votes = new HashMap<>();
    private String id;
    private String description;
    private String icon;
    private String name;
    private String vicinity;// Address
    private Double latitude;
    private Double longitude;
    @Exclude
    MarkerType markerType = MarkerType.PLACE;
    @Exclude
    private Marker marker = null;
    @Exclude
    private LinearLayout placeView = null;

    public EventPlace() {

    }

    public EventPlace(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public EventPlace(Marker marker) {
        this.marker = marker;
        this.latitude = marker.getPosition().latitude;
        this.longitude = marker.getPosition().longitude;
    }

    // Create an EventPlace from a Json object
    static EventPlace jsonToPlaceReference(JSONObject placeReference) {
        try {
            EventPlace result = new EventPlace();
            JSONObject geometry = (JSONObject) placeReference.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");
            result.setLatitude((Double) location.get("lat"));
            result.setLongitude((Double) location.get("lng"));
            result.setIcon(placeReference.getString("icon"));
            result.setName(placeReference.getString("name"));
            result.setVicinity(placeReference.getString("vicinity"));
            result.setId(placeReference.getString("id"));
            return result;
        } catch (JSONException ex) {
            Logger.getLogger(EventPlace.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Marker retrieveMarker() {
        return marker;
    }

    // Returns true if a user has voted for this place
    public boolean hasVoted(String username){
        return Votes.containsKey(username);
    }

    // Returns the number of users who voted for this place
    public int GetVoteCount(){return Votes.size();}

    // Adds user to the list of user who voted for this place
    public void Vote(String username, Long vote) {
        if (Votes.containsKey(username)) {
            return;
        }
        Votes.put(username, vote);
    }

    // Removes a user from the list of users who voted for this place
    public void UnVote(String username){
        if (!Votes.containsKey(username)) {
            return;
        }
        Votes.remove(username);
    }

    public Map<String, Long> getVotes() {
        return Votes;
    }

    public void setVotes(Map<String, Long> votes) {
        Votes = votes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    // Creates a JSON string from an Event place object
    @Override
    public String toString() {
        return "Place{" + "id=" + id + ", icon=" + icon + ", name=" + name + ", latitude=" + latitude + ", longitude=" + longitude + '}';
    }

    @Override
    public MarkerOptions provideMarkerOptions() {
        return new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                //  .draggable(true)
                .title(name)
                //.snippet(description)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                //.icon(BitmapDescriptorFactory.fromBitmap(ImageConverter.decodeIntoBitmap(icon)))
                ;
    }

    public void setMarker(Marker marker) {
        if(this.marker != null){
            marker.remove();
        }
        this.marker = marker;
        marker.setTag(this);
    }

    public void setMarkerType(MarkerType markerType) {
        this.markerType = markerType;
    }

    @Override
    public MarkerType getMarkerType() {
        return markerType;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int findMyVote() {
        if(Votes.containsKey(UserProfile.getInstance().emailString))
            return Votes.get(UserProfile.getInstance().emailString).intValue();
        else
            return 0;
    }

    public void setView(View view) {
        this.view = view;
        ((TextView) view.findViewById(R.id.place_selected_name)).setText(name);
        ((TextView) view.findViewById(R.id.place_selected_desc)).setText(description);
        ((RatingBar) view.findViewById(R.id.place_selected_rating)).setNumStars(3);
    }

    public void setRadioButton(RadioButton radioButton) {
        radioButton.setTag(this);
        radioButton.setText(name + "  (" + average() + "/5)");
    }

    public float average() {
        float res = 0;
        for (Long rating : Votes.values()) {
            res += rating;
        }
        res /= Votes.values().size();
       // Log.d("Franck","avg :"+ res);
        return res;
    }

    @Override
    public boolean equals(Object o) {
        boolean retVal = false;
        if (o instanceof EventPlace) {
            EventPlace another = (EventPlace) o;
            retVal = another.getName().compareToIgnoreCase(name) == 0;
        }
        return retVal;
    }

    @Override
    public int compareTo(@NonNull EventPlace o) {
        return (int) (average() - o.average())*100;
    }


    /*public LinearLayout placeView(){
        if(placeView==null)
        return new LinearLayout();
    }*/
}
