package com.example.abbas.tp2_inf8405_version_2_1;

/**
 * Created by Abbas on 3/13/2017.
 */

public class Post {
    public String emailString;
    public String passString;
    public String profileImage;

    public Post(){

    }
    public Post(String emailStrin,String passString, String profileImage)
    {
        this.emailString=emailStrin;
        this.passString=passString;
        this.profileImage=profileImage;
    }

}
