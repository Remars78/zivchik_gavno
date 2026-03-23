package com.zivchik.gavno.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zivchik.gavno.core.ConfigGenerator
import com.zivchik.gavno.core.VpnManager
import com.zivchik.gavno.data.ConfigDao
import com.zivchik.gavno.data.ConfigType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val vpnManager: VpnManager,
    private val configDao: ConfigDao
) : ViewModel() {

    private val _isWhitelistMode = MutableStateFlow(false)
    val isWhitelistMode: StateFlow<Boolean> = _isWhitelistMode

    val allConfigs = configDao.getAllConfigs().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun toggleVpn() {
        // Simple logic: pick first available config
        viewModelScope.launch {
            val type = if (_isWhitelistMode.value) ConfigType.WHITELIST else ConfigType.VLESS
            val configs = configDao.getConfigsByType(type)
            val vlessConfigs = configs.filter { it.type == ConfigType.VLESS }.map { JSONObject(it.rawJson) }
            val awgConfigs = configDao.getConfigsByType(ConfigType.AWG).map { JSONObject(it.rawJson) }
            
            val fullConfig = ConfigGenerator.generateConfig(vlessConfigs, awgConfigs, _isWhitelistMode.value)
            vpnManager.startVpn(fullConfig)
        }
    }

    fun stopVpn() {
        vpnManager.stopVpn()
    }

    fun toggleWhitelistMode(enabled: Boolean) {
        _isWhitelistMode.value = enabled
    }
}
