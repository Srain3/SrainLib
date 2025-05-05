package com.github.srain3.plugin.lib

import org.bukkit.plugin.java.JavaPlugin

class SrainLib: JavaPlugin() {
    companion object {
        val plugin by lazy { getPlugin(SrainLib::class.java) }
    }
}