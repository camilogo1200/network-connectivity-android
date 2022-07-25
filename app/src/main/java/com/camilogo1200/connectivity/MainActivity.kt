package com.camilogo1200.connectivity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope

class MainActivity : AppCompatActivity() {

    private val DEBUG_TAG = "NetworkStatusExample"
    private val networkManager by lazy { NetworkManager(this) }

    private lateinit var view: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(DEBUG_TAG, "Is online [${networkManager.isNetworkAvailable()}]")

        lifecycleScope.launchWhenResumed {
            networkManager.networkStatus.collect {

                findViewById<View>(R.id.connectivity_overlay).apply { isGone = it.isConnected }
                Log.d(
                    DEBUG_TAG,
                    "Flow : Network Status => isConnected:[${it.isConnected}] -  ConnectionType:[${it.connectionType}] "
                )
            }
        }

    }
}
