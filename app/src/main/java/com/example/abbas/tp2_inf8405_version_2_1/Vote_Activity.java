package com.example.abbas.tp2_inf8405_version_2_1;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import static com.example.abbas.tp2_inf8405_version_2_1.MeetingEvent.Code.ELECTING_PLACE;

public class Vote_Activity extends EventActivity {

    public TextView placeName= null;
    public TextView placeDescription= null;
    public RatingBar placeRating= null;
    public EventPlace current_place_event = null;
    public boolean voted  = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialisation();
        setViews();
    }

    private void setViews() {
        if(placeName== null)
            placeName = (TextView) findViewById(R.id.place_selected_name);
        if(placeDescription== null)
            placeDescription = (TextView) findViewById(R.id.place_selected_desc);
        if(placeRating== null) {
            placeRating = (RatingBar) findViewById(R.id.place_selected_rating);
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
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(Vote_Activity.this, "onMarkerClick", Toast.LENGTH_SHORT).show();
        showPlace(marker);
        return true;
    }

    private void showAverage(EventPlace ep) {
        placeRating.setRating(ep.average());
    }

    private void showMyVote(EventPlace ep) {
        placeRating.setRating(ep.findMyVote());
    }

    private void showPlace(Marker marker) {
        current_place_event = (EventPlace) marker.getTag();
        placeName.setText(current_place_event.getName());
        placeDescription.setText(current_place_event.getDescription());
        placeRating.setVisibility(View.VISIBLE);
        placeRating.setMax(5);
        if(!voted){
            showMyVote(current_place_event);
        }else{
            showAverage(current_place_event);
        }
        Log.d("Franck","Num stars"+placeRating.getNumStars());
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
    protected void passVoteActivity(){}
}
