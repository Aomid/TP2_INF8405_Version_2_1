package com.example.abbas.tp2_inf8405_version_2_1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Group_Choice_Activity extends LoggedActivity {

    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group__choice_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Firebase pour cette partie
        Firebase.setAndroidContext(this);

        ImageView userpic = (ImageView) findViewById(R.id.imageView2);
        TextView Username = (TextView) findViewById(R.id.Username);
        Username.setText("Welcome " + UserProfile.getInstance().emailString);
        byte[] decodedByteArray = android.util.Base64.decode(UserProfile.getInstance().profileImage, Base64.DEFAULT);
        Bitmap bit = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        userpic.setRotation(-90);
        userpic.setImageBitmap(bit);
    }

    public void groupManager(View view) {
        final EditText group = (EditText) findViewById(R.id.meetingName);
        groupName = group.getText().toString();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MeetingEvent me ;
                if(dataSnapshot.hasChild(groupName)){
                    me = dataSnapshot.child(groupName).getValue(MeetingEvent.class);
                    if(me.addMember(UserProfile.getInstance())) {
                        DatabaseReference addGroup = FirebaseDatabase.getInstance().getReference().child("Groups");
                        addGroup.child(groupName).setValue(me);
                        Toast.makeText(getApplicationContext(), "This group exists.", Toast.LENGTH_SHORT).show();
                        JoinGroup(me);
                    }else{
                        // Group full
                        me = null;
                    }
                }else{
                    me= CreateMeeting(groupName);
                    Toast.makeText(getApplicationContext(), "Create a new group "+ groupName , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public MeetingEvent CreateMeeting(String name){
        MeetingEvent meetingEvent = new MeetingEvent(name);
        meetingEvent.setID(name);
        meetingEvent.addMember(UserProfile.getInstance());
        FirebaseDatabase.getInstance().getReference().child("Groups").child(name).setValue(meetingEvent);
        return meetingEvent;
    }

    public void JoinGroup(MeetingEvent me){
        if(me != null) {
            Intent intent = null;
            switch (me.getStatus()) {
                case MeetingEvent.Code.NOT_CREATED:
                    intent = new Intent(getApplicationContext(), Meeting_Setup.class);
                    break;
                case MeetingEvent.Code.SETTING_PLACES:
                    intent = new Intent(getApplicationContext(), Meeting_Setup.class);
                    break;
                case MeetingEvent.Code.RATING_PLACES:
                    intent = new Intent(getApplicationContext(), Vote_Activity.class);
                    break;
                case MeetingEvent.Code.ELECTING_PLACE:
                    if(me.amITheOrganizer()) {
                        intent = new Intent(getApplicationContext(), ChoseEventActivity.class);
                    }else{
                        intent = new Intent(getApplicationContext(), Vote_Activity.class);
                    }
                    break;
                case MeetingEvent.Code.PARTICIPATION:
                    intent = new Intent(getApplicationContext(), ParticipateEventActivity.class);
                    break;
                case MeetingEvent.Code.END:
                    intent = new Intent(getApplicationContext(), ParticipateEventActivity.class);
                    break;
            }
            if (intent != null) {
                intent.putExtra("Meeting_ID", me.getID());
                startActivity(intent);
                finish();
            }
        }
    }

}
