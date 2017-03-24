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
import android.widget.Button;
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

public class Group_Choice_Activity extends AppCompatActivity {

    private String UserName, groupName ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group__choice_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Firebase pour cette partie
        Firebase.setAndroidContext(this);
        Button createGroupe = (Button) findViewById(R.id.create_meeting_button);
        EditText group = (EditText) findViewById(R.id.meetingName);

        ImageView userpic = (ImageView) findViewById(R.id.imageView2);
        TextView Username = (TextView) findViewById(R.id.Username);
        Bundle extras = getIntent().getExtras();
        String value = "";
        String picture = "";

        if (extras != null) {
            value = extras.getString("username");
        }
        Username.setText("Welcome " + value);
        UserName = value;
        picture = extras.getString("picture");
        byte[] decodedByteArray = android.util.Base64.decode(picture, Base64.DEFAULT);
        Bitmap bit = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        userpic.setRotation(-90);
        userpic.setImageBitmap(bit);
    }

    public void groupManager(View view) {
        EditText group = (EditText) findViewById(R.id.meetingName);
        groupName = group.getText().toString();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> tempGroup = new ArrayList<String>();
                String meetingId = null;
                boolean groupExist = false;

                int i = (int) dataSnapshot.getChildrenCount();

                MeetingEvent post = dataSnapshot.getValue(MeetingEvent.class);
                Log.d("Franck", "Nombre " + dataSnapshot.getChildrenCount());

                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    meetingId = dsp.getKey();
                    //tempGroup.add(String.valueOf(dsp.child("groupName").getValue()));
                    if (String.valueOf(dsp.child("meetingName").getValue()).equalsIgnoreCase(groupName)) {
                        groupExist = true;
                        Toast.makeText(getApplicationContext(), "This group is existed.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "You joined it.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if (!groupExist) {
                    meetingId = CreateMeeting(groupName);
                    Toast.makeText(getApplicationContext(), "Create a new group"+meetingId , Toast.LENGTH_SHORT).show();

                }
                if(meetingId != null) {
                    Intent intent = new Intent(getApplicationContext(), Meeting_Setup.class);
                    intent.putExtra("Meeting_ID", meetingId);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public String CreateMeeting(String name){
        MeetingEvent meetingEvent = new MeetingEvent(name);
        meetingEvent.addMember(UserProfile.getInstance());
        DatabaseReference addGroup=FirebaseDatabase.getInstance().getReference().child("Groups");
        String ID=addGroup.push().getKey();
        addGroup.child(ID).setValue(meetingEvent);
        return ID;
    }

}
