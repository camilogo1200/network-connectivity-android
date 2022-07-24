package com.camilogo1200.connectivity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val DEBUG_TAG = "NetworkStatusExample"
    private val networkManager by lazy { NetworkManager(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(DEBUG_TAG, "Is online [${networkManager.isNetworkAvailable()}]")
    }
}