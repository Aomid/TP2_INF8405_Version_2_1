package com.example.abbas.tp2_inf8405_version_2_1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by Franck on 23/03/2017.
 */


/* Fragment pour la fenêtre de dialogue qui permet de mettre un nouveau lieu */
public class AddPlaceDialog extends DialogFragment {

    //Le lieu qui sera rajout
    public static  EventPlace place;
    private View layout=null;
    private  ImageView placeImage;
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
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddPlaceDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }


    @Override
    public void onStart(){
        super.onStart();
        ( (AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildEventPlace();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if(place.getIcon() != null)
            ((ImageView) getDialog().findViewById(R.id.place_image)).setImageBitmap(ImageConverter.decodeIntoBitmap(place.getIcon()));
    }

    public boolean buildEventPlace(){
        boolean result ;
        place.setName(((EditText) getDialog().findViewById(R.id.placename)).getText().toString());
        place.setDescription(((EditText) getDialog().findViewById(R.id.description)).getText().toString());
        place.setIcon(ImageConverter.encode((ImageView) getDialog().findViewById(R.id.place_image)));
        if( (result = ((Meeting_Setup)getActivity()).addPlace(place)))
            dismiss();
        return result;
    }

    public void setPlace(EventPlace place) {
        this.place = place;
    }


    public void setImageView(Bitmap bitmap) {
        place.setIcon(ImageConverter.encodeBitmap(bitmap));
       // ((ImageView) getDialog().findViewById(R.id.place_image)).setImageBitmap(bitmap);
        //((ImageView) getDialog().findViewById(R.id.place_image)).setVisibility(View.VISIBLE);
    }

}
