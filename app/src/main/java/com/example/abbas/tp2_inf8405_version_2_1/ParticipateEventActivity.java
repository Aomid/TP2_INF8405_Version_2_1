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
        setContentView(R.layout.activity_chose_event);
        initialisation();
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
    }

    @Override
    protected void nextAction() {
        saveMeetingEvent();
    }

}