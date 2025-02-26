package com.example.sensorexample;

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class MyLocationListener(ctx: Context) : LocationListener {
    private val ctx = ctx

    companion object {
        const val LOG_TAG = "MyLocationListener"
    }

    override fun onLocationChanged(locations: MutableList<Location>) {
        Log.d(LOG_TAG, "Locations: $locations")
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.d(LOG_TAG, "GNSS Status: $status")
    }

    override fun onLocationChanged(location: Location) {
        Log.d(LOG_TAG, "Location: $location")
    }

    override fun onProviderEnabled(provider: String) {
        Toast.makeText(ctx, "$provider enabled", Toast.LENGTH_LONG).show()
    }

    override fun onProviderDisabled(provider: String) {
        Toast.makeText(ctx, "$provider disabled", Toast.LENGTH_LONG).show()
    }
}