package com.example.abbas.tp2_inf8405_version_2_1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
    }


    protected void show_Participate_Btns() {
        findViewById(R.id.participate_radio_group).setVisibility(View.VISIBLE);
    }

    public void setUpRadioGroup() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        radioGroup.removeAllViews();
        EventPlace ep = meetingEvent.getFinalPlace();
        RadioButton radioButtonView = new RadioButton(this);
        ep.setRadioButton(radioButtonView);
        radioGroup.addView(radioButtonView);
    }

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
        setUpRadioGroup();
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
        if(meetingEvent.getParticipations().get(UserProfile.getInstance()).equalsIgnoreCase(getResources().getString(R.string.participate))){
            addToCalendar();
        }
        saveMeetingEvent();
    }

    private void addToCalendar() {
        /*Calendar beginTime = Calendar.getInstance();
        beginTime.set(2012, 0, 19, 7, 30);
        Calendar endTime = Calendar.getInstance();
        endTime.set(2012, 0, 19, 8, 30);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(Events.TITLE, "Yoga")
                .putExtra(Events.DESCRIPTION, "Group class")
                .putExtra(Events.EVENT_LOCATION, "The gym")
                .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
                .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");
        startActivity(intent);*/
    }

}
