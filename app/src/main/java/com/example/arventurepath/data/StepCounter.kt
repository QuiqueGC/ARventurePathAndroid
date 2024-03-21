package com.example.arventurepath.data
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepCounter : SensorEventListener{

    private var sensorManager: SensorManager ?= null

    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    override fun onSensorChanged(event: SensorEvent?) {
        TODO("Not yet implemented")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }

}