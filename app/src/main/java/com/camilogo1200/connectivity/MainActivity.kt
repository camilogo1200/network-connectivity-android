package com.camilogo1200.connectivity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.camilogo1200.connectivity.NetworkManager.isNetworkAvailable

class MainActivity : AppCompatActivity() {

    private val DEBUG_TAG = "NetworkStatusExample"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(DEBUG_TAG, "Is online [${isNetworkAvailable(this)}]")
        NetworkManager.networkListener(this)
    }
}