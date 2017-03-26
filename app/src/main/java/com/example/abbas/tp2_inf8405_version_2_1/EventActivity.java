package com.example.abbas.tp2_inf8405_version_2_1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventActivity extends LoggedActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMarkerClickListener{
    protected MeetingEvent meetingEvent = null;
    protected AddPlaceDialog addDialog = null;
    public TextView placeName= null;
    public TextView placeDescription= null;
    public RatingBar placeRating= null;
    public EventPlace current_place_event = null;

    private GoogleMap map;
    private int intervalSelected=1;
    private int interval=1000;

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

    protected void showPlace(Marker marker) {
        if(marker == null){
            Log.d("Franck","Merker null");
        }
        if(marker.getTag()== null){
            Log.d("Franck","Tag null");
        }
        current_place_event = (EventPlace) marker.getTag();
        placeName.setText(current_place_event.getName());
        placeDescription.setText(current_place_event.getDescription());
        showRating();
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

    private void initAppbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Enable the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        //Firebase pour cette partie
        Firebase.setAndroidContext(this);
        retrieveMeetingEvent();
    }

    public void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public void retrieveMeetingEvent() {

        Bundle extras = getIntent().getExtras();
        final String event_Id = extras.getString("Meeting_ID");
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(event_Id);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                removeUserListeners();
                meetingEvent = dataSnapshot.getValue(MeetingEvent.class);
                meetingEvent.setID(event_Id);
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

    private void addUserListeners() {
        for (User user : meetingEvent.getMembers().values()){
            getUserLastLocation(user);
        }
    }

    private void removeUserListeners() {
        DatabaseReference myParentRef = FirebaseDatabase.getInstance().getReference().child("Users");
        for (User user : meetingEvent.getMembers().values()){
            if(user.findEventListener() != null) {
                myParentRef.child(user.emailString).removeEventListener(user.findEventListener());
                user.setValueListener(null);
            }
        }
    }

    protected void updateMeetingChanges() {
        if(map != null) {
            if(meetingEvent.getOrganizer().retrieveMarker() == null)
                meetingEvent.getOrganizer().setMarker(map.addMarker(meetingEvent.provideMarkerFinalPlace()));
            if(meetingEvent.getFinalPlace().retrieveMarker() == null)
                meetingEvent.getFinalPlace().setMarker(map.addMarker(meetingEvent.provideMarkerOrganizer()));

            for (User user : meetingEvent.getMembers().values()){
                if(user.retrieveMarker() == null && user.getLatitude()!= null && user.getLongitude()!=null){
                    user.setMarker(map.addMarker(user.provideMarkerOptions()));
                }
            }
            for (EventPlace ep : meetingEvent.getPlaces().values()) {
                if (ep.retrieveMarker() == null) {
                    ep.setMarker(map.addMarker(ep.provideMarkerOptions()));
                }
            }


           /* EventPlace ep = meetingEvent.getFinalPlace();
            if(ep!=null) {
                if (ep.retrieveMarker() == null) {
                    ep.setMarker(map.addMarker(ep.provideMarkerOptions()));
                }
            }*/
        }else{
            Log.d("Franck", "map null");
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
        intent.putExtra("Meeting_ID", meetingEvent.getID());
        startActivity(intent);
        finish();
    }


    protected void rated() {
        Intent intent = new Intent(getApplicationContext(), Vote_Activity.class);
        intent.putExtra("Meeting_ID", meetingEvent.getID());
        startActivity(intent);
        finish();
    }

    protected void passVoteActivity() {
        Intent intent = new Intent(getApplicationContext(), Vote_Activity.class);
        intent.putExtra("Meeting_ID", meetingEvent.getID());
        startActivity(intent);
        finish();
    }

    public void saveMeetingEvent() {
        Log.d("Franck", "Save");
        Log.d("Franck", meetingEvent.detailsIntoString());

        DatabaseReference addGroup = FirebaseDatabase.getInstance().getReference().child("Groups");
        addGroup.child(meetingEvent.getID()).setValue(meetingEvent);
    }

    public void showDialogPlace(LatLng latlng) {
        addDialog = new AddPlaceDialog();
        EventPlace ep = new EventPlace(latlng.latitude, latlng.longitude);
        addDialog.setPlace(ep);
        Log.d("Franck", meetingEvent.detailsIntoString());
        addDialog.show(getFragmentManager(),"Place");
    }

    public void addPlace(EventPlace place) {
        meetingEvent.addPlace(place);
        place.setMarker(map.addMarker(place.provideMarkerOptions()));
        saveMeetingEvent();
    }



        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(interval);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);


    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(interval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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

    public void setupMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            //map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));

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
            map.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the map.
            if (map != null) {


                map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location arg0) {
                        // TODO Auto-generated method stub

                        map.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                    }
                });

            }
        }
    }


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
        setupMap();
        //45.498838, -73.616995
        map.setMyLocationEnabled(true);
