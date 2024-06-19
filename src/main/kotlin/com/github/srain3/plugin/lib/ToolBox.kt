package com.github.srain3.plugin.lib

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * いろいろな便利機能
 */
object ToolBox {
    /**
     * &を§へ変換
     */
    fun String.color(): String {
        return ChatColor.translateAlternateColorCodes('&', this)
    }

    /**
     * [char]を§へ変換
     */
    fun String.color(char: Char): String {
        return ChatColor.translateAlternateColorCodes(char, this)
    }

    /**
     * &を§へ変換
     */
    fun List<String>.color(): List<String> {
        return this.map { it.color() }
    }

    /**
     * [char]を§へ変換
     */
    fun List<String>.color(char: Char): List<String> {
        return this.map { it.color(char) }
    }

    /**
     * §(カラーコード)を除去
     */
    fun String.unColor(): String {
        return ChatColor.stripColor(this) ?: this
    }

    /**
     * ItemStackに表示名と説明を追加する(自動カラー化付き)
     */
    fun ItemStack.addText(title: String?, lore: MutableList<String>): ItemStack {
        val meta = this.itemMeta ?: return this
        meta.setDisplayName(title?.color())
        meta.lore = lore.map { it.color() }
        this.itemMeta = meta
        return this
    }

    /**
     * ItemStackの非表示フラグをすべて追加する
     */
    fun ItemStack.allHide(): ItemStack {
        val meta = this.itemMeta ?: return this
        ItemFlag.entries.forEach { flag ->
            meta.addItemFlags(flag)
        }
        this.itemMeta = meta
        return this
    }

    /**
     * ItemStackの見た目だけエンチャントを付与する(エンチャ隠すフラグ自動付与)
     */
    fun ItemStack.fakeEnchant(): ItemStack {
        val meta = this.itemMeta ?: return this
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        if (meta.hasEnchants()) {
            this.itemMeta = meta
            return this
        }
        if (this.type == Material.BOW || this.type == Material.CROSSBOW) {
            meta.addEnchant(Enchantment.LURE, 0, true)
        } else {
            meta.addEnchant(Enchantment.ARROW_INFINITE, 0, true)
        }
        this.itemMeta = meta
        return this
    }

    /**
     * プラグイン用フォルダからnameのディレクトリ内にあるファイルを返す
     */
    fun getFolderToFile(plugin: JavaPlugin, name: String): List<File>? {
        val file = File(plugin.dataFolder, name)
        return if (file.isDirectory) {
            file.listFiles()?.filter { it.isFile }
        } else {
            null
        }
    }

    /**
     * 指定されたディレクトリ内にあるファイルを返す
     */
    fun getFolderToFile(file: File): List<File>? {
        return if (file.isDirectory) {
            file.listFiles()?.filter { it.isFile }
        } else {
            null
        }
    }

    /**
     * プラグイン用フォルダからnameのディレクトリ内にあるディレクトリを返す
     */
    fun getFolderToFolder(plugin: JavaPlugin, name: String): List<File>? {
        val file = File(plugin.dataFolder, name)
        return if (file.isDirectory) {
            file.listFiles()?.filter { it.isDirectory }
        } else {
            null
        }
    }

    /**
     * 指定されたディレクトリ内にあるディレクトリを返す
     */
    fun getFolderToFolder(file: File): List<File>? {
        return if (file.isDirectory) {
            file.listFiles()?.filter { it.isDirectory }
        } else {
            null
        }
    }

    /**
     * [LocalDateTime]を文字列に変える時のフォーマットパターン
     */
    private val formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")

    /**
     * 「2024-02-20 11:15:20」のような文字列に変換する
     */
    fun LocalDateTime.toFormat(): String {
        return this.format(formatter)
    }

    /**
     * uuidからスキンを適応したプレイヤーヘッドを返す
     */
    fun getPlayerHead(uuid: UUID): ItemStack {
        val headItem = ItemStack(Material.PLAYER_HEAD)
        val skullMeta = headItem.itemMeta as SkullMeta
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid))
        headItem.itemMeta = skullMeta
        return headItem
    }

}