package com.example.abbas.tp2_inf8405_version_2_1;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Abbas on 3/19/2017.
 */

public class EventPlace implements MyMarker {
    private Map<String, Long> Votes = new HashMap<>();
    private String id;
    private String description;
    private String icon;
    private String name;
    private String vicinity;// Address
    private Double latitude;
    private Double longitude;
    private Marker marker = null;

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
                .snippet(description)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                //.icon(BitmapDescriptorFactory.fromBitmap(ImageConverter.decodeIntoBitmap(icon)))
                ;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
        marker.setTag(this);
    }

    public String getDescription() {
        return description;
    }

    public int findMyVote() {
        if(Votes.containsKey(UserProfile.getInstance().emailString))
            return Votes.get(UserProfile.getInstance().emailString).intValue();
        else
            return 0;
    }
}
