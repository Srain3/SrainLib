package com.github.srain3.plugin.lib.util.command

import org.bukkit.command.CommandSender

/**
 * コマンド管理用
 */
object CmdManager {
    private val commands = mutableMapOf<String, Cmd>()

    fun Cmd.setManager() {
        commands[this.name] = this
    }

    fun Cmd.removeManager() {
        commands.remove(this.name)
    }

    fun unRegisterByName(name: String): Cmd? {
        return commands[name]?.unRegister()
    }

    fun getCommand(name: String): Cmd? {
        return commands[name]
    }

    fun unregisterAll() {
        commands.values.toList().forEach { it.unRegister() }
        commands.clear()
    }

    fun errorCommandMsg(sender: CommandSender, label: String, args: Array<out String>, errorIndex: Int) {
        var msg = "§c§n/$label"
        repeat(errorIndex) {
            msg += " ${args[it]}"
        }
        msg += "§r§c§o← [error]"
        sender.sendMessage(msg)
    }

    fun notPermissionMsg(sender: CommandSender, label: String, args: Array<out String>, errorIndex: Int) {
        var msg = "§c§n/$label"
        repeat(errorIndex) {
            msg += " ${args[it]}"
        }
        msg += "§r§c§o← [not permission]"
        sender.sendMessage(msg)
    }
}