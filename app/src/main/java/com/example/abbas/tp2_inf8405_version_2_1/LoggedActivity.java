package com.example.abbas.tp2_inf8405_version_2_1;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Franck","create");
        if(GpsTracker.getInstance()== null){
            startService(new Intent(this, GpsTracker.class));
        }
        GpsTracker.setCurrentActivity(this);
        GpsTracker.getInstance();
    }

    public void updateLocation(Location location) {
        Log.d("Franck","Update_Location");
        UserProfile.getInstance().setLatitude(location.getLatitude());
        UserProfile.getInstance().setLongitude(location.getLongitude());
        UserProfile.getInstance().setLastUpdate(new SimpleDateFormat("yyyy/MM/dd @ HH:mm:ss").format(new Date()));
        saveUser();
    }

    public static void saveUser() {
        DatabaseReference addGroup = FirebaseDatabase.getInstance().getReference().child("Users");
        addGroup.child(UserProfile.getInstance().emailString).setValue(UserProfile.getInstance());
        //saveUserInAllGroups();
    }

    /*private static void saveUserInAllGroups() {
        DatabaseReference addGroup = FirebaseDatabase.getInstance().getReference().child("Group");
    }*/
}
