package com.zivchik.gavno.core

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VpnManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun startVpn(configJson: String) {
        val intent = Intent(context, ZivchikVpnService::class.java).apply {
            putExtra(ZivchikVpnService.EXTRA_CONFIG, configJson)
        }
        context.startService(intent)
    }

    fun stopVpn() {
        val intent = Intent(context, ZivchikVpnService::class.java)
        context.stopService(intent)
    }
}
