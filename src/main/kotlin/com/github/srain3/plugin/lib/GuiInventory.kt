package com.github.srain3.plugin.lib

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * GUIとして使うインベントリを作成する。自動でそのインベントリ内で起こるクリックイベントをキャンセルする。
 */
abstract class GuiInventory: Listener {

    /**
     * インベントリ(GUI)を作成して返す
     * @param type インベントリのタイプ
     * @param title インベントリのタイトル
     */
    fun createInventory(type: InventoryType,title: String): Inventory {
        val inv = Bukkit.createInventory(null, type, title)
        invList.add(inv)
        return inv
    }

    /**
     * インベントリ(GUI)を作成して返す
     * @param line 行数(1～6)
     * @param title インベントリのタイトル
     */
    fun createInventory(line: Int,title: String): Inventory {
        val inv = Bukkit.createInventory(null, 9*line, title)
        invList.add(inv)
        return inv
    }

    /**
     * インベントリイベント判定用
     */
    private val invList = mutableSetOf<Inventory>()

    /**
     * Inventoryシングルクリックのキャンセル用
     */
    @EventHandler
    fun inventoryClickEvents(e: InventoryClickEvent) {
        // invListに存在しないインベントリのイベントの場合return
        if (!invList.contains(e.view.topInventory)) return
        //Tools.plugin.logger.info("debug: invListにあるInventoryのClickEvent発生")
        e.isCancelled = true
        GuiItem.clickItemToRun(e)
    }

    /**
     * Inventoryドラッグのキャンセル用
     */
    @EventHandler
    fun inventoryDragEvents(e: InventoryDragEvent) {
        // invListに存在しないインベントリのイベントの場合return
        if (!invList.contains(e.view.topInventory)) return
        //Tools.plugin.logger.info("debug: invListにあるInventoryのDragEvent発生")
        e.isCancelled = true
    }

    /**
     * Inventory閉じた時に判定用キャッシュから消す用
     */
    @EventHandler
    fun inventoryCloseEvent(e: InventoryCloseEvent) {
        // invListに存在しないインベントリのイベントの場合return
        if (!invList.contains(e.view.topInventory)) return
        //Tools.plugin.logger.info("debug: invListにあるInventoryのCloseEvent発生")
        invList.remove(e.view.topInventory)
    }

    /**
     * プラグイン無効化の時の処理
     */
    fun disableTask() {
        // invListにあるインベントリを見ているプレイヤーにインベントリを閉じさせる処理
        invList.toList().forEach { inv ->
            inv.viewers.toList().forEach { player ->
                player.closeInventory()
            }
        }
    }

    object GuiItem {
        //Long型の最小値～最大値のrange、GuiItemのランダムID用
        private val rangeLong = (Long.MIN_VALUE..Long.MAX_VALUE)
        //GuiItemのデータのKey用
        @Suppress("DEPRECATION")
        private val key : NamespacedKey = NamespacedKey("srainlib-${(UInt.MIN_VALUE..UInt.MAX_VALUE).random()}", "guiitemid")
        //GuiItemID別のクリック時Unit保存用
        private val cacheClick = mutableMapOf<Long, (InventoryClickEvent) -> Unit>()

        /**
         * Gui用のクリック処理を登録
         * @param run ここに処理したい内容
         */
        fun ItemStack.guiClickEvent(run: (InventoryClickEvent) -> Unit): ItemStack {
            val meta = this.itemMeta
            val idLong = rangeLong.random()
            meta?.persistentDataContainer?.set(key, PersistentDataType.LONG, idLong)
            this.itemMeta = meta
            cacheClick[idLong] = run
            return this
        }

        /**
         * ClickEventからクリックされたアイテムを取得してrunが存在すれば実行する
         */
        fun clickItemToRun(e: InventoryClickEvent) {
            val clickItem = e.currentItem ?: return
            val idLong = clickItem.itemMeta?.persistentDataContainer?.get(key, PersistentDataType.LONG) ?: return
            cacheClick[idLong]?.run { this(e) }
        }
    }
}