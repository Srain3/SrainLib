package com.github.srain3.plugin.lib

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.forEach

/**
 * いろいろな便利機能
 */
object ToolBox {
    /**
     * JavaPluginクラス(Main)
     */
    val plugin by lazy { getPlugin(SrainLib::class.java) }

    /**
     * 特定の文字(デフォ:&)を§へ変換
     */
    fun String.color(char: Char = '&'): String {
        return ChatColor.translateAlternateColorCodes(char, this)
    }

    /**
     * 特定の文字(デフォ:&)を§へ変換(List用)
     */
    fun List<String>.color(char: Char = '&'): List<String> {
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
            meta.addEnchant(Enchantment.INFINITY, 0, true)
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
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

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
        skullMeta.owningPlayer = Bukkit.getOfflinePlayer(uuid)
        headItem.itemMeta = skullMeta
        return headItem
    }

    /**
     * 方角の文字列からYawを取得する
     */
    fun getYawNEWS(str: String): Double {
        return when (str.lowercase()) {
            "n" -> { -180.0 }
            "nne" -> { -157.5 }
            "ne" -> { -135.0 }
            "ene" -> { -112.5 }
            "e" -> { -90.0 }
            "ese" -> { -67.5 }
            "se" -> { -45.0 }
            "sse" -> { -22.5 }
            "s" -> { 0.0 }
            "ssw" -> { 22.5 }
            "sw" -> { 45.0 }
            "wsw" -> { 67.5 }
            "w" -> { 90.0 }
            "wnw" -> { 112.5 }
            "nw" -> { 135.0 }
            "nnw" -> { 157.5 }
            else -> { if (str.toDoubleOrNull() != null) {str.toDouble()} else {0.0} }
        }
    }

    /**
     * listに対してlocを中心にxyzの箱の中に入っている物のみListで返す
     */
    fun getNearbyEntitys(list: List<Entity>, loc: Location, x: Double, y: Double, z:Double): List<Entity> {
        val aabb = BoundingBox.of(loc, x, y, z)
        val reList = mutableListOf<Entity>()
        list.forEach { entity ->
            if (entity.world.uid == loc.world?.uid) {
                if (aabb.contains(entity.location.x, entity.location.y, entity.location.z)) {
                    reList.add(entity)
                }
            }
        }
        return reList
    }

    /**
     * listにあるEntityに対してのみレイトレースを試みてhitしたらtrueを返す。Distanceが0.0以下だとnullを返す。
     */
    fun rayTraceEntities(
        start: Location,
        direction: Vector,
        maxDistance: Double,
        raySize: Double,
        searchEntityList: List<Entity>
    ): Boolean? {
        if (maxDistance < 0.0) {
            return null
        } else {
            val startPos = start.toVector()
            val var17: Iterator<*> = searchEntityList.iterator()
            var hit = false

            while (var17.hasNext()) {
                val entity = var17.next() as Entity
                val boundingBox = entity.boundingBox.expand(raySize)
                val hitResult = boundingBox.rayTrace(startPos, direction, maxDistance)
                if (hitResult != null) {
                    hit = true
                    break
                }
            }

            return hit
        }
    }
}