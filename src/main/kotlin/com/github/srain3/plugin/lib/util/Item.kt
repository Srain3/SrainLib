package com.github.srain3.plugin.lib.util

import com.github.srain3.plugin.lib.util.Text.color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object Item {
    /**
     * ItemStackに表示名と説明を追加する(自動カラー化付き)
     */
    fun ItemStack.text(title: String?, lore: List<String>?): ItemStack {
        val meta = this.itemMeta ?: return this
        meta.setDisplayName(title?.color())
        meta.lore = lore?.color()
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
}