package com.github.srain3.plugin.lib.util.file

import com.github.srain3.plugin.lib.SrainLib
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object File {
    /**
     * プラグイン用フォルダからnameのディレクトリ内にあるファイルを返す
     */
    fun getFilesFromFolder(name: String, pl: JavaPlugin = SrainLib.getPlugin()): List<File>? {
        val file = File(pl.dataFolder, name)
        return if (file.isDirectory) {
            file.listFiles()?.filter { it.isFile }
        } else {
            null
        }
    }

    /**
     * 指定されたディレクトリ内にあるファイルを返す
     */
    fun getFilesFromFolder(file: File): List<File>? {
        return if (file.isDirectory) {
            file.listFiles()?.filter { it.isFile }
        } else {
            null
        }
    }

    /**
     * プラグイン用フォルダからnameのディレクトリ内にあるディレクトリを返す
     */
    fun getFoldersFromFolder(name: String, pl: JavaPlugin = SrainLib.getPlugin()): List<File>? {
        val file = File(pl.dataFolder, name)
        return if (file.isDirectory) {
            file.listFiles()?.filter { it.isDirectory }
        } else {
            null
        }
    }

    /**
     * 指定されたディレクトリ内にあるディレクトリを返す
     */
    fun getFoldersFromFolder(file: File): List<File>? {
        return if (file.isDirectory) {
            file.listFiles()?.filter { it.isDirectory }
        } else {
            null
        }
    }
}