//        googleMap.setMyLocationEnabled(true);
//        Location myLocation = googleMap.getMyLocation();  //Nullpointer exception.........
//        LatLng myLatLng = new LatLng(myLocation.getLatitude(),
//                myLocation.getLongitude());
        LatLng latLng = new LatLng(45.498838, -73.616995);
        map.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Your location"));
        //saveCurrentUserLocation(latitude, longitude);
    }



    //this method updates Latitude and Longitude of users in Database when they login
    public void saveCurrentUserLocation(final double latitude, final double longitude) {
        final DatabaseReference update = FirebaseDatabase.getInstance().getReference("Users");
        update.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String tempUsername;
                int i = (int) dataSnapshot.getChildrenCount();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    dsp.getKey();
                    tempUsername = String.valueOf(dsp.child("emailString").getValue());
                    if (tempUsername.equalsIgnoreCase(UserProfile.getInstance().emailString)) {
                        String temp = update.getKey();
                        dsp.getRef().child("latitude").setValue(latitude);
                        dsp.getRef().child("longitude").setValue(longitude);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EventActivity.this, "There is a error in update", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /** Listeners for the map */

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Toast.makeText(EventActivity.this, "onMarkerClick", Toast.LENGTH_SHORT).show();
        //TODO if marker for Place show place in place meeting else User
        return false;
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
        Toast.makeText(EventActivity.this, "onMarkerDragEnd", Toast.LENGTH_SHORT).show();
        // getting the Co-ordinates
        //  latitude = marker.getPosition().latitude;
        //  longitude = marker.getPosition().longitude;

        //move to current position
        //moveMap();
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
            case R.id.action_settings: {
                setContentView(R.layout.activity_set_interval);

                Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_LONG).show();
                return true;
            }

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
        intent.putExtra("Meeting_ID", meetingEvent.getID());
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



        public void onRadioButtonClicked(View view) {
            // Is the button now checked?
            boolean checked = ((RadioButton) view).isChecked();

            // Check which radio button was clicked
            switch(view.getId()) {
                case R.id.one:
                    if (checked)
                        intervalSelected=1;
                        break;
                case R.id.five:
                    if (checked)
                        intervalSelected=2;
                        break;
                case R.id.ten:
                    if(checked)
                       intervalSelected=3;
                        break;
                case R.id.thirty:
                    if(checked)
                        intervalSelected=4;
                        break;
            }
        }

    public void saveClick(View view) {
        switch (intervalSelected){
            case 1:
                interval=60000;
                Toast.makeText(getApplicationContext(),"Location update interval is One minute",
                        Toast.LENGTH_SHORT).show();
                break;
            case 2:
                interval=300000;
                Toast.makeText(getApplicationContext(),"Location update interval is Five minute",
                        Toast.LENGTH_SHORT).show();
                break;
            case 3:
                interval=600000;
                Toast.makeText(getApplicationContext(),"Location update interval is Ten minute",
                        Toast.LENGTH_SHORT).show();
                break;
            case 4:
                    interval=1800000;
                Toast.makeText(getApplicationContext(),"Location update interval is Thirty minute",
                        Toast.LENGTH_SHORT).show();
                break;

        }

    }

    public void getUserLastLocation(User userm){
        final String name = userm.emailString;
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userm.emailString);
        userm.setValueListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                meetingEvent.changeUser(user);
                Log.d("Franck","User change"+name);
                updateMeetingChanges();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        myRef.addValueEventListener(userm.findEventListener());
    }
}
