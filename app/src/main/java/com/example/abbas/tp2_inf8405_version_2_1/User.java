package com.example.abbas.tp2_inf8405_version_2_1;

/**
 * Created by Abbas on 3/13/2017.
 */

public class User {
    public String emailString;
    public String passString;
    public String profileImage;

    public User()
    {
    }

    public User(String emailString, String profileImage)
    {
        this.emailString=emailString;
        this.profileImage=profileImage;
        this.passString=null;
    }

    public User(String emailString,String passString, String profileImage)
    {
        this.emailString=emailString;
        this.passString=passString;
        this.profileImage=profileImage;
    }

    @Override
    public boolean equals(Object o){
        boolean retVal = false;
        if (o instanceof User){
            User another = (User) o;
            retVal = another.emailString.compareToIgnoreCase(emailString) == 0;
        }
        return retVal;
    }
}
