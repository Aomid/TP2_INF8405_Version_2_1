package com.example.abbas.tp2_inf8405_version_2_1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Created by Abbas on 3/20/2017.
 * This classes monitors the battery usage for all the application and for some actions
 */

public class RessourceMonitor extends BroadcastReceiver {
    static private RessourceMonitor mInstance = null;
    private float lastBatteryLevel;
    private Float initialBatteryLevel;
    private Intent batteryStatus;

    static public RessourceMonitor getInstance(){
        if(mInstance == null)
            mInstance = new RessourceMonitor();
        return mInstance;
    }

    private RessourceMonitor(){
        lastBatteryLevel = 0f;
        initialBatteryLevel = 0f;
        batteryStatus = new Intent();
    }

   // @Override
    public void onReceive(Context context, Intent intent) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.batteryStatus = context.registerReceiver(null, ifilter);
        if(initialBatteryLevel != null) initialBatteryLevel = GetCurrentBatteryLevel();
    }

    // Returns the current battery level
    public float GetCurrentBatteryLevel()
    {
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = (float)level / (float)scale * 100.0f;
        return batteryPct;
    }

    // Returns the battery usage for all the app
    public float GetTotalBatteryUsage()
    {
        return  GetCurrentBatteryLevel() - initialBatteryLevel;
    }

    // Saves the current battery level
    public void SaveCurrentBatteryLevel()
    {
        this.lastBatteryLevel = this.GetCurrentBatteryLevel();
    }

    // Returns the battery usage since the last save
    public float GetLastBatteryUsage()
    {
        float latestBatteryLevel = GetCurrentBatteryLevel();
        return latestBatteryLevel - lastBatteryLevel;
    }
}
