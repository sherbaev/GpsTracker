package me.sherbaev.gpstracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.LocationResult

class MyBackgroundService : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_PROCESS_UPDATE == action) {
                val result:LocationResult? = LocationResult.extractResult(intent)
                if (result != null) {
                    val location = result.lastLocation
                    val longlat = "${location.longitude}/${location.latitude}"
                    val intent = Intent(LOCATION_UPDATE_INTENT)
                    intent.putExtra(LONGLAT, longlat)
                    context?.sendBroadcast(intent)
                }
            }
        }
    }


}
