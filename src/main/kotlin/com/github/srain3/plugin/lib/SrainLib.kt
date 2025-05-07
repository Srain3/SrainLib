package com.github.srain3.plugin.lib

import com.github.srain3.plugin.lib.util.command.CmdManager
import com.github.srain3.plugin.lib.util.gui.GuiInventory
import org.bukkit.Bukkit
import org.bukkit.command.SimpleCommandMap
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.Field

/**
 * SrainLibのメインクラス
 * 使用するPluginはこのクラスのonEnable()とonDisable()をしっかり実行してください。
 */
object SrainLib {
    private var plugin: JavaPlugin? = null
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

    fun getPlugin(): JavaPlugin {
        return plugin ?: throw NullPointerException("Plugin is not set. Please call onEnable() before using this.")
    }

    /**
     * ライブラリの起動
     * @param plugin 使用プラグイン
     */
    fun onEnable(plugin: JavaPlugin) {
        this.plugin = plugin
        plugin.server.pluginManager.registerEvents(GuiInventory, plugin)
    }

    /**
     * ライブラリの終了
     */
    fun onDisable() {
        CmdManager.unregisterAll()
        GuiInventory.disableTask()
    }
}