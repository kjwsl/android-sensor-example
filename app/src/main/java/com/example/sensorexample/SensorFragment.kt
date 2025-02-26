package com.example.sensorexample

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import android.location.OnNmeaMessageListener
import android.os.Bundle
import android.os.Debug
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.sensorexample.databinding.FragmentSensorBinding
import com.example.sensorexample.databinding.SensorXyzBinding

/**
 * A simple [Fragment] subclass.
 * Use the [SensorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

val XYZ_SENSORS: Array<Int> = arrayOf(
    Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
    Sensor.TYPE_ACCELEROMETER,
    Sensor.TYPE_MAGNETIC_FIELD,
    Sensor.TYPE_GYROSCOPE
)


class SensorFragment : Fragment() {
    private lateinit var locationManager: LocationManager;

    // Register the permission launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // FINE_LOCATION granted
                    onLocationPermissionsGranted()
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // COARSE_LOCATION granted
                    onLocationPermissionsGranted()
                }

                else -> {
                    // Permissions denied
                    onLocationPermissionsDenied()
                }
            }
        }

    companion object {
        const val TAG: String = "com.example.sensorexample.SensorFragment"
    }

    private lateinit var mSensorManager: SensorManager
    private lateinit var mBinding: FragmentSensorBinding
    private var mSensorLayoutByType: HashMap<Int, Sensor?> = HashMap()
    private var mSensorBindingByType: HashMap<Int, SensorXyzBinding> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = FragmentSensorBinding.inflate(layoutInflater)
        mSensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL)

        checkAndRequestLocationPermissions(requireContext());
        mBinding.myfab.setOnClickListener {
            Debug.startNativeTracing()
            Log.d(TAG, "getSensorList(Sensor.TYPE_ALL) is called")
            Log.d(TAG, "Sensor List: ${mSensorManager.getSensorList(Sensor.TYPE_ALL)}")
            Debug.stopNativeTracing()
        }
        XYZ_SENSORS.forEach { type ->
            val curSensor = mSensorManager.getDefaultSensor(type)
            mSensorLayoutByType[type] = curSensor
            mSensorManager.registerListener(
                object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent?) {
                        event ?: return
                        var sensorBinding = mSensorBindingByType[type]
                        if (sensorBinding == null) {
                            mSensorBindingByType[type] = SensorXyzBinding.inflate(layoutInflater)
                            mBinding.parentLayout.addView(mSensorBindingByType[type]!!.root)
                            sensorBinding = mSensorBindingByType[type]!!
                        }
                        with(sensorBinding) {
                            sensor = event.sensor.stringType
                            dataX = event.values[0].toString()
                            dataY = event.values[1].toString()
                            dataZ = event.values[2].toString()
                        }
                    }

                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    }

                },
                curSensor,
                100000
            )

        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return this.mBinding.root
    }

    private fun checkAndRequestLocationPermissions(ctx: Context) {
        val fineLocation = ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocation = ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val permissionsToRequest = mutableListOf<String>()

        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (coarseLocation != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            // Optionally, show a rationale here before requesting
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // Permissions are already granted
            onLocationPermissionsGranted()
        }
    }

    @SuppressLint("MissingPermission")
    private fun onLocationPermissionsGranted() {
        // Initialize LocationManager and proceed with location-related tasks
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // For example, check if providers are enabled and request location updates
        Toast.makeText(context, "Location permissions granted.", Toast.LENGTH_SHORT).show()
        // Proceed to initialize LocationManager or other location services

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10f, MyLocationListener(requireContext()))
        val nmeaHandlerThread = HandlerThread("NmeaHandler")
        nmeaHandlerThread.start()
        val nmeaHandler = Handler(nmeaHandlerThread.looper)
        locationManager.addNmeaListener(OnNmeaMessageListener { message, timestamp -> Log.d(TAG, "NMEA: $message, timestamp: $timestamp") },nmeaHandler)
    }

    private fun onLocationPermissionsDenied() {
        // Inform the user that the permissions are necessary
        Toast.makeText(
            context,
            "Location permissions are required for this app to function correctly.",
            Toast.LENGTH_LONG
        ).show()
        // Optionally, guide the user to app settings
    }

}