package com.github.srain3.plugin.lib

import com.github.srain3.plugin.lib.util.command.CmdManager
import com.github.srain3.plugin.lib.util.gui.GuiInventory
import org.bukkit.Bukkit
import org.bukkit.command.SimpleCommandMap
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.Field

class SrainLib: JavaPlugin() {
    companion object {
        val plugin by lazy { getPlugin(SrainLib::class.java) }
        val commandMap by lazy {
            try {
                val bukkitCommandMap: Field = Bukkit.getServer().javaClass.getDeclaredField("commandMap")

                bukkitCommandMap.setAccessible(true)
                val commandMap = bukkitCommandMap.get(Bukkit.getServer()) as SimpleCommandMap

                return@lazy commandMap
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@lazy null
        }
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(GuiInventory, this)

        TestObj.testRun()
    }

    override fun onDisable() {
        CmdManager.unregisterAll()
        GuiInventory.disableTask()
    }
}