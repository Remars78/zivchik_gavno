package com.zivchik.gavno.data

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.json.JSONArray
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepository @Inject constructor(
    private val configDao: ConfigDao,
    private val httpClient: OkHttpClient
) {

    suspend fun updateVlessConfigs(url: String) {
        try {
            val response = fetchConfigs(url)
            val vlessLinks = response.lines().filter { it.startsWith("vless://") }
            val configs = vlessLinks.mapNotNull { parseVless(it) }
            
            if (configs.isNotEmpty()) {
                configDao.clearConfigsByType(ConfigType.VLESS)
                configDao.insertConfigs(configs.map { 
                    ConfigEntity(type = ConfigType.VLESS, name = it.getString("tag"), rawJson = it.toString()) 
                })
            }
        } catch (e: Exception) {
            Log.e("ConfigRepo", "Error updating VLESS", e)
        }
    }

    suspend fun updateAwgConfigs() {
        val mockAwg = JSONObject().apply {
            put("type", "wireguard")
            put("tag", "AWG-Warp")
            put("server", "162.159.193.1")
            put("server_port", 2408)
            put("local_address", JSONArray(listOf("172.16.0.2/32")))
            put("private_key", "MOCK_PRIVATE_KEY")
            put("peer_public_key", "bmXOC+F1FxEMF9dyiK2H5/1SUtzH0JuVo51h2wPfgyo=")
            
            put("amnezia_jc", 4)
            put("amnezia_jmin", 40)
            put("amnezia_jmax", 80)
            put("amnezia_s1", 20)
            put("amnezia_s2", 100)
            put("amnezia_h1", 1)
            put("amnezia_h2", 2)
            put("amnezia_h3", 3)
            put("amnezia_h4", 4)
        }
        
        configDao.clearConfigsByType(ConfigType.AWG)
        configDao.insertConfig(ConfigEntity(
            type = ConfigType.AWG, 
            name = "Warp AWG", 
            rawJson = mockAwg.toString()
        ))
    }

    suspend fun pingServer(config: ConfigEntity): Long {
        return try {
            val json = JSONObject(config.rawJson)
            val host = json.getString("server")
            val port = json.getInt("server_port")
            
            val start = System.currentTimeMillis()
            val socket = java.net.Socket()
            socket.connect(java.net.InetSocketAddress(host, port), 2000)
            socket.close()
            val end = System.currentTimeMillis()
            end - start
        } catch (e: Exception) {
            -1L
        }
    }

    suspend fun refreshAllPings() {
        val configs = configDao.getConfigsByType(ConfigType.VLESS) + 
                     configDao.getConfigsByType(ConfigType.AWG)
        
        configs.forEach { config ->
            val ping = pingServer(config)
            configDao.updateConfig(config.copy(pingMs = ping))
        }
        
        configDao.clearDeadConfigs()
    }

    private fun fetchConfigs(url: String): String {
        val request = Request.Builder().url(url).build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Unexpected code $response")
            return response.body?.string() ?: ""
        }
    }

    private fun parseVless(link: String): JSONObject? {
        return try {
            val uri = URI(link)
            val userInfo = uri.userInfo
            val host = uri.host
            val port = uri.port
            val query = uri.query
            val tag = uri.fragment ?: "VLESS-${host}"
            
            JSONObject().apply {
                put("type", "vless")
                put("tag", tag)
                put("server", host)
                put("server_port", port)
                put("uuid", userInfo)
                
                val params = query?.split("&")?.associate {
                    val parts = it.split("=")
                    parts[0] to (parts.getOrNull(1) ?: "")
                } ?: emptyMap()
                
                put("flow", params["flow"] ?: "")
                
                val tls = JSONObject()
                tls.put("enabled", params["security"] == "tls" || params["security"] == "reality")
                tls.put("server_name", params["sni"] ?: "")
                
                if (params["security"] == "reality") {
                    val reality = JSONObject()
                    reality.put("enabled", true)
                    reality.put("public_key", params["pbk"] ?: "")
                    reality.put("short_id", params["sid"] ?: "")
                    tls.put("reality", reality)
                }
                
                put("tls", tls)
            }
        } catch (e: Exception) {
            null
        }
    }
}
