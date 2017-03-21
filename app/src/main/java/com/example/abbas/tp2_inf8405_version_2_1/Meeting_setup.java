package com.example.abbas.tp2_inf8405_version_2_1;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.annotations.NotNull;
import com.firebase.client.realtime.Connection;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Meeting_setup extends FragmentActivity
        implements
        OnMapReadyCallback,
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapLongClickListener,
GoogleMap.OnMarkerDragListener,
GoogleMap.OnMarkerClickListener,
        Observer {
//8888888888888888888888888
private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private GoogleApiClient googleApiClient;
    //8888888888888888888888
 //   private static final String TAG = "";
    private static final float DEFAULT_ZOOM = 2;
    private static String groupName;
    final int SELECT_PHOTO = 1;
    MeetingEvent eventBeingModified = null;
    Button createMeetingButton, settingsButton;
    EditText meetingName;
    LocationRequest mLocationRequest;
    ListView scheduledMeetingsList;
    GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Location mLastKnownLocation;
    Marker mCurrLocationMarker;
    boolean mLocationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        setContentView(R.layout.activity_meeting_setup);

        if (googleServiceAvailable()) {
            Toast.makeText(this, "Google service is ok!!!", Toast.LENGTH_LONG).show();
            //  initMap();
        }
        //Firebase pour cette partie
        Firebase.setAndroidContext(this);

        Button createGroupe = (Button) findViewById(R.id.create_meeting_button);
        EditText group = (EditText) findViewById(R.id.meetingName);

        ImageView userpic = (ImageView) findViewById(R.id.imageView2);
        TextView Username = (TextView) findViewById(R.id.Username);
        Bundle extras = getIntent().getExtras();
        String value = "";
        String picture = "";

        if (extras != null) {
            value = extras.getString("username");
        }
        Username.setText("Welcome " + value);
        picture = extras.getString("picture");
        byte[] decodedByteArray = android.util.Base64.decode(picture, Base64.DEFAULT);
        Bitmap bit = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        userpic.setRotation(-90);
        userpic.setImageBitmap(bit);

    }

    //888888888888888888888888888
    private void getCurrentLocation() {
        mMap.clear();
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
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            //moving the map to location
            moveMap();
        }
    }
    private void moveMap() {
        /**
         * Creating the latlng object to store lat, long coordinates
         * adding marker to map
         * move the camera with animation
         */
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Marker in India"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setZoomControlsEnabled(true);


    }
//888888888888888888888888888888
    private void initMap() {

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

//Search meeting button onClick event

    public void groupManager(View view) {
        EditText group = (EditText) findViewById(R.id.meetingName);
        groupName = group.getText().toString();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Groups");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> tempGroup = new ArrayList<String>();
                boolean groupExist = false;

                int i = (int) dataSnapshot.getChildrenCount();

                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    dsp.getKey();
                    tempGroup.add(String.valueOf(dsp.child("groupName").getValue()));
                }
                for (String str : tempGroup) {
                    if (str.equalsIgnoreCase(groupName)) {
                        groupExist = true;
                        Toast.makeText(getApplicationContext(), "This group is existed.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "You joined it.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if (!groupExist) {
                    Toast.makeText(getApplicationContext(), "Create a new group", Toast.LENGTH_SHORT).show();
                    // CreateMeeting();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

/*    public void CreateMeeting()
    {
        ShowUserPositionsOnMap();
    }
    // Show the users positions on the map
    void ShowUserPositionsOnMap() {
  //      map.clear();
 //       LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        /*Group currentGroup = DataManager.getInstance().getCurrentGroup();
        List<UserProfile> groupMembers = currentGroup.getGroupMembers();
        if (groupMembers != null) {
            for (UserProfile u : groupMembers) {
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(u.getLatitude(), u.getLongitude()))
                        .title(u.getUsername())
                        .snippet(u.getUsername());
                map.addMarker(markerOptions);
            }

            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            for (UserProfile u : groupMembers) {
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(u.getLatitude(), u.getLongitude()))
                        .title(u.getUsername())
                        .snippet(u.getUsername());
                map.addMarker(markerOptions);
                boundsBuilder.include(new LatLng(u.getLatitude(), u.getLongitude()));
            }
            */
    // Bounds the map around the users positions
 /*           try {
                LatLngBounds bounds = boundsBuilder.build();
                int padding = 250;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                map.animateCamera(cu);
            }
            catch (Exception e){
                Log.d("DEBUG", e.toString() + "!");
            }

        }
*/
//77777777777777777777777777777777777
@Override
public void onMarkerDragStart(Marker marker) {
    Toast.makeText(Meeting_setup.this, "onMarkerDragStart", Toast.LENGTH_SHORT).show();
}

    @Override
    public void onMarkerDrag(Marker marker) {
        Toast.makeText(Meeting_setup.this, "onMarkerDrag", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // getting the Co-ordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //move to current position
        moveMap();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(Meeting_setup.this, "onMarkerClick", Toast.LENGTH_SHORT).show();
        return true;
    }
    @Override
    public void onMapLongClick(LatLng latLng) {
        // mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
    }
    //77777777777777777777777777777777777777
    @Override
    public void onConnected(Bundle connectionHint){//@NotNull Bundle bundle) {

       getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
       map = googleMap;


    }


    public void update(Observable o, Object arg) {

    }

}
    // Returns a string containing the places types that at least one group member liked
 /*   private String GetPlacesPreferences(){
        ArrayList<String> preferences = new ArrayList<>();
        Group currentGroup = (Group) MainActivity.getInstance().getCurrentGroup();
        List<UserProfile> groupMembers = currentGroup.getGroupMembers();
        for (UserProfile u : groupMembers) {
            for(String pref : u.getPreferences()){
                String lowerCasePref = pref.toLowerCase();
                if(!preferences.contains(lowerCasePref))
                {
                    preferences.add(lowerCasePref);
                }
            }
        }
        StringBuilder prefStr = new StringBuilder();
        for(int i = 0; i < preferences.size(); ++i){
            prefStr.append(preferences.get(i));
            if(i<preferences.size()-1)
                prefStr.append('|');
        }
        return prefStr.toString();
    }
*/


