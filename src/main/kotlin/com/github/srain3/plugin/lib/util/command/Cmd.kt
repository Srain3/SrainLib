package com.github.srain3.plugin.lib.util.command

import com.github.srain3.plugin.lib.SrainLib.commandMap
import com.github.srain3.plugin.lib.SrainLib.getPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.Field

/**
 * 始まりのコマンド
 * permissionは「plugin名.command.コマンド名.use」で登録される
 * @param runCommand 「実行者,コマンド,入力コマンド名,引数リスト」->「実行内容」
 */
class Cmd(
    name: String,
    description: String = "",
    usageMessage: String = "/$name",
    aliases: List<String> = listOf(),
    permissionDefault: PermissionDefault = PermissionDefault.TRUE,
    private val runCommand: (CommandSender, Cmd, String, Array<out String>) -> Boolean
): Command(
    name,
    description,
    usageMessage,
    aliases
), Args {
    override val subCmds: MutableMap<String?, SubCmd> = mutableMapOf()
    private val plugin: JavaPlugin = getPlugin()
    val permission0: Permission = Permission(
        "${plugin.name}.command.$name.use",
        "\"/$name\" command permission.",
        permissionDefault)

    init {
        plugin.server.pluginManager.addPermission(permission0)
        this.permission = permission0.name
        commandMap?.register(plugin.name, this@Cmd)
        CmdManager.setRegister(this)
    }

    fun unRegister(): Cmd {
        try {
            val knownCommandsField: Field = SimpleCommandMap::class.java.getDeclaredField("knownCommands")
            knownCommandsField.setAccessible(true)
            val knownCommands = knownCommandsField.get(commandMap) as MutableMap<*, *>
            val command = commandMap?.getCommand(this.name) ?: return this
            for (alias in command.aliases) knownCommands.remove(alias)

            knownCommands.remove(command.name)
            commandMap?.let { command.unregister(it) }
            knownCommandsField.setAccessible(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        CmdManager.unRegister(this)
        plugin.server.pluginManager.removePermission(permission0)
        return this
    }

    // コマンドが一番最初に実行される箇所
    // 引数がない場合は[runCommand]を実行
    override fun execute(
        sender: CommandSender, label: String, args: Array<out String>
    ): Boolean {
        if (!testPermission(sender)) return false
        if (args.isEmpty()) return runCommand(sender, this, label, args)
        if (subCmds[args[0]] != null) {
            return subCmds[args[0]]?.run(sender, this, label, args) ?: false
        }
        return subCmds[null]?.run(sender, this, label, args) ?: run {
            CmdManager.errorCommandMsg(sender, label, args, 1)
            false
        }
    }

    // Tab補完
    override fun tabComplete(
        sender: CommandSender, alias: String, args: Array<out String?>
    ): List<String?> {
        if (!testPermission(sender)) return emptyList()
        if (args.size <= 1) return getNextArg(args[0]).toList()
        var subCmd = subCmds[args[0]] ?: subCmds[null] ?: return emptyList()
        repeat(args.size - 1) {
            if (it < args.size - 2) {
                subCmd = subCmd.subCmds[args[it + 1]] ?: return emptyList()
            } else {
                return subCmd.getNextArg(args[it + 1]).toList()
            }
        }
        return emptyList()
    }
}