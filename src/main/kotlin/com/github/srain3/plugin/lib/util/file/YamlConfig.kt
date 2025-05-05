package com.github.srain3.plugin.lib.util.file

import com.github.srain3.plugin.lib.SrainLib
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.util.logging.Level

/**
 * データフォルダ内に自由なファイル名で管理できる
 * @param fromJar リソースフォルダからのファイルを保存するか/上書きペーストするかどうか
 */
class YamlConfig (
    private val fileName: String,
    private val javaPlugin: JavaPlugin = SrainLib.Companion.plugin,
    private val fromJar: Pair<Boolean, Boolean> = Pair(true, false)
) : YamlConfiguration() {
    private val file = File(javaPlugin.dataFolder, fileName)

    init {
        reload()
    }

    /**
     * ファイルがない場合に[fromJar]がtrueの場合jarからファイルをペースト、[fromJar]が両方trueの場合上書きペーストする。
     */
    fun saveDefault() {
        val dataFolder = javaPlugin.dataFolder
        if (!dataFolder.exists() || !dataFolder.isDirectory) {
            dataFolder.mkdir()
        }
        if (!file.exists()) {
            if (fromJar.first) {
                javaPlugin.saveResource(fileName, false)
            } else {
                save()
            }
        } else if (fromJar.first && fromJar.second) {
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