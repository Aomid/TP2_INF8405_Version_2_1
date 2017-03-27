package com.example.abbas.tp2_inf8405_version_2_1;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ParticipateEventActivity extends EventActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Franck", " Instance participate");
    }

    @Override
    protected void initViews(){
        super.initViews();
        findViewById(R.id.layout_participate).setVisibility(View.VISIBLE);
        findViewById(R.id.layout_chose).setVisibility(View.VISIBLE);
    }



    protected void show_Participate_Btns() {
        findViewById(R.id.participate_radio_group).setVisibility(View.VISIBLE);
    }

    /*public void setUpRadioGroup() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        radioGroup.removeAllViews();
        EventPlace ep = meetingEvent.getFinalPlace();
        RadioButton radioButtonView = new RadioButton(this);
        ep.setRadioButton(radioButtonView);
        radioGroup.addView(radioButtonView);
    }*/

    public void onParticipateRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        if (checked) {
            meetingEvent.setParticipation((String) ((RadioButton) view).getText());
        }
    }

    @Override
    protected void passParticipate() {

        show_Participate_Btns();
        //setUpRadioGroup();
        updateMeetingChanges();
        showPlace(meetingEvent.getFinalPlace());
    }

    @Override
    protected void updateMeetingChanges() {
        if (meetingEvent.getFinalPlace().retrieveMarker() == null)
            meetingEvent.getFinalPlace().setMarker(map.addMarker(meetingEvent.provideMarkerFinalPlace()));
        else {
            if (!meetingEvent.getFinalPlace().retrieveMarker().isVisible()) {
                meetingEvent.getFinalPlace().setMarker(map.addMarker(meetingEvent.provideMarkerFinalPlace()));
            }
        }
        super.updateMeetingChanges();
        Date end = meetingEvent.convertEndDate().getTime();
        Date start = meetingEvent.convertStartDate().getTime();
        ((TextView)findViewById(R.id.eventStartingDate)).setText(new SimpleDateFormat("yyyy/MM/dd").format(start));
        ((TextView)findViewById(R.id.eventEndingDate)).setText(new SimpleDateFormat("yyyy/MM/dd").format(end));
        ((TextView)findViewById(R.id.eventStartingTime)).setText(new SimpleDateFormat("HH:mm").format(start));
        ((TextView)findViewById(R.id.eventEndingTime)).setText(new SimpleDateFormat("HH:mm").format(end));
        if(meetingEvent.getParticipations().containsKey(UserProfile.getInstance().emailString)){
            for(View view : findViewById(R.id.participate_radio_group).getTouchables()){
                view.setEnabled(false);
            }
            switch (meetingEvent.getParticipations().get(UserProfile.getInstance().emailString)){
                case "Yes":
                    ((RadioButton) findViewById(R.id.participate)).setChecked(true);
                    break;
                case "No":
                    ((RadioButton) findViewById(R.id.doNotParticipate)).setChecked(true);
                    break;
                case "Maybe":
                    ((RadioButton) findViewById(R.id.mayParticipate)).setChecked(true);
                    break;
            }
        }
    }

    @Override
    protected void showAllPlaces(){

    }

    @Override
    protected void showPlace(MyMarker marker){
        super.showPlace(marker);
        showRating();
        showAverage(current_place_event);
    }

    @Override
    protected void showUser(MyMarker marker){
        super.showUser(marker);
        placeRating.setVisibility(View.GONE);
    }

    @Override
    protected void nextAction() {
        if(meetingEvent.getParticipations().get(UserProfile.getInstance().emailString).equalsIgnoreCase(getResources().getString(R.string.participate))){
            addToCalendar();
        }
        saveMeetingEvent();
    }

    private void addToCalendar() {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, meetingEvent.getStartDateLong())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, meetingEvent.getEndDateLong())
                .putExtra(CalendarContract.Events.TITLE, meetingEvent.getMeetingName())
                .putExtra(CalendarContract.Events.DESCRIPTION, meetingEvent.getDescription())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, meetingEvent.getFinalPlace().getName())
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                //.putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com")
                ;
        startActivity(intent);
    }

}
