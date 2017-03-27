package com.example.abbas.tp2_inf8405_version_2_1;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Franck on 23/03/2017.
 */

public interface MyMarker {

    boolean isLocationAvailable();

    enum MarkerType {USER, PLACE, ELECTED_PLACE,ORGANIZER}

    MarkerOptions provideMarkerOptions();
    Marker retrieveMarker();
    void setMarker(Marker marker);
    MarkerType getMarkerType();
    void setMarkerType(MarkerType markerType);
    String getName();
    String getDescription();
    String getIcon();
    /*
    @Override
    String toString();*/
}
