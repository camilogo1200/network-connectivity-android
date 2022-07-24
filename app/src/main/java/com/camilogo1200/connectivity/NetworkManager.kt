package com.camilogo1200.connectivity

import android.content.Context
import android.net.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class NetworkManager(private val context: Context) {

    private val tag = NetworkManager::class.java.name
    private var onLostListener: (() -> Unit)? = null
    private var onAvailableListener: (() -> Unit)? = null

    var connectionType: ConnectionType = ConnectionType.UNKNOWN
        private set
    var hasConnectivity: Boolean = false
        private set

    val isCellular: Boolean
        get() = connectionType == ConnectionType.CELLULAR

    val isWifi: Boolean
        get() = connectionType == ConnectionType.WIFI
                || connectionType == ConnectionType.WIFI_AWARE

    init {
        setUpNetworkProperties()
        setUpNetworkListeners()
    }

    fun setOnAvailableNetworkListener(onAvailableListener: () -> Unit) {
        this.onAvailableListener = onAvailableListener
    }

    fun setOnLostNetworkListener(onLostListener: () -> Unit) {
        this.onLostListener = onLostListener
    }

    fun removeListeners() {
        onLostListener = null
        onAvailableListener = null
    }

    fun isNetworkAvailable(): Boolean { //inject context
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            isConnectivityAvailable(context)
        else isConnectivityAvailableLegacy(context)
    }

    @Suppress("DEPRECATION")
    private fun isConnectivityAvailableLegacy(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun isConnectivityAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val network = connectivityManager.activeNetwork
        return connectivityManager.getNetworkCapabilities(network)?.let {
            val internetConnection = it.hasCapability(NET_CAPABILITY_INTERNET)
            val validatedNetwork = it.hasCapability(NET_CAPABILITY_VALIDATED)
            internetConnection && validatedNetwork
        } ?: false
    }

    private fun setUpNetworkListeners() {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        connectivityManager.registerDefaultNetworkCallback(
            object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    onAvailableListener?.invoke()
                    Log.e(tag, "Connectivity Available")
                }

                override fun onLost(network: Network) {
                    onLostListener?.invoke()
                    Log.e(tag, "Connectivity Lost")
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    setNetworkType(networkCapabilities)
                    setConnectivity(networkCapabilities)
                }
            })
    }

    private fun setConnectivity(networkCapabilities: NetworkCapabilities) {
        val internet =
            networkCapabilities.hasCapability(NET_CAPABILITY_INTERNET)
        val validated =
            networkCapabilities.hasCapability(NET_CAPABILITY_VALIDATED)
        hasConnectivity = internet && validated
    }

    private fun setNetworkType(networkCapabilities: NetworkCapabilities) {
        if (networkCapabilities.hasTransport(TRANSPORT_ETHERNET)) {
            connectionType = ConnectionType.ETHERNET
            return
        }
        if (networkCapabilities.hasTransport(TRANSPORT_CELLULAR)) {
            connectionType = ConnectionType.CELLULAR
            return
        }
        if (networkCapabilities.hasTransport(TRANSPORT_WIFI) ||
            networkCapabilities.hasTransport(TRANSPORT_WIFI_AWARE)
        ) {
            connectionType = ConnectionType.CELLULAR
            return
        }
        if (networkCapabilities.hasTransport(TRANSPORT_USB)) {
            connectionType = ConnectionType.USB
            return
        }
        if (networkCapabilities.hasTransport(TRANSPORT_BLUETOOTH)) {
            connectionType = ConnectionType.BLUETOOTH
            return
        }
        if (networkCapabilities.hasTransport(TRANSPORT_VPN)) {
            connectionType = ConnectionType.VPN
            return
        }
        if (networkCapabilities.hasTransport(TRANSPORT_LOWPAN)) {
            connectionType = ConnectionType.LOWPAN
            return
        }
        connectionType = ConnectionType.UNKNOWN
    }

    private fun setUpNetworkProperties() {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val network = connectivityManager.activeNetwork
        connectivityManager.getNetworkCapabilities(network)?.let {
            setNetworkType(it)
            setConnectivity(it)
        }
    }
}
