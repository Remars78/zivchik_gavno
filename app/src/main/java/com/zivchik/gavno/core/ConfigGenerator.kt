package com.zivchik.gavno.core

import org.json.JSONArray
import org.json.JSONObject

/**
 * Utility to generate sing-box JSON configuration.
 */
object ConfigGenerator {

    fun generateConfig(
        vlessConfigs: List<JSONObject>,
        awgConfigs: List<JSONObject>,
        isWhitelistMode: Boolean
    ): String {
        val root = JSONObject()

        // 1. Log
        val log = JSONObject()
        log.put("level", "info")
        log.put("timestamp", true)
        root.put("log", log)

        // 2. DNS
        val dns = JSONObject()
        val dnsServers = JSONArray()
        
        val googleDns = JSONObject()
        googleDns.put("tag", "google")
        googleDns.put("address", "8.8.8.8")
        dnsServers.put(googleDns)
        
        dns.put("servers", dnsServers)
        root.put("dns", dns)

        // 3. Inbounds
        val inbounds = JSONArray()
        val tunInbound = JSONObject()
        tunInbound.put("type", "tun")
        tunInbound.put("tag", "tun-in")
        tunInbound.put("inet4_address", "172.19.0.1/30")
        tunInbound.put("auto_route", true)
        tunInbound.put("strict_route", true)
        tunInbound.put("sniff", true)
        inbounds.put(tunInbound)
        root.put("inbounds", inbounds)

        // 4. Outbounds
        val outbounds = JSONArray()
        
        // Add all provided configs
        // For simplicity, we just put them as is
        // In reality, we'd need to ensure tags are unique
        
        vlessConfigs.forEach { outbounds.put(it) }
        awgConfigs.forEach { outbounds.put(it) }

        // Default outbounds
        val directOut = JSONObject()
        directOut.put("type", "direct")
        directOut.put("tag", "direct")
        outbounds.put(directOut)

        val dnsOut = JSONObject()
        dnsOut.put("type", "dns")
        dnsOut.put("tag", "dns-out")
        outbounds.put(dnsOut)

        root.put("outbounds", outbounds)

        // 5. Route
        val route = JSONObject()
        val rules = JSONArray()

        // DNS rule
        val dnsRule = JSONObject()
        dnsRule.put("protocol", "dns")
        dnsRule.put("outbound", "dns-out")
        rules.put(dnsRule)

        // Whitelist logic would go here:
        if (isWhitelistMode) {
            // Only route certain IPs or use a specific proxy
            // For now, let's assume we use the FIRST VLESS/AWG as primary
        }

        route.put("rules", rules)
        route.put("auto_detect_interface", true)
        root.put("route", route)

        return root.toString(4)
    }
}
