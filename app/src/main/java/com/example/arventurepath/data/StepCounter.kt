package com.example.arventurepath.data
import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.request.SensorRequest
import java.util.concurrent.TimeUnit

class StepCounter(
    private val context: Context,
    private val account: GoogleSignInAccount,
    private val onStepCountListener: (Int) -> Unit
) {
    private var onDataPointListener: OnDataPointListener? = null

    fun start() {
        onDataPointListener = OnDataPointListener { dataPoint ->
            val stepCount = dataPoint.getValue(Field.FIELD_STEPS).asInt()
            onStepCountListener.invoke(stepCount)
        }

        val request = SensorRequest.Builder()
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setSamplingRate(3, TimeUnit.SECONDS)
            .build()

        Fitness.getSensorsClient(context, account)
            .add(request, onDataPointListener!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("StepCounter", "Listener registered!")
                } else {
                    Log.e("StepCounter", "Listener not registered.", task.exception)
                }
            }
    }

    fun stop() {
        onDataPointListener?.let {
            Fitness.getSensorsClient(context, account)
                .remove(it)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("StepCounter", "Listener removed!")
                    } else {
                        Log.e("StepCounter", "Listener not removed.", task.exception)
                    }
                }
        }
        onDataPointListener = null
    }
}
