package com.github.srain3.plugin.lib.util.command

import org.bukkit.command.CommandSender
import java.util.*

interface Args {
    val subCmds: MutableMap<String?, SubCmd>

    /**
     * 引数のコマンド追加、nullは引数自由なコマンドになる
     */
    fun addArg(
        arg: String?,
        runCommand: (CommandSender, Cmd, String, Array<out String>) -> Boolean
    ): SubCmd {
        val i = if (this is SubCmd) {
            this.index + 1
        } else {
            0
        }
        val subCmd = SubCmd(arg, i, runCommand)
        subCmds[arg] = subCmd
        return subCmd
    }

    /**
     * 引数のコマンドを一括追加
     */
    fun addArg(
        arg: List<String>,
        runCommand: (CommandSender, Cmd, String, Array<out String>) -> Boolean
    ): List<SubCmd> {
        val i = if (this is SubCmd) {
            this.index + 1
        } else {
            0
        }
        val list = mutableListOf<SubCmd>()
        for (a in arg) {
            val subCmd = SubCmd(a, i, runCommand)
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
        return subCmds.keys
            .filterNot { it == null }.filter { regex.containsMatchIn(it ?: " ") }
            .toSortedSet(compareBy { it ?: "" })
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