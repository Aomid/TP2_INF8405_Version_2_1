package com.example.abbas.tp2_inf8405_version_2_1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Observable;

public class Meeting_Setup extends EventActivity
{
    private static final String TAG = "Meeting Setup";
    final int SELECT_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialisation();
        Log.d("Franck", " Instance setup");
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(meetingEvent.amITheOrganizer()) {
            showDialogPlace(latLng);
        }
    }


    public void selectPhoto(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    protected void showRating(){
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        //final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        //final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        showImageSelected(imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }
    private void showImageSelected(Uri uri) throws FileNotFoundException {
        ImageView img=addDialog.getImageView();
        final InputStream imageStream = getContentResolver().openInputStream(uri);
        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        img.setImageBitmap(selectedImage);
        img.setVisibility(View.VISIBLE);

    }

    @Override
    protected void nextAction() {
        if (meetingEvent.getPlaces().size() >= meetingEvent.getNbPlacesMin()) {
            meetingEvent.setStatus(MeetingEvent.Code.RATING_PLACES);
            saveMeetingEvent();
            passVoteActivity();
        }
    }
}