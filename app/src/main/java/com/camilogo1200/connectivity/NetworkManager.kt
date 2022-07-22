package com.camilogo1200.connectivity

import android.content.Context
import android.net.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

object NetworkManager {




    private val TAG = NetworkManager::class.java.name
    fun isNetworkAvailable(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) isConnectivityAvailable(context)
        else isConnectivityAvailableLegacy(context)
    }


    private fun isConnectivityAvailableLegacy(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun isConnectivityAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

        val network = connectivityManager.activeNetwork

        val capabilities = connectivityManager.getNetworkCapabilities(network)

        capabilities?.let {
            val internetConnection = it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val validatedNetwork = it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            return (internetConnection && validatedNetwork)
        }
        return false
    }

    fun networkListener(context: Context) {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        connectivityManager.registerDefaultNetworkCallback(
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    //Log.e(TAG, "The default network is now: $network")
                    Log.e(TAG, "onAvailable()")
                }

                override fun onLost(network: Network) {

                    Log.e(TAG, "onLost()")
//                    Log.e(
//                        TAG,
//                        "The application no longer has a default network. The last default network was $network"
                    //)
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    val internet =
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    val validated =
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    Log.e(TAG, "Internet: $internet - Validated: $validated")
                    //Log.e(TAG, "The default network changed capabilities: $networkCapabilities")
                }

                override fun onLinkPropertiesChanged(
                    network: Network,
                    linkProperties: LinkProperties
                ) {
                    //Log.e(TAG, "The default network changed link properties: $linkProperties")
                }
            })
    }
}
