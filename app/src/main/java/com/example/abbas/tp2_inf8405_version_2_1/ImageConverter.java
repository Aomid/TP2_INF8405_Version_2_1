package com.example.abbas.tp2_inf8405_version_2_1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by Franck on 23/03/2017.
 */

public class ImageConverter {
    public static String encode(ImageView imageView) {
        Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        return encodeBitmap(image);
    }

    public static String encodeBitmap(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);
    }

    public static Bitmap decodeIntoBitmap(String encode) {
        byte[] decodedByteArray = android.util.Base64.decode(encode, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    public static void decodeInto(String encode, ImageView image) {
        Bitmap bit = decodeIntoBitmap(encode);
        image.setRotation(-90);
        image.setImageBitmap(bit);
    }
}
