package com.github.srain3.plugin.lib.util

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.UUID

object Util {
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
    fun newsStringToYaw(str: String): Double {
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
}