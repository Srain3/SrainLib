package com.github.srain3.plugin.lib.util.command

import com.github.srain3.plugin.lib.SrainLib.Companion.commandMap
import com.github.srain3.plugin.lib.SrainLib.Companion.plugin
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import java.lang.reflect.Field
import java.util.*

/**
 * 始まりのコマンド
 * @param run0 「実行者,コマンド,入力コマンド名,引数リスト」->「実行内容」
 */
class Cmd(
    name: String,
    description: String = "",
    usageMessage: String = "/$name",
    aliases: List<String> = listOf(),
    permissionDefault: PermissionDefault = PermissionDefault.TRUE,
    val permission0: Permission = Permission("${plugin.name}.$name.use", "\"/$name\" command permission.", permissionDefault),
    private val run0: (CommandSender, Cmd, String, Array<out String>) -> Boolean = { _, _, _, _ -> false }
): Command(
    name,
    description,
    usageMessage,
    aliases
) {
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

    // サブコマンド(引数)
    private val subCmds = mutableMapOf<String?, SubCmd>()

    // コマンドが一番最初に実行される箇所
    // 引数がない場合は[run0]を実行
    override fun execute(
        sender: CommandSender, label: String, args: Array<out String>
    ): Boolean {
        if (!testPermission(sender)) return false
        if (args.isEmpty()) return run0(sender, this, label, args)
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

    /**
     * 引数のコマンド追加、nullは引数自由なコマンドになる
     */
    fun addArg(
        arg: String?,
        run: (CommandSender, Cmd, String, Array<out String>) -> Boolean = { _, _, _, _ -> false }
    ): SubCmd {
        val subCmd = SubCmd(arg, 0, run)
        subCmds[arg] = subCmd
        return subCmd
    }

    /**
     * 引数のコマンドを一括追加
     */
    fun addArg(
        arg: List<String>,
        run: (CommandSender, Cmd, String, Array<out String>) -> Boolean = { _, _, _, _ -> false }
    ): List<SubCmd> {
        val list = mutableListOf<SubCmd>()
        for (a in arg) {
            val subCmd = SubCmd(a, 0, run)
            subCmds[a] = subCmd
            list.add(subCmd)
        }
        return list
    }

    /**
     * このコマンドの一個後ろの引数リスト(String)を取得
     * @param filter マッチング文字列
     */
    fun getNextArg(filter: String? = null): SortedSet<String?> {
        val regex = (filter ?: "").toRegex(RegexOption.LITERAL)
        return subCmds.keys.filterNot { it == null }.filter { regex.containsMatchIn(it ?: " ") }.toSortedSet(compareBy { it ?: "" })
    }

    /**
     * このコマンドの一個後ろの引数リスト(SubCmd)を取得
     * @param arg マッチング文字列
     */
    fun getArg(arg: String? = null): List<SubCmd> {
        if (arg == null) return subCmds.values.toList()
        return subCmds.filter { it.key == arg }.values.toList()
    }

    /**
     * 引数のコマンドを削除
     */
    fun delete(arg: String?): SubCmd? {
        return subCmds.remove(arg)
    }
}