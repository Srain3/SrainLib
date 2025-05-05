package com.github.srain3.plugin.lib.util.file

import com.github.srain3.plugin.lib.SrainLib
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.util.logging.Level

/**
 * [javaPlugin]のデータフォルダ内に自由なファイル名で管理できる[org.bukkit.configuration.file.YamlConfiguration]の拡張版
 * @param isSaveBeforeLoading (Option)最初に[saveDefault]を行うかどうか、デフォ:false=ファイルセーブを行わずにロードする
 * @param fromJar (Option)[saveDefault]の時[javaPlugin]内resourcesフォルダから同名ファイルをペーストするかどうか
 * @param fromJarReplace (Option)[saveDefault]の時[fromJar]がtrueの場合、データフォルダ内に同名ファイルが存在していても上書き保存するかどうか
 */
class YamlConfig (
    private val fileName: String,
    private val javaPlugin: JavaPlugin = SrainLib.Companion.plugin,
    isSaveBeforeLoading: Boolean = false,
    private val fromJar: Boolean = false,
    private val fromJarReplace: Boolean = false
) : YamlConfiguration() {
    private val file = File(javaPlugin.dataFolder, fileName)

    init {
        if (isSaveBeforeLoading) {
            saveDefault()
        }
        reload()
    }

    /**
     * ファイルがない場合に[fromJar]がtrueの場合jarからファイルをペースト、[fromJar]がfalseの場合空ファイル生成、[fromJar]と[fromJarReplace]が両方trueの場合上書きペーストする。
     */
    fun saveDefault() {
        val dataFolder = javaPlugin.dataFolder
        if (!dataFolder.exists() || !dataFolder.isDirectory) {
            dataFolder.mkdir()
        }
        if (!file.exists()) {
            if (fromJar) {
                javaPlugin.saveResource(fileName, false)
            } else {
                save()
            }
        } else if (fromJarReplace && fromJar) {
            javaPlugin.saveResource(fileName, true)
        }
    }

    /**
     * ファイルへ保存する
     */
    fun save() {
        try {
            this.save(file)
        } catch (ex: IOException) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save config", ex)
        }
    }

    /**
     * ファイルをロードする
     */
    fun reload() {
        this.load(file)
    }

    /**
     * ファイルを消去する。消去できたらtrueを返す
     */
    fun delete(): Boolean {
        if (file.exists()) {//存在するファイルの場合
            return file.delete()
        }
        return false
    }
}