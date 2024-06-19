package com.github.srain3.plugin.lib

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.util.logging.Level

/**
 * [File]から管理できる[YamlConfiguration]の拡張版
 */
open class CustomYamlFile (
    private val file: File
) : YamlConfiguration() {

    init {
        reload()
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