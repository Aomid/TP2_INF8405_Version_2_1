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
import java.util.Collections;
import java.util.List;

import static com.example.abbas.tp2_inf8405_version_2_1.MeetingEvent.Code.PARTICIPATION;

public class ChoseEventActivity extends EventActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Franck", " Instance Chose Event");
    }


    @Override
    protected void initViews(){
        super.initViews();
        //Mettre la vue avec les radio buttons visible
        findViewById(R.id.layout_chose).setVisibility(View.VISIBLE);
    }


    //Mettre la valeur d'aujourd'hui dans les textViews s'il n'y a pas encore eu de choix sur les dates et heure
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




    @Override
    protected void showPlace(MyMarker marker){
        super.showPlace(marker);
        showRating();
    }



     /*********************************************************************
     Afficher les dialog pour choisir les dates et heure de début et de fin
      *********************************************************************/
    public void showEndDatePickerDialog(View v) {
        DialogFragment newFragment = new MyDateTimePicker.DatePickerFragment();
        MyDateTimePicker.setStart(false);
        newFragment.show(getSupportFragmentManager(), "End of the Event (Date)");
    }

    public void showStartDatePickerDialog(View v) {
        DialogFragment newFragment = new MyDateTimePicker.DatePickerFragment();
        MyDateTimePicker.setStart(true);
        newFragment.show(getSupportFragmentManager(), "Beginning of the Event (Date)");
    }

    public void showEndTimePickerDialog(View v) {
        DialogFragment newFragment = new MyDateTimePicker.TimePickerFragment();
        MyDateTimePicker.setStart(false);
        newFragment.show(getSupportFragmentManager(), "End of the Event (Time)");
    }

    public void showStartTimePickerDialog(View v) {
        DialogFragment newFragment = new MyDateTimePicker.TimePickerFragment();
        MyDateTimePicker.setStart(true);
        newFragment.show(getSupportFragmentManager(), "Beginning of the Event (Time)");
    }


    /*********************************************************************
     Mettre les dates et heures choisies dans l'événement/Groupe et les afficher
     *********************************************************************/

    public void setStartDate(int yy, int mm, int dd) {
        meetingEvent.setStartDate(yy, mm, dd);
        ((TextView) findViewById(R.id.eventStartingDate)).setText(yy + "/" + (mm + 1) + "/" + dd);
    }

    public void setEndTime(int hh, int min) {
        meetingEvent.setEndTime(hh, min);
        ((TextView) findViewById(R.id.eventEndingTime)).setText(hh + "h" + min);
    }


    public void setStartTime(int hh, int min) {
        meetingEvent.setStartime(hh, min);
        ((TextView) findViewById(R.id.eventStartingTime)).setText(hh + "h" + min);
    }

    public void setEndDate(int yy, int mm, int dd) {
        meetingEvent.setEndDate(yy, mm, dd);
        ((TextView) findViewById(R.id.eventEndingDate)).setText(yy + "/" + (mm + 1) + "/" + dd);
    }


    //Mettre la liste des lieux dans un Radio Group pour pouvoir les selectionner
    public void setUpRadioGroup() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        radioGroup.removeAllViews();
        List<EventPlace> list = new ArrayList<>(meetingEvent.getPlaces().values());
        Collections.sort(list);
        for (EventPlace ep : list) {
            RadioButton radioButtonView = new RadioButton(this);
            radioButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Mettre le selectionner comme lieu Final
                    onRadioButtonClicked(v);
                }
            });
            ep.setRadioButton(radioButtonView);
            radioGroup.addView(radioButtonView);
        }
    }

    // Mettre le selectionner comme lieu Final
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        if (checked) {
            meetingEvent.setFinalPlace((EventPlace) view.getTag());
        }
    }

    @Override
    // Lorsque l'on appuie sur Valider dans la barre d'application
    protected void nextAction() {
        Log.d("Franck", " Save db");
        //TODO Verifier qu'il y ait bien les dates
        meetingEvent.setStatus(PARTICIPATION);
        saveMeetingEvent();
        passParticipate();
    }

    @Override
    //Ne pas afficher la liste des lieux (déja présente dans le Radio Group)
    protected void showAllPlaces(){
    }

    @Override
    // Doit être redéfinit car sinon lance cette activité
    // Est appelé après un retrieveEvent
    protected void chosefinalPlace() {
        setUpRadioGroup();
        setDateTime();
    }
}
