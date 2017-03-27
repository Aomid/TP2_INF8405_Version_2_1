package com.example.abbas.tp2_inf8405_version_2_1;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;

public class EventActivity extends LoggedActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMarkerClickListener{
    protected MeetingEvent meetingEvent = null;
    public TextView placeName= null;
    public TextView placeDescription= null;
    public RatingBar placeRating= null;
    public EventPlace current_place_event = null;
    protected GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void initialisation(){
        setContentView();
        initViews();
        initAppbar();
        initLocalisation();
        initMap();
        initDatabase();
    }

    protected void initViews() {
        if(placeName== null)
            placeName = (TextView) findViewById(R.id.place_selected_name);
        if(placeDescription== null)
            placeDescription = (TextView) findViewById(R.id.place_selected_desc);
        if(placeRating== null) {
            placeRating = (RatingBar) findViewById(R.id.place_selected_rating);
        }
    }

    protected void showPlace(MyMarker marker) {
        if(marker == null){
            Log.d("Franck","Place null");
        }
        current_place_event = (EventPlace) marker;
        placeName.setText(current_place_event.getName());
        placeDescription.setText(current_place_event.getDescription());
    }

    protected void showRating() {
        placeRating.setVisibility(View.VISIBLE);
        placeRating.setMax(5);
    }

    protected void showAverage(EventPlace ep) {
        placeRating.setStepSize(0.1f);
        placeRating.setRating(ep.average());
    }


    protected void setContentView() {
        setContentView(R.layout.meeting_global_layout);
    }


    @Override
    protected void initAppbar(){
        super.initAppbar();
        Bundle extras = getIntent().getExtras();
        final String Meeting_Name = extras.getString("Meeting_Name");
        getSupportActionBar().setSubtitle(Meeting_Name);
    }

    private void initLocalisation() {
        /*GpsTracker gps = new GpsTracker(this);
        if (gps.canGetLocation) {
            UserProfile.getInstance().setLatitude(gps.getLatitude());
            UserProfile.getInstance().setLongitude(gps.getLongitude());
            //Toast.makeText(this, "Longitude = " + (String.valueOf(longitude) + " Latitude= " + (String.valueOf(latitude))), Toast.LENGTH_LONG).show();
        }*/
    }



    private void initDatabase() {
        retrieveMeetingEvent();
    }

