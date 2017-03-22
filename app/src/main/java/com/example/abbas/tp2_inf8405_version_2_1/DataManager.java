package com.example.abbas.tp2_inf8405_version_2_1;

/**
 * Created by Abbas on 3/8/2017.
 */

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import com.firebase.client.Firebase;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;


import java.lang.reflect.MalformedParameterizedTypeException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataManager {
    private static DataManager instance = null;
    private Firebase firebaseRef;
    private Group currentGroup; //our java class
    private UserProfile currentUser; //our java class
    private List<Group> groupList;

    private DataManager() {
        firebaseRef = new Firebase("https://inf8405-tp2-b3b53.firebaseio.com/");
        currentGroup = new Group();
        currentUser = new UserProfile();
        groupList = new ArrayList<Group>();


/*        firebaseRef.child("username").addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                List<Group> newList = new ArrayList<Group>();

                //pour chaque Username dans le DB
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //on Excract le Group
                    Group tempGroup = ds.getValue(Group.class);
                    //pour enregistrer tous les groups
                    newList.add(tempGroup);
                    //if the current group concerned has changed, we update it's content
                    if (tempGroup.getGroupName().equalsIgnoreCase(currentGroup.getGroupName())) {
                        currentGroup.setGroupMembsers(tempGroup.getGroupMembers());

                        //for each meeting in the "NEW" version of the currentGroup , we add or update it to the currentGroup
                        for (MeetingEvent me : tempGroup.getGroupEvents()) {
                            currentGroup.addorUpdateEvent(me);
                        }
                    }
                }
                //we empty the list and replace it with the newest version
                groupList.clear();
                groupList.addAll(newList);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("Error _Group", firebaseError.getMessage());
            }
        });

    }


    public static DataManager getInstance() {
           if(instance==null) instance=new DataManager();
        return instance;
    }

    public void createGroup(String groupName)
    {
        Group group=new Group(groupName);
        this.currentGroup=group;
        firebaseRef.child("Groups").child(groupName).setValue(group);
    }

    public Gruop getGroup(String groupName)
    {
        if(groupName.equalsIgnoreCase(currentGroup.getGroupName()))
            return currentGroup;

        for(Group g:groupList)
        {
            if(g.getGroupName().equalsIgnoreCase(groupName))
                return g;
        }
        //this name is not present in the database
        return null;
    }
    public UserProfile getUser(String username)
    {
        if(currentGroup.getGroupMembers()!=null)
        {
            for(UserProfile up: currentGroup.getGroupMembers())
            {
                if(up.getUserName().equalsIgnoreCase(username))
                    return  up;
            }
        }
        return  null;
    }

    public void addorUpdateUser(UserProfile user)
    {
        currentGroup.addorUpdateGroupMember(user);

        //sync with DB
        firebaseRef.child("group").child(currentGroup.getGroupName()).setValue(currentGroup);

    }

    public void addorUpdateEvent()
*/
    }
}


