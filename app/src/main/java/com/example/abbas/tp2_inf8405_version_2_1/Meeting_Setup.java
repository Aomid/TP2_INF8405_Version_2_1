package com.example.abbas.tp2_inf8405_version_2_1;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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

import java.util.Observable;
import java.util.Observer;

public class Meeting_Setup extends EventActivity
        implements
        Observer {
    //8888888888888888888888888
    private static final String TAG = "Meeting Setup";
    //8888888888888888888888
    //   private static final String TAG = "";
    private static final float DEFAULT_ZOOM = 2;
    private static String groupName;
    final int SELECT_PHOTO = 1;
    MeetingEvent eventBeingModified = null;
    Button createMeetingButton, settingsButton;
    EditText meetingName;

    ListView scheduledMeetingsList;
    Location mLastLocation;
    Location mLastKnownLocation;
    Marker mCurrLocationMarker;
    boolean mLocationPermissionGranted;

    private double longitude;
    private double latitude;
    private GoogleApiClient googleApiClient;
    //    GoogleMap map;

    private String UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_setup);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        initialisation();
        ImageView userpic = (ImageView) findViewById(R.id.imageView2);
        TextView Username = (TextView) findViewById(R.id.Username);
        //Bundle extras = getIntent().getExtras();
       /* String value = "";
        String picture = "";

        if (extras != null) {
            value = extras.getString("username");
        }
        Username.setText("Welcome " + value);
        UserName=value;
        picture = extras.getString("picture");
        byte[] decodedByteArray = android.util.Base64.decode(picture, Base64.DEFAULT);
        Bitmap bit = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        userpic.setRotation(-90);
        userpic.setImageBitmap(bit);*/

    }

    public void update(Observable o, Object arg) {

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






}