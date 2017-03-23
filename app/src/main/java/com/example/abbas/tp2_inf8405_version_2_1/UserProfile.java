package com.example.abbas.tp2_inf8405_version_2_1;

/**
 * Created by Abbas on 3/8/2017.
 */

public class UserProfile {

    private static User INSTANCE = null;

    public static User getInstance()
    {	return INSTANCE;
    }

    public static void setINSTANCE(User INSTANCE) {
        UserProfile.INSTANCE = INSTANCE;
    }
}
