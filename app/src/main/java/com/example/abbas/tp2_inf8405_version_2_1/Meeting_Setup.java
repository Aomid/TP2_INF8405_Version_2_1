package com.example.abbas.tp2_inf8405_version_2_1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class Meeting_Setup extends EventActivity
{
    private static final String TAG = "Meeting Setup";
    private static AddPlaceDialog addDialog  = null;
    final int SELECT_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void showDialogPlace(LatLng latlng) {
        Meeting_Setup.addDialog = new AddPlaceDialog();
       /* if(Meeting_Setup.addDialog == null){
            Meeting_Setup.addDialog = new AddPlaceDialog();
        }*/
        EventPlace ep = new EventPlace(latlng.latitude, latlng.longitude);
        Meeting_Setup.addDialog.setPlace(ep);
        Log.d("Franck", "Launch Fragment Add Place");
        Log.d("Franck", meetingEvent.detailsIntoString());
        Meeting_Setup.addDialog.show(getFragmentManager(),"Place");
    }

    public boolean addPlace(EventPlace place) {
        if(place.getName().length()!=0) {
            if (meetingEvent.addPlace(place)) {
                place.setMarker(map.addMarker(place.provideMarkerOptions()));
                saveMeetingEvent();
                return true;
            } else {
                Toast.makeText(this, "Place not created (Name already exists)", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else {
            Toast.makeText(this, "Place not created (Name not provided )", Toast.LENGTH_SHORT).show();
            return false;
        }
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
        final InputStream imageStream = getContentResolver().openInputStream(uri);
        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        addDialog.setImageView(selectedImage);
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