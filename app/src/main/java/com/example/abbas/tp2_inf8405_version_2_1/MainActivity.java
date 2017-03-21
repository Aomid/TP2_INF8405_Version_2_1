package com.example.abbas.tp2_inf8405_version_2_1;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.client.*;
import com.firebase.client.core.Context;
import com.firebase.client.core.Tag;
import com.google.firebase.database.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firebase_core.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static android.R.attr.bitmap;
import static android.R.attr.key;
import static android.R.attr.singleUser;
import static android.R.attr.value;

import android.graphics.Bitmap;

public class MainActivity extends AppCompatActivity {

    private Uri f;
    private ImageButton imagebutton;
    private static  MainActivity instance=null;
    private static String emaildefault = null;
    private static boolean userPassOK=false;
    private static EditText email;
    private static EditText password;
    private static Boolean userExist=false;

    private Bitmap bitmap;
    private static String profileImageOnString;
    private Button loginButton, signupButton, setProfilePicButton;
    private ImageView profilePicture;
    private LinearLayout signinOptions;

    DatabaseReference myRef=FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.pass);
        loginButton = (Button) findViewById(R.id.login_button);
        signupButton = (Button) findViewById(R.id.signup_button);
        setProfilePicButton = (Button) findViewById(R.id.set_profile_picture_button);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
