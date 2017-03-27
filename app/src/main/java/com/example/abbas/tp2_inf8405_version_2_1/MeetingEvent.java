package com.example.abbas.tp2_inf8405_version_2_1;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 * Created by Abbas on 3/19/2017.
 */


@IgnoreExtraProperties
class MeetingEvent extends Observable {

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
    @Exclude
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

    @Exclude
    public void setDecodedPhoto(Bitmap image){
        encodedPhoto= ImageConverter.encodeBitmap(image);
        /*ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.WEBP, 100, bYtE); //On pourrait essayer d'autre CompressFormat si jamais le décodage marche pas.
        //image.recycle();
        byte[] byteArray = bYtE.toByteArray();
        String imageFile = com.firebase.client.utilities.Base64.encodeBytes(byteArray);
        encodedPhoto = imageFile;*/
        setChanged();
    }

    @Exclude
    public Bitmap getGetDecodedImage(){

        try {
            return ImageConverter.decodeIntoBitmap(encodedPhoto);
            /*byte[] decodedByte = com.firebase.client.utilities.Base64.decode(encodedPhoto);
            Bitmap bitmap= BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            return bitmap;*/
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

    public boolean addPlace(EventPlace instance) {
        if (!places.containsValue(instance)) {
            places.put(instance.getName(), instance);
            setChanged();
            return true;
        }return false;
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

    public String detailsIntoString() {
        String retString = "";
        retString =
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
        if(startDate == null){
            startDate = Calendar.getInstance();
        }
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
        if(endDate == null){
            endDate = Calendar.getInstance();
        }
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
        if(startDate != null)
            startDateLong = startDate.getTimeInMillis();
    }

    private void updateEnd() {
        if(endDate != null)
            endDateLong = endDate.getTimeInMillis();
    }

    public void setStartime(int hh, int min) {
        if (startDate == null)
            startDate = (Calendar) Calendar.getInstance().clone();
        startDate.set(Calendar.HOUR_OF_DAY, hh);
        startDate.set(Calendar.MINUTE, min);
    }

    public void setEndTime(int hh, int min) {
        if (endDate == null){
            endDate = (Calendar) Calendar.getInstance().clone();
        }
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
        if(startDateLong == null)
            updateStart();
        return startDateLong;
    }

    public void setStartDateLong(Long startDateLong) {
        this.startDateLong = startDateLong;
    }

    public Long getEndDateLong() {
        if(endDateLong == null)
            updateEnd();
        return endDateLong;
    }

    public void setEndDateLong(Long endDateLong) {
        this.endDateLong = endDateLong;
    }


    public void concat(MeetingEvent savedMeeting){
        //The members in the Database are the most accurate only if they don't impact me
        if(members.containsKey(UserProfile.getInstance().emailString)) {
            savedMeeting.addMember(UserProfile.getInstance());
        }
        else {
            savedMeeting.removeMember(UserProfile.getInstance());
        }
        members = savedMeeting.getMembers();
        //The places must be from the organizer
        // Be careful of votes
        if(!amITheOrganizer()){
            for( String ep_key : savedMeeting.getPlaces().keySet()){
                EventPlace myep = null;
                if( (myep = places.get(ep_key)) != null) {
                    if (myep.getVotes().get(UserProfile.getInstance().emailString) != null) {
                        savedMeeting.getPlaces().get(ep_key).getVotes().put(UserProfile.getInstance().emailString, myep.getVotes().get(UserProfile.getInstance().emailString));
                    }
                }
            }
            places = savedMeeting.getPlaces();
            FinalPlace = savedMeeting.FinalPlace ;
            nbParticipantsMin = savedMeeting.nbParticipantsMin ;
            nbParticipantsMax = savedMeeting.nbParticipantsMax ;
            nbPlacesMin = savedMeeting.nbPlacesMin ;
            nbPlacesMax= savedMeeting.nbPlacesMax ;
            //status = savedMeeting.status ;
            startDateLong = savedMeeting.startDateLong ;
            endDateLong = savedMeeting.endDateLong;
            description = savedMeeting.description;
            encodedPhoto = savedMeeting.encodedPhoto;
        }
        meetingName =savedMeeting.meetingName;
        Log.d("Franck", "L'autre "+ savedMeeting.meetingName);
        Log.d("Franck", "Le mien "+ meetingName);
        if(status.compareTo(savedMeeting.status) < 0){
            status = savedMeeting.status;
        }
    }

    public void removeMember(User user){
        if(user.equals(organizer)){
            status= Code.REMOVE;
        }
        members.remove(user.emailString);
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


    public MarkerOptions provideMarkerFinalPlace() {
        return new MarkerOptions()
                .position(new LatLng(FinalPlace.getLatitude(),FinalPlace.getLongitude()))
                //  .draggable(true)
                .title(FinalPlace.getName())
                .snippet(description)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                //.icon(BitmapDescriptorFactory.fromBitmap(ImageConverter.decodeIntoBitmap(icon)))
            ;
    }

    public MarkerOptions provideMarkerOrganizer() {
        if(organizer.getLongitude() == null || organizer.getLatitude() == null)
            return new MarkerOptions().position(new LatLng(0,0)).visible(false);
        return new MarkerOptions()
                .position(new LatLng(organizer.getLatitude(),organizer.getLongitude()))
                //  .draggable(true)
                .title(organizer.emailString)
                .snippet(description)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                //.icon(BitmapDescriptorFactory.fromBitmap(ImageConverter.decodeIntoBitmap(icon)))
                ;
    }

    //Met a jour les donnees sur l'utlisateur
    public void changeUser(User user) {
        members.put(user.emailString,user);
        if(user.equals(organizer)){
            organizer = user;
        }
    }

    public void linkParams() {
        if(organizer != null)
            organizer = members.get(organizer.emailString);
        if(FinalPlace != null)
            FinalPlace = places.get(FinalPlace.getName());
    }

    public String getStart(){
        if(startDate == null)
            return null;
        return new SimpleDateFormat("yyyy/MM/dd @ HH:mm:ss").format(startDate.getTime());
    }

    public String getEnd(){
        if(endDate == null)
            return null;
        return new SimpleDateFormat("yyyy/MM/dd @ HH:mm:ss").format(endDate.getTime());
    }

    class Code {
        public static final String NOT_CREATED = "0L";
        public static final String SETTING_PLACES = "1L";
        public static final String RATING_PLACES = "2L";
        public static final String ELECTING_PLACE = "3L";
        public static final String PARTICIPATION = "4L";
        public static final String END = "5L";
        public static final String REMOVE = "6L";
    }
}