    public void initMap(){
        Log.d("Franck", "Init Map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public void retrieveMeetingEvent() {
        Log.d("Franck", "Add Retrieve Event Request");
        Bundle extras = getIntent().getExtras();
        final String Meeting_Name = extras.getString("Meeting_Name");
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(Meeting_Name);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                removeMarkers();
                removeUserListeners();
                meetingEvent = dataSnapshot.getValue(MeetingEvent.class);
                meetingEvent.setMeetingName(Meeting_Name);
                meetingEvent.linkParams();
                Log.d("Franck", "Retrieve");
                Log.d("Franck", meetingEvent.detailsIntoString());
                changeActivity();
                addUserListeners();
                updateMeetingChanges();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void removeMarkers() {
        if (meetingEvent != null) {
            removeMarkers(meetingEvent.getPlaces().values());
            removeMarkers(meetingEvent.getMembers().values());
        }
    }

   private void removeMarkers(Collection<? extends MyMarker> markers ){
       for (MyMarker marker : markers){
           if(marker.retrieveMarker() != null ) {
                marker.retrieveMarker().remove();
           }
           marker.setMarker(null);
       }
   }

    private void addUserListeners() {
        Log.d("Franck", "add Listeners");
        for (User user : meetingEvent.getMembers().values()){
            getUserLastLocation(user);
        }
    }

    private void removeUserListeners() {
        Log.d("Franck", "Remove Listeners");
        DatabaseReference myParentRef = FirebaseDatabase.getInstance().getReference().child("Users");
        if(meetingEvent != null){
            for (User user : meetingEvent.getMembers().values()){
                if(user.findEventListener() != null) {
                    myParentRef.child(user.emailString).removeEventListener(user.findEventListener());
                    user.setValueListener(null);
                }
            }
        }
    }

    protected void updateMeetingChanges() {
        Log.d("Franck", "Update Changes");
        if(map != null) {
            if(meetingEvent.getOrganizer().retrieveMarker() == null)
                meetingEvent.getOrganizer().setMarker(map.addMarker(meetingEvent.provideMarkerOrganizer()));
            else {
                if (!meetingEvent.getOrganizer().retrieveMarker().isVisible())
                    meetingEvent.getOrganizer().setMarker(map.addMarker(meetingEvent.provideMarkerOrganizer()));
            }
            showAllUsers();
            showAllPlaces();
        }else{
            Log.d("Franck", "map null");
        }
    }

    protected void showAllPlaces() {
        findViewById(R.id.layout_all_places).setVisibility(View.VISIBLE);
        showList((LinearLayout) findViewById(R.id.layout_all_places_contents), meetingEvent.getPlaces().values());
    }

    protected void showAllUsers(){
        showList((LinearLayout) findViewById(R.id.layout_all_users_contents), meetingEvent.getMembers().values());
    }


    protected void showList(LinearLayout ll,Collection< ? extends MyMarker> markers){
        ll.removeAllViews();
        ll.setVisibility(View.VISIBLE);
        LayoutInflater li = LayoutInflater.from(this);
        for (MyMarker ep : markers) {
            LinearLayout child = (LinearLayout) li.inflate(R.layout.mini_place_layout, null);
            child.setTag(ep);
            showInList(child,ep);
            if(ep.retrieveMarker() == null && ep.isLocationAvailable()){
                ep.setMarker(map.addMarker(ep.provideMarkerOptions()));
            }
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMarker((MyMarker) v.getTag());
                }
            });
            ll.addView(child);
        }
    }

    protected void showInList(LinearLayout child, MyMarker ep) {
        //ImageConverter.decodeInto(ep.getIcon(), ((ImageView) child.findViewById(R.id.mini_icon)));
        Log.d("Franck",ep.getName() );
        ((TextView)child.findViewById(R.id.mini_place_name)).setText(ep.getName());
        ((TextView) child.findViewById(R.id.mini_description)).setText(ep.getDescription());
        //ImageConverter.decodeInto(ep.getIcon(), ((ImageView) child.findViewById(R.id.mini_icon));
    }

    protected void showMarker(MyMarker marker) {
        if(marker.retrieveMarker()!= null){
            map.moveCamera(CameraUpdateFactory.newLatLng(marker.retrieveMarker().getPosition()));
            marker.retrieveMarker().showInfoWindow();
        }
        switch (marker.getMarkerType()){
            case USER: {
                showUser(marker);
                break;
            }
            case PLACE: {
                showPlace(marker);
                break;
            }
        }
    }

    protected void changeActivity() {
        switch (meetingEvent.getStatus()) {
            case MeetingEvent.Code.NOT_CREATED:
                return;
            case MeetingEvent.Code.SETTING_PLACES:
                return;
            case MeetingEvent.Code.RATING_PLACES:
                passVoteActivity();
                return;
            case MeetingEvent.Code.ELECTING_PLACE:
                chosefinalPlace();
                return;
            case MeetingEvent.Code.PARTICIPATION:
                passParticipate();
                return;
            case MeetingEvent.Code.END:
                return;
        }
    }
        /*protected void changeActivity() {
            if (meetingEvent.getFinalPlace() != null) {
                passParticipate();
                return;
            }
            if (meetingEvent.allrated()) {
                // Show the final Place
                chosefinalPlace();
                return;
            }
            if (meetingEvent.rated()) {
                rated();
                return;
            }
            if (meetingEvent.getPlaces().size() > 2) {
                // Make Votes

                return;
            }
        }
    }*/

    protected void chosefinalPlace() {
        Intent intent = null ;
        if(meetingEvent.amITheOrganizer())
            intent = new Intent(getApplicationContext(), ChoseEventActivity.class);
        else
            intent = new Intent(getApplicationContext(), Vote_Activity.class);
        intent.putExtra("Meeting_Name", meetingEvent.getMeetingName());
        startActivity(intent);
        finish();
    }


    protected void rated() {
        Intent intent = new Intent(getApplicationContext(), Vote_Activity.class);
        intent.putExtra("Meeting_Name", meetingEvent.getMeetingName());
        startActivity(intent);
        finish();
    }

    protected void passVoteActivity() {
        Intent intent = new Intent(getApplicationContext(), Vote_Activity.class);
        intent.putExtra("Meeting_Name", meetingEvent.getMeetingName());
        startActivity(intent);
        finish();
    }

    public void saveMeetingEvent() {
        Log.d("Franck", "Save");
        Log.d("Franck", meetingEvent.detailsIntoString());
        DatabaseReference addGroup = FirebaseDatabase.getInstance().getReference().child("Groups");
        addGroup.child(meetingEvent.getMeetingName()).setValue(meetingEvent);
    }



   /* private void moveMap() {
        /**
         * Creating the latlng object to store lat, long coordinates
         * adding marker to map
         * move the camera with animation
         */
   /*
        int i, j;
        i = (int) latitude;
        j = (int) longitude;
        LatLng latLng = new LatLng(longitude, latitude);
        map.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Marker in India"));

        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
        map.getUiSettings().setZoomControlsEnabled(true);


    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLongClickListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnMarkerDragListener(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(UserProfile.getInstance().isLocationAvailable())
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(UserProfile.getInstance().getLatitude(),UserProfile.getInstance().getLatitude())));
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
    }


    /** Listeners for the map */
    @Override
    public boolean onMarkerClick(Marker marker) {
        //Toast.makeText(EventActivity.this, "onMarkerClick", Toast.LENGTH_SHORT).show();
        //TODO if marker for Place show place in place meeting else User
        showMarker((MyMarker) marker.getTag());
        return false;
    }

    protected void showUser(MyMarker marker) {
        User user = (User) marker;
        placeName.setText(user.getEmailString());
        placeDescription.setText("");
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //map.addMarker(new MarkerOptions().position(latLng).draggable(true));
        //showDialogPlace(latLng);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Toast.makeText(EventActivity.this, "onMarkerDragStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        //Toast.makeText(EventActivity.this, "onMarkerDrag", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //Toast.makeText(EventActivity.this, "onMarkerDragEnd", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.appbargroup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_ok:
                nextAction();
                return true;

            case R.id.action_quit_group:
                quit_group();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    protected void quit_group() {
        DatabaseReference delete=FirebaseDatabase.getInstance().getReference().child("Groups");
        /*
        meetingEvent.removeMember(UserProfile.getInstance());
        saveMeeting();
        finish();*/
        delete.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp : dataSnapshot.getChildren())
                {
                    dsp.getKey();
                    if(dsp.child("meetingName").getValue().toString().equalsIgnoreCase(meetingEvent.getMeetingName()))
                    {
                        dsp.child("members").child(UserProfile.getInstance().emailString).getRef().removeValue();
                        Toast.makeText(getApplicationContext(),"You leave this group",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    protected void nextAction() {
    }

    protected void passParticipate() {
        Log.d("Franck", "Must participate");
        Intent intent = new Intent(getApplicationContext(), ParticipateEventActivity.class);
        intent.putExtra("Meeting_Name", meetingEvent.getMeetingName());
        startActivity(intent);
        finish();
    }


    public void takePhoto(View view) {
        Toast.makeText(getApplicationContext(),"Camera test",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // f = Uri.fromFile(getMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT,true);
        startActivity(intent);
        // startActivityForResult(intent, 1);
    }

    public void getUserLastLocation(User userm){
        final String name = userm.emailString;
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userm.emailString);
        userm.setValueListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                meetingEvent.changeUser(user);
                Log.d("Franck","User change : "+user.emailString);
                updateMeetingChanges();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        myRef.addValueEventListener(userm.findEventListener());
    }
}
