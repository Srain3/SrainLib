package com.github.srain3.plugin.lib

import com.github.srain3.plugin.lib.SrainLib.Companion.plugin
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap

class Cmd(
    name: String,
    description: String = "",
    usageMessage: String = "/$name",
    aliases: List<String> = listOf()
): Command(
    name,
    description,
    usageMessage,
    aliases
) {
    // コマンドが一番最初に実行される箇所
    override fun execute(
        sender: CommandSender,
        label: String,
        args: Array<out String>
    ): Boolean {
        sender.sendMessage("Test")
        return true
    }

    fun setRegister() {
        SimpleCommandMap(Bukkit.getServer()).register(plugin.name, this)
    }

    fun removeRegister() {
        this.unregister(SimpleCommandMap(Bukkit.getServer()))
    }

}