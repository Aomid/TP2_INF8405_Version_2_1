package com.example.abbas.tp2_inf8405_version_2_1;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ChoseEventActivity extends EventActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chose_event);
        initialisation();
    }


    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new MyDateTimePicker.DatePickerFragment();
        MyDateTimePicker.setView(v);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new MyDateTimePicker.TimePickerFragment();
        MyDateTimePicker.setView(v);
        newFragment.show( getSupportFragmentManager(), "timePicker");
    }

    public void setDatePicker( int yy, int mm, int dd){
        ((TextView)MyDateTimePicker.getView()).setText(yy+"/"+(mm+1)+"/"+dd);
    }

    public void setTimePicker(int hh, int min){
        ((TextView)MyDateTimePicker.getView()).setText(hh+"h"+min);
    }

   /* public void setUpRadioGroup(){
        LayoutInflater inflater = LayoutInflater.from(this);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        for (int k = 0; k < meetingEvent.getPlaces().size(); k++) {
            View radioButtonView = inflater.inflate(R.layout.check_radio_button, null);
            RadioButton radioButton = (RadioButton) radioButtonView
                    .findViewById(R.id.radio_button);
            radioButton.setText(answerValues.get(k));
            radioGroup.addView(radioButtonView);
        }
    }*/
}
