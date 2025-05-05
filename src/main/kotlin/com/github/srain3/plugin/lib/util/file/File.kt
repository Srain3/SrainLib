package com.github.srain3.plugin.lib.util.file

import com.github.srain3.plugin.lib.SrainLib
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object File {
    /**
     * プラグイン用フォルダからnameのディレクトリ内にあるファイルを返す
     */
    fun getFolderToFile(name: String, pl: JavaPlugin = SrainLib.Companion.plugin): List<File>? {
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
    fun getFolderToFile(file: File): List<File>? {
        return if (file.isDirectory) {
            file.listFiles()?.filter { it.isFile }
        } else {
            null
        }
    }

    /**
     * プラグイン用フォルダからnameのディレクトリ内にあるディレクトリを返す
     */
    fun getFolderToFolder(name: String, pl: JavaPlugin = SrainLib.Companion.plugin): List<File>? {
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
    fun getFolderToFolder(file: File): List<File>? {
        return if (file.isDirectory) {
            file.listFiles()?.filter { it.isDirectory }
        } else {
            null
        }
    }
}