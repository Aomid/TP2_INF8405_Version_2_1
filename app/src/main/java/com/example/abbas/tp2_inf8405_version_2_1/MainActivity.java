package com.example.abbas.tp2_inf8405_version_2_1;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static  MainActivity instance=null;
    private static String emaildefault = null;
    private static boolean userPassOK=false;
    private static EditText email;
    private static EditText password;
    private static Boolean userExist=false;
    private static String profileImageOnString;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    String imageBinaryFile;
    private Uri f;
    private ImageButton imagebutton;
    private Bitmap bitmap;
    private Button loginButton, signupButton, setProfilePicButton;
    private ImageView profilePicture;
    private LinearLayout signinOptions;

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

    public static MainActivity getInstance() {
        if (instance == null)
            instance = new MainActivity();

        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RessourceMonitor.getInstance();
        Intent batteryStatus=registerReceiver(RessourceMonitor.getInstance(), new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.pass);
        loginButton = (Button) findViewById(R.id.login_button);
        signupButton = (Button) findViewById(R.id.signup_button);
        setProfilePicButton = (Button) findViewById(R.id.set_profile_picture_button);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);
//create a Database instance
        Firebase.setAndroidContext(this);


        /*loginButton.setEnabled(false);
        signupButton.setEnabled(false);
        setProfilePicButton.setEnabled(false);*/


        //profilePicture.setVisibility(View.GONE);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.length() > 2 && email.length() > 2)
                    Login();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.length() > 2 && email.length() > 2)
                   Signup();
            }
        });

        //in method vase dokme set profile picture mibashad, baz kardane Camera
        //va seda kardane method zakhire pic
        setProfilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.length() > 2 && email.length() > 2) {
                    Toast.makeText(getApplicationContext(), "Camera is started", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    f = Uri.fromFile(getMediaFile());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, f);
                    startActivityForResult(intent, 1);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent imageReturned) {
        super.onActivityResult(requestCode, resultCode, imageReturned);
        super.onActivityResult(requestCode, resultCode, imageReturned);
        String path = f.toString();

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
        //Toast.makeText(getApplicationContext(),"Username and password is correct",Toast.LENGTH_LONG).show();
        UserProfile.setINSTANCE(new User(UserName,profileImageOnString));
        Intent intent=new Intent(getApplicationContext(),Group_Choice_Activity.class);
        startActivity(intent);



    }

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
        User user=new User(emailString,passString,imageBinaryFile);
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

    public Object getCurrentGroup() {
        return this.getCurrentGroup();
    }
    @Override
    public void onBackPressed() {
        String batteryLevelMessage =
                new String("Battery usage : " + String.valueOf(RessourceMonitor.getInstance().GetTotalBatteryUsage()));
        ShowBatteryUsage("Application battery usage", batteryLevelMessage, true);
    }



    void ShowBatteryUsage(String title, String message, boolean leavePage) {
        final boolean leave = leavePage;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (leave) finish();
            }
        });
        if(leave)
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                }
            });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
