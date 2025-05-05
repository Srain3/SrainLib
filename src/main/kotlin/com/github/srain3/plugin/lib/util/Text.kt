package com.github.srain3.plugin.lib.util

import org.bukkit.ChatColor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Text {
    fun String.color(char: Char = '&'): String =
        ChatColor.translateAlternateColorCodes(char, this)

    fun List<String>.color(char: Char = '&'): List<String> = this.map { it.color(char) }

    fun String.unColor(): String = ChatColor.stripColor(this) ?: this

    /**
     * [LocalDateTime]を文字列に変える時のフォーマットパターン
     */
    private val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    /**
     * 「2024-02-20 11:15:20」のような文字列に変換する
     */
    fun LocalDateTime.toFormat(): String = this.format(timeFormatter)
}