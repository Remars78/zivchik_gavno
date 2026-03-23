package com.zivchik.gavno.core

import android.content.Intent
import android.net.VpnService
import android.os.Binder
import android.os.IBinder
import android.util.Log
import io.nekohasekai.libbox.BoxService
import io.nekohasekai.libbox.Libbox
import io.nekohasekai.libbox.BoxListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ZivchikVpnService : VpnService() {

    private var boxService: BoxService? = null
    private val _state = MutableStateFlow(VpnState.STOPPED)
    val state: StateFlow<VpnState> = _state

    inner class ServiceBinder : Binder() {
        fun getService(): ZivchikVpnService = this@ZivchikVpnService
    }

    override fun onBind(intent: Intent?): IBinder {
        return if (intent?.action == SERVICE_INTERFACE) super.onBind(intent)!! else ServiceBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val configJson = intent?.getStringExtra(EXTRA_CONFIG)
        if (configJson != null) {
            startVpn(configJson)
        }
        return START_STICKY
    }

    private fun startVpn(configJson: String) {
        if (_state.value != VpnState.STOPPED) return

        _state.value = VpnState.STARTING
        Log.i(TAG, "Starting VPN with config: $configJson")

        try {
            // Setup sing-box context
            Libbox.setContext(applicationContext)

            // Create box listener for logs and status
            val listener = object : BoxListener {
                override fun writeLog(message: String?) {
                    Log.d("SingBox", message ?: "")
                }
            }

            // Create service
            boxService = Libbox.newService(configJson, listener)
            
            // Start the service
            boxService?.start()
            
            _state.value = VpnState.STARTED
            Log.i(TAG, "VPN Started")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start VPN", e)
            _state.value = VpnState.STOPPED
        }
    }

    fun stopVpn() {
        Log.i(TAG, "Stopping VPN")
        try {
            boxService?.stop()
            boxService?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping VPN", e)
        } finally {
            boxService = null
            _state.value = VpnState.STOPPED
        }
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }

    override fun onRevoke() {
        stopVpn()
        super.onRevoke()
    }

    companion object {
        private const val TAG = "ZivchikVpnService"
        const val EXTRA_CONFIG = "extra_config"
    }
}

enum class VpnState {
    STOPPED,
    STARTING,
    STARTED,
    STOPPING
}
