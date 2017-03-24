package com.example.abbas.tp2_inf8405_version_2_1;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
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

public class EventActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private MeetingEvent meetingEvent = null;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation,mLastKnownLocation;
    LocationRequest mLocationRequest;
    private GoogleMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void initialisation(){
        initLocalisation();
        initApiCLient();
        initDatabase();
        initMap();
    }

    private void initLocalisation() {
        GpsTracker gps = new GpsTracker(this);
        if (gps.canGetLocation) {
            UserProfile.getInstance().setLatitude(gps.getLatitude());
            UserProfile.getInstance().setLongitude(gps.getLongitude());
            //Toast.makeText(this, "Longitude = " + (String.valueOf(longitude) + " Latitude= " + (String.valueOf(latitude))), Toast.LENGTH_LONG).show();
        }
    }

    private void initApiCLient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

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
                meetingEvent = dataSnapshot.getValue(MeetingEvent.class);
                meetingEvent.setID(event_Id);
                Log.d("Franck", "Retrieve");
                Log.d("Franck", meetingEvent.getDetails());
                if (meetingEvent.getPlaces().size() > 2) {
                    //passVoteActivity();
                }
                for (EventPlace ep : meetingEvent.getPlaces().values()) {
                    if (ep.retreiveMarker() == null) {
                        ep.setMarker(map.addMarker(ep.provideMarkerOptions()));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void passVoteActivity() {
        Intent intent = new Intent(getApplicationContext(), Vote_Activity.class);
        intent.putExtra("Meeting_ID", meetingEvent.getID());
        startActivity(intent);
    }

    public void saveMeetingEvent() {
        Log.d("Franck", "Save");
        Log.d("Franck", meetingEvent.getDetails());
        DatabaseReference addGroup = FirebaseDatabase.getInstance().getReference().child("Groups");
        addGroup.child(meetingEvent.getID()).setValue(meetingEvent);
    }

    public void showDialogPlace(LatLng latlng) {
        AddPlaceDialog dialog = new AddPlaceDialog();
        EventPlace ep = new EventPlace(latlng.latitude, latlng.longitude);
        dialog.setPlace(ep);
        Log.d("Franck", meetingEvent.getDetails());
        dialog.show(getFragmentManager(),"Place");
    }

    public void addPlace(EventPlace place) {
        meetingEvent.addPlace(place);
        place.setMarker(map.addMarker(place.provideMarkerOptions()));
        saveMeetingEvent();
    }

    @Override
    public void onConnected(Bundle connectionHint) {//@NotNull Bundle bundle) {

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

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            String x, y;
            x = (String.valueOf(mLastLocation.getLatitude()));
            y = (String.valueOf(mLastLocation.getLongitude()));
            Toast.makeText(getApplicationContext(), "Location= " + x + "  " + y, Toast.LENGTH_LONG).show();
        } else setupMap();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    //check for google service avalability

    public boolean googleServiceAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Can not connect to google play service.", Toast.LENGTH_LONG).show();
        }
        return false;
    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }



    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

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
        map.setOnMapLongClickListener(new EventActivity.MapListener());
        map.setOnMarkerClickListener(new EventActivity.MapListener());
        map.setOnMarkerDragListener(new EventActivity.MapListener());
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

    private class MapListener implements GoogleMap.OnMapLongClickListener,
            GoogleMap.OnMarkerDragListener,
            GoogleMap.OnMarkerClickListener{
        @Override
        public boolean onMarkerClick(Marker marker) {
            Toast.makeText(EventActivity.this, "onMarkerClick", Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        public void onMapLongClick(LatLng latLng) {
            //map.addMarker(new MarkerOptions().position(latLng).draggable(true));
            showDialogPlace(latLng);
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

    }
}
