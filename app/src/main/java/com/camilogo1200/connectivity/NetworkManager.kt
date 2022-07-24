package com.camilogo1200.connectivity

import android.content.Context
import android.net.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class NetworkManager(private val context: Context) {

    private val tag = NetworkManager::class.java.name
    private lateinit var onLostListener: () -> Unit
    private lateinit var onAvailableListener: () -> Unit

    init {
        setUpNetworkListeners()
    }

    fun setOnAvailableNetworkListener(onAvailableListener: () -> Unit) {
        this.onAvailableListener = onAvailableListener
    }

    fun setOnLostNetworkListener(onLostListener: () -> Unit) {
        this.onLostListener = onLostListener
    }


    fun isNetworkAvailable(): Boolean { //inject context
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
        connectivityManager.getNetworkCapabilities(network)?.let {
            val internetConnection = it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val validatedNetwork = it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            return (internetConnection && validatedNetwork)
        }
        return false
    }

    private fun setUpNetworkListeners() {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        connectivityManager.registerDefaultNetworkCallback(
            object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    if (::onAvailableListener.isInitialized) {
                        onAvailableListener.invoke()
                    }
                    Log.e(tag, "onAvailable() $network")
                }

                override fun onLost(network: Network) {
                    if (::onLostListener.isInitialized) {
                        onLostListener.invoke()
                    }
                    Log.e(tag, "onLost()")
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
                    Log.e(tag, "Internet: $internet - Validated: $validated")
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
