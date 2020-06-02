package me.sherbaev.gpstracker

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {
    var isChecked = true
    var exactLocation = ArrayList<String>()
    lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override val permissions: Array<String>?
        get() = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    override fun permissionGranted() {
        tv.text = "Updating..."
        updateLocation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvLong.movementMethod = ScrollingMovementMethod()
        tvLat.movementMethod = ScrollingMovementMethod()
        sw.isChecked = true
        if (checkPermission()) {
            updateLocation()
        } else {
            tv.text = "Permission is not given"
        }
        updateStatus(sw.isChecked)
        sw.setOnCheckedChangeListener { _, isChecked ->
            updateStatus(isChecked)
        }
    }

    private fun updateStatus(isChecked: Boolean) {
        this.isChecked = isChecked
        if (isChecked) {
            registerReceiver(broadcastReceiver, IntentFilter(LOCATION_UPDATE_INTENT))
        } else {
            unregisterReceiver(broadcastReceiver)
        }
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val message = intent.getStringExtra(LONGLAT)
            if (message.isNullOrEmpty().not() && message.contains("/")) {
                    if (exactLocation.size < 1) {
                        timer.start()
                        Handler().postDelayed({
                            tvLong.text = ""
                            tvLat.text = ""
                            tv.text = exactLocation.average()
                            context?.toast("${exactLocation.average()}\n\n${exactLocation.size} coordinates fetched")
                            exactLocation.clear()
                        }, 30000)
                    }
                    exactLocation.add(message)
                    tvLong.text =
                        "${tvLong.text}\n${message.substringBefore("/")}"
                    tvLat.text =
                        "${tvLat.text}\n${message.substringAfter("/")}"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isChecked.not()) {
            unregisterReceiver(broadcastReceiver)
        }
    }

    val timer = object : CountDownTimer(30000, 1000) {
        override fun onFinish() {
            tvTimer.text = "30"
        }

        override fun onTick(count: Long) {
            tvTimer.text = "${count/1000}"
        }
    }

    private fun updateLocation() {
        buildLocationRequest()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent())
    }

    private fun getPendingIntent(): PendingIntent? {
        val intent = Intent(this, MyBackgroundService::class.java)
        intent.action = ACTION_PROCESS_UPDATE
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 300
        locationRequest.fastestInterval = 1
    }


    override fun permissionDenied() {
    }
}