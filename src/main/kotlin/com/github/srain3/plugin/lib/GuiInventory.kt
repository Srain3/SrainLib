package com.github.srain3.plugin.lib

import com.github.srain3.plugin.lib.GuiInventory.GuiItem.guiClickEvent
import com.github.srain3.plugin.lib.SrainLib.Companion.plugin
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.random.Random

/**
 * GUIとして使うインベントリを作成する。
 */
object GuiInventory: Listener {

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

    /**
     * インベントリイベント判定用
     */
    private val invList = mutableMapOf<Inventory, Pair<Boolean, Boolean>>()

    @EventHandler
    fun inventoryInteractEvents(e: InventoryInteractEvent) {
        // invListに存在しないインベントリのイベントの場合return
        if (!invList.contains(e.view.topInventory)) return
        GuiItem.itemToRun(e)
        // invListの2つ目の要素がtrueの場合は下部インベントリの操作の場合キャンセルする
        if (invList[e.view.topInventory]?.second ?: true) {
            when(e) {
                is InventoryClickEvent -> {
                    if (e.clickedInventory?.type == InventoryType.PLAYER) e.isCancelled = true
                }
                is InventoryDragEvent -> {
                    if (e.inventory.type == InventoryType.PLAYER) e.isCancelled = true
                }
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

    object GuiItem {
        //GuiItemのデータのKey用
        private val clickKey : NamespacedKey = NamespacedKey(plugin, "GuiItemClick")
        private val dragKey : NamespacedKey = NamespacedKey(plugin, "GuiItemDrag")
        //GuiItemID別のUnit保存用
        private val cacheClick = mutableMapOf<Long, Pair<Boolean, (InventoryClickEvent) -> Unit>>()
        private val cacheDrag = mutableMapOf<Long, Pair<Boolean, (InventoryDragEvent) -> Unit>>()

        /**
         * Gui用のクリック処理を登録
         * @param cansel クリックをキャンセル扱いするか
         * @param run ここに処理したい内容
         */
        fun ItemStack.guiClickEvent(
            cansel: Boolean = true,
            run: (InventoryClickEvent) -> Unit = {}
        ): ItemStack {
            val meta = this.itemMeta
            val idLong = Random.nextLong()
            meta?.persistentDataContainer?.set(clickKey, PersistentDataType.LONG, idLong)
            this.itemMeta = meta
            cacheClick[idLong] = Pair(cansel, run)
            return this
        }

        /**
         * Gui用のドラック処理を登録
         * @param cansel クリックをキャンセル扱いするか
         * @param run ここに処理したい内容
         */
        fun ItemStack.guiDragEvent(
            cansel: Boolean = true,
            run: (InventoryDragEvent) -> Unit = {}
        ): ItemStack {
            val meta = this.itemMeta
            val idLong = Random.nextLong()
            meta?.persistentDataContainer?.set(dragKey, PersistentDataType.LONG, idLong)
            this.itemMeta = meta
            cacheDrag[idLong] = Pair(cansel, run)
            return this
        }

        /**
         * InteractEventから操作アイテムを取得してrunが存在すれば実行する
         */
        fun itemToRun(e: InventoryInteractEvent) {
            when(e) {
                is InventoryClickEvent -> {
                    val clickItem = e.currentItem ?: return
                    val idLong = clickItem.itemMeta?.persistentDataContainer?.get(clickKey, PersistentDataType.LONG) ?: return
                    cacheClick[idLong]?.run {
                        if (this.first) e.isCancelled = true
                        this.second(e)
                    }
                }
                is InventoryDragEvent -> {
                    val dragItem = e.cursor ?: return
                    val idLong = dragItem.itemMeta?.persistentDataContainer?.get(dragKey, PersistentDataType.LONG) ?: return
                    cacheDrag[idLong]?.run {
                        if (this.first) e.isCancelled = true
                        this.second(e)
                    }
                }
                else -> return
            }
        }
    }

    class GuiPage(
        val posX: Int,
        val posY: Int,
        val x: Int,
        val y: Int,
        private val itemList: MutableMap<Int, MutableList<ItemStack?>> = mutableMapOf(),
    ) {
        private var nowPage: Int = 0

        init {
            newPage()
        }

        private fun newPage(index: Int = itemList.size) {
            val list = mutableListOf<ItemStack?>()
            repeat(x * y) {
                list.add(null)
            }
            itemList[index] = list
        }

        /**
         * 現在のページを取得する(1~)
         * @return 現在のページ番号
         */
        fun getNowPage(): Int {
            return nowPage+1
        }

        /**
         * 最大ページ数を取得する(size)
         * @return 最大ページ数
         */
        fun getMaxPage(): Int {
            return itemList.size
        }

        /**
         * 指定ページに有るアイテムリストをクローンして返す(=編集不可能) 指定ページがない場合nullを返す
         * @return 指定ページのアイテムリスト
         */
        fun getItemList(index: Int): List<ItemStack?>? {
            val list = mutableListOf<ItemStack?>()
            itemList[index]?.forEach { item ->
                list.add(item?.clone())
            } ?: return null
            return list
        }

        /**
         * 全アイテムリストをクローンして返す(=編集不可能)
         * @return 指定ページのアイテムリスト
         */
        fun getItemList(): Map<Int, List<ItemStack?>> {
            val map = mutableMapOf<Int, List<ItemStack?>>()
            repeat(getMaxPage()) {
                map[it] = getItemList(it) ?: return@repeat
            }
            return map
        }

        /**
         * アイテムを追加する、失敗するとnullを返す
         * @param item 追加するアイテム
         * @param index 指定ページ番号(0~)、nullの場合は先頭の空きに追加or新規ページ作成して追加
         * @param xy 指定ページのSlot番号(0~)、nullの場合は先頭の空きに追加
         * @param replace 既にアイテムがある場合上書きするか
         * @return 追加したアイテム、失敗した場合null
         */
        fun addItem(item: ItemStack, index: Int? = null, xy: Int? = null, replace: Boolean = false): ItemStack? {
            if (index == null) {
                val result = firstEmpty()
                if (result.second == null) {
                    newPage(result.first)
                    return addItem(item, result.first, 0)
                } else {
                    itemList[result.first]!![result.second!!] = item
                    return item
                }
            } else {
                if (itemList[index] == null) return null
                if (xy == null) {
                    val result = firstEmptyPage(index)
                    if (result == null) {
                        return null
                    } else {
                        itemList[index]!![result] = item
                        return item
                    }
                } else {
                    if (itemList[index]!![xy] != null && !replace) return null
                    itemList[index]!![xy] = item
                    return item
                }
            }
        }

        /**
         * 最初に空いているページ場所を返す、新規ページが必要な場合secondはnullを返す
         * @return Pair(first:ページ番号, second:空いている場所)
         */
        private fun firstEmpty(index: Int = 0): Pair<Int, Int?> {
            val list = itemList[index] ?: return Pair(index, null)
            list.forEachIndexed { i, item ->
                if (item == null) {
                    return Pair(index, i)
                }
            }
            return firstEmpty(index+1)
        }

        /**
         * 指定ページの空きSlotを返す、新規ページが必要な場合nullを返す
         * @return 空いている場所
         */
        private fun firstEmptyPage(index: Int): Int? {
            val list = itemList[index] ?: return null
            list.forEachIndexed { i, item ->
                if (item == null) {
                    return i
                }
            }
            return null
        }
    }
}