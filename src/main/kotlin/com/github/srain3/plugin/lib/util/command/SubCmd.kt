package com.github.srain3.plugin.lib.util.command

import org.bukkit.command.CommandSender
import java.util.SortedSet

/**
 * 引数のコマンド、nullは引数自由なコマンド。
 * @param run0 「実行者,コマンド,入力コマンド名,引数リスト」->「実行内容」
 */
class SubCmd(
    val arg: String?,
    val index: Int,
    private val run0: (CommandSender, Cmd, String, Array<out String>) -> Boolean = { _, _, _, _ -> false }
) {
    // Cmd.execute()から初めは呼ばれ、引数が続く限り入れ子構造で呼び続け最後の引数で[run0]を実行する
    fun run(sender: CommandSender, command: Cmd, label: String, args: Array<out String>): Boolean {
        if (args.size <= index + 1) {
            return run0(sender, command, label, args)
        }
        val subCmd = subCmds[args[index + 1]] ?: subCmds[null] ?: run {
            CmdManager.errorCommandMsg(sender, label, args, index + 2)
            return false
        }
        return subCmd.run(sender, command, label, args)
    }

    val subCmds = mutableMapOf<String?, SubCmd>()

    /**
     * 引数のコマンドを追加、nullは引数自由なコマンドになる
     */
    fun addArg(
        arg: String?,
        run: (CommandSender, Cmd, String, Array<out String>) -> Boolean = { _, _, _, _ -> false }
    ): SubCmd {
        val subCmd = SubCmd(arg, index + 1, run)
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
            val subCmd = SubCmd(a, index + 1, run)
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