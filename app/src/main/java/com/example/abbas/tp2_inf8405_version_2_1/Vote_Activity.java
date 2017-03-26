package com.example.abbas.tp2_inf8405_version_2_1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import static com.example.abbas.tp2_inf8405_version_2_1.MeetingEvent.Code.ELECTING_PLACE;

public class Vote_Activity extends EventActivity {


    public boolean voted  = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Franck", " Instance Vote");
        initialisation();
    }

    @Override
    protected void initViews() {
        super.initViews();
        placeRating.setIsIndicator(false);
        placeRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser){
                    current_place_event.Vote(UserProfile.getInstance().emailString, (long) rating);
                    Toast.makeText(getApplicationContext(),"Vote "+(long) rating,Toast.LENGTH_SHORT ).show();
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(Vote_Activity.this, "onMarkerClick", Toast.LENGTH_SHORT).show();
        showPlace(marker);
        return true;
    }


    protected void  showRating() {
        super.showRating();
        if (!voted) {
            showMyVote(current_place_event);
        } else {
            showAverage(current_place_event);
        }
    }



    private void showMyVote(EventPlace ep) {
        placeRating.setStepSize(1.0f);
        placeRating.setRating(ep.findMyVote());
    }





    protected void alertDialogSaveRatings(){
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.dialog_message_save_ratings)
                .setTitle(R.string.dialog_title_save_ratings);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                placeRating.setIsIndicator(true);
                if (meetingEvent.allrated()) {
                    meetingEvent.setStatus(ELECTING_PLACE);
                }
                saveMeetingEvent();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void chosefinalPlace() {
        if(meetingEvent.amITheOrganizer()) {
            Intent intent =  new Intent(getApplicationContext(), ChoseEventActivity.class);
            intent.putExtra("Meeting_Name", meetingEvent.getMeetingName());
            startActivity(intent);
            finish();
        }else{
            passVoteActivity();
        }
    }


    @Override
    protected void showPlace(Marker marker){
        super.showPlace(marker);
        showRating();
    }

    @Override
    protected void nextAction(){
        if(voted || meetingEvent.rated()) {
            if(!placeRating.isIndicator()) {
                alertDialogSaveRatings();
            } else {
                Toast.makeText(this, "Already saved", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "You need to rate all places", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void rated(){
        voted =true;
    }

    @Override
    protected void passVoteActivity(){
        if(meetingEvent.rated()){
            voted = true;
            Log.d("Franck", "Set Indicator True");
            placeRating.setIsIndicator(true);
        }
    }
}
