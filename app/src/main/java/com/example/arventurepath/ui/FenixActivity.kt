package com.example.arventurepath.ui
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.company.product.OverrideUnityActivity;

class FenixActivity : OverrideUnityActivity() {
    override fun showMainActivity(setToColor: String?) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(">", "SE HA CREADO LA ACTIVITY")
    }

    override fun onUnityPlayerUnloaded() {
        //super.onUnityPlayerUnloaded()
        showMainActivity("");
    }
}