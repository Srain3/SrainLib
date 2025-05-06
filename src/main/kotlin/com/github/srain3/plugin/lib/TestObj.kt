package com.github.srain3.plugin.lib

import com.github.srain3.plugin.lib.util.Item.text
import com.github.srain3.plugin.lib.util.command.Cmd
import com.github.srain3.plugin.lib.util.gui.GuiInventory
import com.github.srain3.plugin.lib.util.gui.GuiInventory.clickSound
import com.github.srain3.plugin.lib.util.gui.GuiInventory.setItem
import com.github.srain3.plugin.lib.util.gui.GuiItem.guiClickEvent
import com.github.srain3.plugin.lib.util.gui.GuiPagePanel
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.permissions.PermissionDefault

object TestObj {
    fun testRun() {
        Cmd("guitest", aliases = listOf("gui"), permissionDefault = PermissionDefault.OP) { sender, cmd, label, args ->
            if (sender !is Player) return@Cmd true
            GuiInventory.createInventory(6, "Test GUI").apply inv@{
                GuiPagePanel(1, 0, 7, 1).apply page@{
                    for (i in 0..7*5-1) {
                        addItem(ItemStack(randomItemMaterial()))
                            ?.text("&a横！ $i", listOf("&7横長版"))
                            ?.guiClickEvent { event ->
                                event.clickSound()
                            }
                    }
                    this@inv.setItem(8, 0, nextPageItem())
                    this@inv.setItem(0, 0, backPageItem())
                }.reflash(this)

                GuiPagePanel(1,1, 3, 5).apply page@{
                    for (i in 0..3 * 5 * 5 - 1) {
                        addItem(ItemStack(randomItemMaterial()))
                            ?.text("&a縦！ $i", listOf("&7縦長版"))
                            ?.guiClickEvent { event ->
                                event.clickSound()
                            }
                    }
                    this@inv.setItem(0, 5, nextPageItem(Material.GREEN_BANNER))
                    this@inv.setItem(0, 1, backPageItem(Material.GREEN_BANNER))
                }.reflash(this)

                sender.openInventory(this)
            }
            return@Cmd true
        }.apply {
            // コマンドツリー
            addArg("args[0]") { sender, _, _, args ->
                sender.sendMessage("args[0]: ${args[0]}")
                return@addArg true
            }.addArg(listOf("args[1]_1", "args[1]_2")) { sender, _, _, args ->
                sender.sendMessage("args[1]: ${args[1]}")
                return@addArg true
            }

            // こんなことも可能(リスト大量追加)
            addArg("material") { sender, cmd, label, args ->
                sender.sendMessage("Command:${cmd.name} label:$label input:/$label ${args.joinToString(" ")}")
                return@addArg true
            }.addArg(Material.entries.map { it.name }) { sender, cmd, label, args ->
                sender.sendMessage("Command:${cmd.name} label:$label input:/$label ${args.joinToString(" ")}")
                return@addArg true
            }

            // 自由引数のサンプル
            addArg("freeArgTest") { sender, cmd, label, args ->
                sender.sendMessage("Command:${cmd.name} label:$label input:/$label ${args.joinToString(" ")}")
                return@addArg true
            }.addArg(null) { sender, cmd, label, args ->
                sender.sendMessage("Command:${cmd.name} label:$label input:/$label ${args.joinToString(" ")}")
                return@addArg true
            }

            // 自由引数のサンプル(追加/削除をするver)
            val fatAdded = addArg("freeArgTestAdded") { sender, cmd, label, args ->
                sender.sendMessage("Command:${cmd.name} label:$label input:/$label ${args.joinToString(" ")}")
                return@addArg true
            }
            fatAdded.addArg(null) { sender, cmd, label, args ->
                fatAdded.addArg(args[1]) { sender, cmd, label, args ->
                    sender.sendMessage("${args[1]} is run!")
                    return@addArg true
                }.addArg("delete") { sender, cmd, label, args ->
                    fatAdded.delete(args[1])
                    sender.sendMessage("${args[1]} is deleted")
                    return@addArg true
                }
                sender.sendMessage("${args[1]} is added")
                return@addArg true
            }
        }

    }

    private fun randomItemMaterial(): Material {
        var material: Material = Material.entries.random()
        while (!material.isItem) {
            material = Material.entries.random()
        }
        return material
    }
}