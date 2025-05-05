package com.github.srain3.plugin.lib.util.gui

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.inventory.Inventory

object GuiInventory: Listener {
    private val invList = mutableMapOf<Inventory, Pair<Boolean, Boolean>>()

    /**
     * インベントリ(GUI)を作成して返す
     * @param type インベントリのタイプ
     * @param title インベントリのタイトル
     * @param keep trueの場合はインベントリを閉じた時にキャッシュから消さない
     * @param lock trueの場合は下部インベントリの操作をキャンセルする
     */
    fun createInventory(type: InventoryType,title: String,keep: Boolean = false,lock: Boolean = true): Inventory {
        val inv = Bukkit.createInventory(null, type, title)
        invList[inv] = Pair(keep, lock)
        return inv
    }

    /**
     * インベントリ(GUI)を作成して返す
     * @param line 行数(1～6)
     * @param title インベントリのタイトル
     * @param keep trueの場合はインベントリを閉じた時にキャッシュから消さない
     * @param lock trueの場合は下部インベントリの操作をキャンセルする
     */
    fun createInventory(line: Int,title: String,keep: Boolean = false,lock: Boolean = true): Inventory {
        val inv = Bukkit.createInventory(null, 9*line, title)
        invList[inv] = Pair(keep, lock)
        return inv
    }

    @EventHandler
    fun interactEvent(e: InventoryInteractEvent) {
        // invListに存在しないインベントリのイベントの場合return
        if (!invList.contains(e.view.topInventory)) return
        when(e) {
            is InventoryClickEvent -> {
                GuiItem.itemToRun(e)
                if (invList[e.view.topInventory]?.second == true &&
                    e.clickedInventory?.type == InventoryType.PLAYER) e.isCancelled = true
            }
            is InventoryDragEvent -> {
                e.isCancelled = true
            }
        }
    }

    /**
     * Inventory閉じた時に判定用キャッシュから消す用
     */
    @EventHandler
    fun inventoryCloseEvent(e: InventoryCloseEvent) {
        // invListに存在しないインベントリのイベントの場合return
        // keepがtrueの場合はキャッシュから消さない(return)
        if (invList[e.view.topInventory]?.first ?: true) return
        invList.remove(e.view.topInventory)
    }

    /**
     * プラグイン無効化の時の処理
     */
    fun disableTask() {
        // invListにあるインベントリを見ているプレイヤーにインベントリを閉じさせる処理
        invList.keys.toList().forEach { inv ->
            inv.viewers.toList().forEach { player ->
                player.closeInventory()
            }
        }
    }
}