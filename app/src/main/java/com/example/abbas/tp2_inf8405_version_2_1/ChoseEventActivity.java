package com.example.abbas.tp2_inf8405_version_2_1;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.example.abbas.tp2_inf8405_version_2_1.MeetingEvent.Code.PARTICIPATION;

public class ChoseEventActivity extends EventActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Franck", " Instance Chose Event");
        initialisation();
        //setDateTime();
    }


    @Override
    protected void initViews(){
        super.initViews();
        findViewById(R.id.layout_chose).setVisibility(View.VISIBLE);
    }


    private void setDateTime() {
        Calendar rightNow = meetingEvent.convertStartDate();
        if (rightNow == null) {
            rightNow = Calendar.getInstance();
        }
        setStartDate(rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH), rightNow.get(Calendar.DAY_OF_MONTH));
        setStartTime(rightNow.get(Calendar.HOUR_OF_DAY), rightNow.get(Calendar.MINUTE));
        rightNow = meetingEvent.convertEndDate();
        if (rightNow == null) {
            rightNow = Calendar.getInstance();
            rightNow.add(Calendar.HOUR_OF_DAY, 2);
        }
        setEndDate(rightNow.get(Calendar.YEAR), rightNow.get(Calendar.MONTH), +rightNow.get(Calendar.DAY_OF_MONTH));
        setEndTime(rightNow.get(Calendar.HOUR_OF_DAY), rightNow.get(Calendar.MINUTE));
    }


    public void showEndDatePickerDialog(View v) {
        DialogFragment newFragment = new MyDateTimePicker.DatePickerFragment();
        MyDateTimePicker.setStart(false);
        newFragment.show(getSupportFragmentManager(), "End of the Event (Date)");
    }

    public void setEndDate(int yy, int mm, int dd) {
        meetingEvent.setEndDate(yy, mm, dd);
        ((TextView) findViewById(R.id.eventEndingDate)).setText(yy + "/" + (mm + 1) + "/" + dd);
    }

    public void showStartDatePickerDialog(View v) {
        DialogFragment newFragment = new MyDateTimePicker.DatePickerFragment();
        MyDateTimePicker.setStart(true);
        newFragment.show(getSupportFragmentManager(), "Beginning of the Event (Date)");
    }

    public void setStartDate(int yy, int mm, int dd) {
        meetingEvent.setStartDate(yy, mm, dd);
        ((TextView) findViewById(R.id.eventStartingDate)).setText(yy + "/" + (mm + 1) + "/" + dd);
    }

    public void showEndTimePickerDialog(View v) {
        DialogFragment newFragment = new MyDateTimePicker.TimePickerFragment();
        MyDateTimePicker.setStart(false);
        newFragment.show(getSupportFragmentManager(), "End of the Event (Time)");
    }

    public void setEndTime(int hh, int min) {
        meetingEvent.setEndTime(hh, min);
        ((TextView) findViewById(R.id.eventEndingTime)).setText(hh + "h" + min);
    }

    public void showStartTimePickerDialog(View v) {
        DialogFragment newFragment = new MyDateTimePicker.TimePickerFragment();
        MyDateTimePicker.setStart(true);
        newFragment.show(getSupportFragmentManager(), "Beginning of the Event (Time)");
    }


    public void setStartTime(int hh, int min) {
        meetingEvent.setStartime(hh, min);
        ((TextView) findViewById(R.id.eventStartingTime)).setText(hh + "h" + min);
    }

    public void setUpRadioGroup() {
        //LayoutInflater inflater = getLayoutInflater();
        //LinearLayout layout = (LinearLayout) findViewById(R.id.group_radio_layout);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        radioGroup.removeAllViews();
        List<EventPlace> list = new ArrayList<>(meetingEvent.getPlaces().values());
        Collections.sort(list);
        for (EventPlace ep : list) {
            RadioButton radioButtonView = new RadioButton(this);
            radioButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRadioButtonClicked(v);
                }
            });
            ep.setRadioButton(radioButtonView);
            radioGroup.addView(radioButtonView);
        }
        //layout.addView(radioGroup);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        if (checked) {
            meetingEvent.setFinalPlace((EventPlace) view.getTag());
        }
    }

    @Override
    protected void nextAction() {
        Log.d("Franck", " Save db");
        //TODO Verifier qu'il y ait bien les dates
        meetingEvent.setStatus(PARTICIPATION);
        saveMeetingEvent();
        passParticipate();
    }

    @Override
    protected void chosefinalPlace() {
        setUpRadioGroup();
        setDateTime();
    }
}
