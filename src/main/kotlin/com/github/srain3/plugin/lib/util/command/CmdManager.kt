package com.github.srain3.plugin.lib.util.command

import org.bukkit.command.CommandSender

/**
 * コマンド管理用
 */
object CmdManager {
    private val commands = mutableMapOf<String, Cmd>()

    fun setRegister(cmd: Cmd) {
        commands[cmd.name] = cmd
    }

    fun unRegister(cmd: Cmd) {
        commands.remove(cmd.name)
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
}