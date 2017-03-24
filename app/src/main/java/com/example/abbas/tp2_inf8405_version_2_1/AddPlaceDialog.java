package com.example.abbas.tp2_inf8405_version_2_1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Franck on 23/03/2017.
 */

public class AddPlaceDialog extends DialogFragment {
    private static String placeName;
    private EventPlace place;
    private View layout=null;
    private  ImageView placeImage;
    public AddPlaceDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        layout= inflater.inflate(R.layout.addplacelayout, null);
        placeImage=(ImageView) layout.findViewById(R.id.place_image);
        builder.setView(layout)
                // Add action buttons
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        place.setName(((EditText) layout.findViewById(R.id.placename)).getText().toString());
                        place.setDescription(((EditText) layout.findViewById(R.id.description)).getText().toString());
                        place.setIcon(ImageConverter.encode((ImageView) layout.findViewById(R.id.place_image)));
                        Meeting_Setup ms = (Meeting_Setup) getActivity();
                        ms.addPlace(place);
                        placeName=place.getName().toString();

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddPlaceDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    static public String getEventName()
    {
        return placeName;
    }
    public void setPlace(EventPlace place) {
        this.place = place;
    }


    public ImageView getImageView() {
        return placeImage;
    }
}
