package com.example.myapplication

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Debug
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.FragmentSensorBinding
import com.example.myapplication.databinding.SensorXyzBinding

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
    companion object {
        const val TAG: String = "com.example.myapplication.SensorFragment"
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

        mBinding.myfab.setOnClickListener {
            Debug.startNativeTracing()
            Log.d(TAG,"getSensorList(Sensor.TYPE_ALL) is called")
            Log.d(TAG, "Sensor List: ${mSensorManager.getSensorList(Sensor.TYPE_ALL)}")
            Debug.stopNativeTracing()
        }
        XYZ_SENSORS.forEach { type ->
            val curSensor = mSensorManager.getDefaultSensor(type)
            mSensorLayoutByType[type] = curSensor
            mSensorManager.registerListener(
                object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent?) {
                        event?: return
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
        /*
                mBinding.textSensorList.text =
                    "Sensor List: " + sensorList.joinToString(", ", transform = { it -> it.name })
                Log.d(TAG, "Sensor List: ${sensorList.joinToString { it.name }}")

                var sensorListText: String = ""
                sensorList.forEach { sensor ->
                    sensorListText += "${sensor.name} "
                }
        */


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

}