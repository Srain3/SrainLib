package com.github.srain3.plugin.lib.util.command

import org.bukkit.command.CommandSender

/**
 * 引数のコマンド、nullは引数自由なコマンド。
 * @param runCommand 「実行者,コマンド,入力コマンド名,引数リスト」->「実行内容」
 */
class SubCmd(
    val arg: String?,
    val index: Int,
    private val runCommand: (CommandSender, Cmd, String, Array<out String>) -> Boolean
): Args {
    override val subCmds: MutableMap<String?, SubCmd> = mutableMapOf()

    // Cmd.execute()から初めは呼ばれ、引数が続く限り入れ子構造で呼び続け最後の引数で[runCommand]を実行する
    fun run(sender: CommandSender, command: Cmd, label: String, args: Array<out String>): Boolean {
        if (args.size <= index + 1) {
            return runCommand(sender, command, label, args)
        }
        val subCmd = subCmds[args[index + 1]] ?: subCmds[null] ?: run {
            CmdManager.errorCommandMsg(sender, label, args, index + 2)
            return false
        }
        return subCmd.run(sender, command, label, args)
    }
}