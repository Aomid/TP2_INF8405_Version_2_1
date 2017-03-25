package com.example.abbas.tp2_inf8405_version_2_1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

/**
 * Created by Abbas on 3/19/2017.
 */


@IgnoreExtraProperties
class MeetingEvent extends Observable implements MyMeetingMarkers{
    private String ID;
    private String meetingName;
    private String description;
    private String encodedPhoto;
    private User organizer;
    private Map<String, EventPlace> places = new HashMap<String, EventPlace>();
    private Map<String, User> members = new HashMap<String, User>();
    private Map<String, String> participations = new HashMap<>();
    private EventPlace FinalPlace = null;
    private Long nbParticipantsMin = 3L, nbParticipantsMax = 3L;
    private Long nbPlacesMin = 3L, nbPlacesMax = 3L;
    private String status = Code.NOT_CREATED;
    private Long startDateLong = null, endDateLong = null;
    private Calendar startDate = null, endDate = null;

    public MeetingEvent (){
    }

    public MeetingEvent(String name){
        meetingName = name;
    }

    public Long getNbParticipantsMin() {
        return nbParticipantsMin;
    }

    public void setNbParticipantsMin(Long nbParticipantsMin) {
        this.nbParticipantsMin = nbParticipantsMin;
    }

    public Long getNbParticipantsMax() {
        return nbParticipantsMax;
    }

    public void setNbParticipantsMax(Long nbParticipantsMax) {
        this.nbParticipantsMax = nbParticipantsMax;
    }

    public Long getNbPlacesMin() {
        return nbPlacesMin;
    }

    public void setNbPlacesMin(Long nbPlacesMin) {
        this.nbPlacesMin = nbPlacesMin;
    }

    public Long getNbPlacesMax() {
        return nbPlacesMax;
    }

    public void setNbPlacesMax(Long nbPlacesMax) {
        this.nbPlacesMax = nbPlacesMax;
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

    public boolean addMember(User instance) {
        if (members.size() == 0)
            organizer = instance;
        if (!members.containsValue(instance) ) {
            if(members.size() == nbParticipantsMax)
                return false;
            else {
                members.put(instance.emailString, instance);
                setChanged();
            }
        }
        return true;

    }

    public void addPlace(EventPlace instance) {
        if (!places.containsValue(instance)) {
            places.put("Place " + (places.size() + 1), instance);
            setChanged();
        }
    }

    public boolean rated(){
        for(EventPlace pe :places.values()){
            if(!pe.getVotes().containsKey(UserProfile.getInstance().emailString)){
                return false;
            }
        }
        return true;
    }

    public boolean allrated(){
        for(EventPlace pe :places.values()){
            if(pe.getVotes().size()!=members.size()){
                return false;
            }
        }
        return true;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String detailsIntoString() {
        String retString = "";
        retString = "Id : " + ID + "\n" +
                "Name : " + meetingName + "\n" +
                "Status : " + status + "\n" +
                "Place Elected : " + (FinalPlace == null ? "null" : FinalPlace.getName()) + "\n" +
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

    public Map<String, String> getParticipations() {
        return participations;
    }

    public void setParticipations(Map<String, String> participations) {
        this.participations = participations;
    }

    public Calendar convertStartDate() {
        if (startDate != null && startDateLong != null) {
            startDate.setTimeInMillis(startDateLong);
        }
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
        updateStart();
    }

    public Calendar convertEndDate() {
        if (endDate != null && endDateLong != null) {
            endDate.setTimeInMillis(endDateLong);
        }
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
        updateEnd();
    }

    private void updateStart() {
        startDateLong = startDate.getTimeInMillis();
    }

    private void updateEnd() {
        endDateLong = endDate.getTimeInMillis();
    }

    public void setStartime(int hh, int min) {
        if (startDate == null)
            startDate = (Calendar) Calendar.getInstance().clone();
        startDate.set(Calendar.HOUR_OF_DAY, hh);
        startDate.set(Calendar.MINUTE, min);
    }

    public void setEndTime(int hh, int min) {
        if (endDate == null)
            endDate = (Calendar) Calendar.getInstance().clone();
        endDate.set(Calendar.HOUR_OF_DAY, hh);
        endDate.set(Calendar.MINUTE, min);
    }

    /*public User getOrganizer() {
        return organizer;
    }*/

    public void setStartDate(int yy, int mm, int dd) {
        if (startDate == null)
            startDate = (Calendar) Calendar.getInstance().clone();
        startDate.set(yy, mm, dd);
    }

    public void setEndDate(int yy, int mm, int dd) {
        if (endDate == null)
            endDate = (Calendar) Calendar.getInstance().clone();
        endDate.set(yy, mm, dd);
    }

    public void setParticipation(String participation) {
        participations.put(UserProfile.getInstance().emailString, participation);
    }

    /* public boolean chosen() {
         return (FinalPlace!=null);
     }
 */
    public boolean amITheOrganizer() {
        Log.d("Franck", "OrganizerMe"+ UserProfile.getInstance().equals(organizer));
        return UserProfile.getInstance().equals(organizer);
    }

    public Long getStartDateLong() {
        return startDateLong;
    }

    public void setStartDateLong(Long startDateLong) {
        this.startDateLong = startDateLong;
    }

    public Long getEndDateLong() {
        return endDateLong;
    }

    public void setEndDateLong(Long endDateLong) {
        this.endDateLong = endDateLong;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public List<MarkerOptions> provideMeetingMarkersOptions() {
        return null;
    }


    class Code {
        public static final String NOT_CREATED = "0L";
        public static final String SETTING_PLACES = "1L";
        public static final String RATING_PLACES = "2L";
        public static final String ELECTING_PLACE = "3L";
        public static final String PARTICIPATION = "4L";
        public static final String END = "5L";
    }


}


