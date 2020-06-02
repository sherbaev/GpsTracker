package me.sherbaev.gpstracker

import android.content.Context
import android.widget.Toast

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

const val ACTION_PROCESS_UPDATE = "me.sherbaev.gpstracker.UPDATE_LOCATION"
const val LOCATION_UPDATE_INTENT = "me.sherbaev.gpstracker.LOCATION_UPDATE_INTENT"
const val LONGLAT = "me.sherbaev.gpstracker.LONGLAT"

fun ArrayList<String>.average(): String {
    var long = 0.0
    var lat = 0.0
    this.forEach {
        long += it.substringBefore("/").toDouble()
        lat += it.substringAfter("/").toDouble()
    }
    return "${long/this.size}/${lat/this.size}"
}