//create a Database instance
        Firebase.setAndroidContext(this);


        loginButton.setEnabled(false);
        signupButton.setEnabled(false);
        setProfilePicButton.setEnabled(false);


        //profilePicture.setVisibility(View.GONE);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (email.length() > 2 && password.length() > 2) {
                    loginButton.setEnabled(true);
                    signupButton.setEnabled(true);
                    setProfilePicButton.setEnabled(true);
                } else {
                    loginButton.setEnabled(false);
                    signupButton.setEnabled(false);
                    setProfilePicButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                emaildefault = email.getText().toString();
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (password.length() > 2 && email.length() > 2) {
                    loginButton.setEnabled(true);
                    signupButton.setEnabled(true);
                    setProfilePicButton.setEnabled(true);
                } else {
                    loginButton.setEnabled(false);
                    signupButton.setEnabled(false);
                    setProfilePicButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Login();
            }
        });



        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                   Signup();
            }
        });

        //in method vase dokme set profile picture mibashad, baz kardane Camera
        //va seda kardane method zakhire pic
        setProfilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Camera is started", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                f = Uri.fromFile(getMediaFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, f);
                startActivityForResult(intent, 1);
            }
        });
    }

    private static File getMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "INF8405");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (email.getText() != null) {
            return new File(mediaStorageDir.getPath() + File.separator + email.getText() + ".jpg");
        } else
            return new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent imageReturned) {
        super.onActivityResult(requestCode, resultCode, imageReturned);
        String path = f.toString();
        Bundle extras=imageReturned.getExtras();
        bitmap=(Bitmap)extras.get("imageReturned");

        ImageView image = (ImageView) findViewById(R.id.profilePicture);
        image.setImageBitmap(BitmapFactory.decodeFile(path));
        image.setImageURI(f);
        image.setRotation(-90);
    }
    private void Login()
    {

        //Toast.makeText(getApplicationContext(),"Login method test!", Toast.LENGTH_SHORT).show();
        String emailString=email.getText().toString();
        String passString =password.getText().toString();
        checkUserExistance("login");
    }


    private void Signup()
    {
        String emailString=email.getText().toString();
        String passString =password.getText().toString();
        //Toast.makeText(getApplicationContext(),"Signup clicked!!! "+emailString+"  "+ groupString, Toast.LENGTH_LONG).show();
        checkUserExistance("signup");
/*
        DatabaseReference userRef=myRef.child("username");

        //userRef.setValue(email.getText().toString());

       //to change user profile photo to a binary file
        BitmapDrawable drawable = (BitmapDrawable) profilePicture.getDrawable();
        Bitmap image = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        String imageBinaryFile = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        //to save into the database _append

        checkUserExistance(false);
        Toast.makeText(getApplicationContext(),(String.valueOf(userExist)), Toast.LENGTH_SHORT).show();
  */
      //  if(userExistCheck) {
       //     myRef.child("username").push().setValue(emailString);
        //    myRef.child("group").push().setValue(groupString);
         //   myRef.child("profileImage").push().setValue(imageBinaryFile);
        //}

    }

    public void checkUserExistance(final String callByWhichMethod)
    {

        final String emailString=email.getText().toString();
        final String passString=password.getText().toString();
        DatabaseReference readMethod= FirebaseDatabase.getInstance().getReference("Users");
       // DatabaseReference myref=readMethod.child("Users").child("username");

        readMethod.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<String> tempUser = new ArrayList<String>();
                List<String> tempPass = new ArrayList<String>();
                List<String> tempImage=new ArrayList<String>();
                boolean userPassCorrect=false;
                boolean res=false;

                //Toast.makeText(getApplicationContext(),"zoooort", Toast.LENGTH_SHORT).show();
                int i = (int) dataSnapshot.getChildrenCount();

                for (DataSnapshot dsp :  dataSnapshot.getChildren())
                {
                    dsp.getKey();
                    //Toast.makeText(getApplicationContext(), "Teststtttt : " + i, Toast.LENGTH_LONG).show();
                 //    test.add(String.valueOf(dsp.child("emailString").getValue()));
                    //Toast.makeText(getApplicationContext(),"dool:  "+dsp.child("Users").child("username").getValue().toString(),Toast.LENGTH_SHORT).show();
                    tempUser.add(String.valueOf(dsp.child("emailString").getValue()));
                    tempPass.add(String.valueOf(dsp.child("passString").getValue()));
                    tempImage.add(String.valueOf(dsp.child("profileImage").getValue()));
                }
                int findPass=0;
                for (String str : tempUser) {
                     if (str.equalsIgnoreCase(emailString)) {
                        res = true;
                         //Toast.makeText(getApplicationContext(),str, Toast.LENGTH_SHORT).show();
                         break;
                    }
                    findPass++;
                }
                if(findPass<tempPass.size()&& res)
                    if((String.valueOf(tempPass.get(findPass))).equalsIgnoreCase(passString)) {
                        userPassCorrect = true;
                        profileImageOnString=tempImage.get((findPass));
                    }
                //Toast.makeText(getApplicationContext(), (String.valueOf(userPassCorrect)), Toast.LENGTH_LONG).show();

                if (!res && callByWhichMethod.equalsIgnoreCase("signup")) {
                    addNewUser();
                 //   Toast.makeText(getApplicationContext(),"The new user is created.",Toast.LENGTH_SHORT).show();
                } else if(res && callByWhichMethod.equalsIgnoreCase("signup"))
                    Toast.makeText(getApplicationContext(), "This Email used by another user", Toast.LENGTH_SHORT).show();
              //  sendResOfUserPass(userPassCorrect,1);
                if(callByWhichMethod.equalsIgnoreCase("login")&& userPassCorrect)
                    sendResOfUserPass();
                  //  loadProfileImageTest(); // it's just for testing , and it's correct
                else if (callByWhichMethod.equalsIgnoreCase("login")&& !userPassCorrect)
                    Toast.makeText(getApplicationContext(),"Username or password isn't correct",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    public void loadProfileImageTest()
    {
        Bitmap bit;
        ImageView image=(ImageView)findViewById(R.id.profilePicture);
        byte[] decodedByteArray = android.util.Base64.decode(profileImageOnString, Base64.DEFAULT);
        bit= BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
        image.setRotation(-90);
        image.setImageBitmap(bit);

    }
    public void sendResOfUserPass()
    {
        String UserName=email.getText().toString();
        Toast.makeText(getApplicationContext(),"Username and password is correct",Toast.LENGTH_LONG).show();
        Intent intent=new Intent(getApplicationContext(),Meeting_setup.class);
        intent.putExtra("username",UserName);
        intent.putExtra("picture",profileImageOnString);

       // intent.putExtra("pic",bitmap);

        startActivity(intent);



    }
    String imageBinaryFile;
    public void addNewUser()
    {
        final String emailString=email.getText().toString();
        final String passString=password.getText().toString();

        DatabaseReference addUser=FirebaseDatabase.getInstance().getReference("Users");
        //
        // DatabaseReference userRef=myRef.child("users");

        //userRef.setValue(email.getText().toString());
        //to change user profile photo to a binary file
        BitmapDrawable drawable = (BitmapDrawable) profilePicture.getDrawable();
        Bitmap image = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        imageBinaryFile = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

       // myRef.child("users/username").push().setValue(emailString);
       // myRef.child("users/password").push().setValue(passString);
       // myRef.child("users/profileImage").push().setValue(imageBinaryFile);

        String ID=addUser.push().getKey();
        users user=new users(emailString,passString,imageBinaryFile);
        addUser.child(ID).setValue(user);

        //addUser.push().child("password").setValue(passString);
        //addUser.push().child("profileImage").setValue(imageBinaryFile);

        //addUser.push().setValue(emailString);
        //addUser.push().setValue(passString);
        //addUser.push().setValue(imageBinaryFile);
   // Toast.makeText(getApplicationContext(),"Get key: "+ID,Toast.LENGTH_SHORT).show();
    }

    public void userNames(Map<String, Object> users)
    {
        boolean userExist=false;
        ArrayList<String> username=new ArrayList<>();
        for(Map.Entry<String ,Object> entry:users.entrySet()) {
            //get user list
            Map singleUser = (Map) entry.getValue();
            username.add((String) singleUser.get("users/username"));
        }
        userExist= searchUser(username);
        if (userExist)
        {
            Toast.makeText(getApplicationContext(),"Email address exist!!!", Toast.LENGTH_LONG).show();
        }
       // return userExist;
    }

    public boolean searchUser(ArrayList<String> users)
    {
        boolean res=false;
        String username=email.getText().toString();
        for(String item:users)
        {
            if(item.contains(username)) res=true;
        }
        return res;
    }
    public static MainActivity getInstance(){
        if(instance == null)
            instance = new MainActivity();

        return instance;
    }

    public Object getCurrentGroup() {
        return this.getCurrentGroup();
    }
}
