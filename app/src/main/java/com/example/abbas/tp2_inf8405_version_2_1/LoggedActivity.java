package com.example.abbas.tp2_inf8405_version_2_1;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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


    protected void initAppbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Enable the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d("Franck", "Add Retrieve Event Request");
    }

    public void updateLocation(Location location) {
        UserProfile.getInstance().setLatitude(location.getLatitude());
        UserProfile.getInstance().setLongitude(location.getLongitude());
        UserProfile.getInstance().setLastUpdate(new SimpleDateFormat("yyyy/MM/dd @ HH:mm:ss").format(new Date()));
        Log.d("Franck","Update_Location @ "+ UserProfile.getInstance().getLastUpdate());
        saveUser();
    }

    public static void saveUser() {
        DatabaseReference addGroup = FirebaseDatabase.getInstance().getReference().child("Users");
        addGroup.child(UserProfile.getInstance().emailString).setValue(UserProfile.getInstance());
        //saveUserInAllGroups();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.appbar_menu_choice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home: {
                NavUtils.navigateUpFromSameTask(this);
                return true;
            }

            case R.id.action_settings: {
                Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_LONG).show();
                showAlertIntervalLocation();
                return true;
            }

            case R.id.action_logout: {
                finish();
                return true;
            }

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void showAlertIntervalLocation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View v  = inflater.inflate(R.layout.set_interval, null);
        ((SeekBar) v.findViewById(R.id.interval_location)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ((TextView) v.findViewById(R.id.interval_value)).setText("Location update interval : " + (seekBar.getProgress()+1) );
            }
        });
        builder.setView(v)
                // Add action buttons
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        GpsTracker.setMinTimeBwUpdates(((SeekBar) v.findViewById(R.id.interval_location)).getProgress()+1);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //builder.getDialog().cancel();
                    }
                }).setTitle("Update Interval");
        builder.create().show();
    }

    /*private static void saveUserInAllGroups() {
        DatabaseReference addGroup = FirebaseDatabase.getInstance().getReference().child("Group");
    }*/
}
