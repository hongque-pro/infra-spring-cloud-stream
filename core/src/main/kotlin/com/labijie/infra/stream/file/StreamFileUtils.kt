package com.labijie.infra.stream.file

import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.RandomAccessFile
import java.util.concurrent.ConcurrentHashMap


/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-27 21:20
 * @Description:
 */

internal object StreamFileUtils {
    private val fileSync = ConcurrentHashMap<String, Any>()

    private val invalidSeparator
        get() = if (File.separator == "\\") "/" else "\\"

    val defaultFolder: String
        get() {
            val userDir = System.getProperty("user.home")
            return File(userDir, ".spring-cloud-stream").absolutePath
        }

    fun getDestinationFile(folder: String, destination: String): String {
        val dir = folder.ifBlank { defaultFolder }
        val fileName = destination.lowercase() + ".stream"

        val f = dir.replace(invalidSeparator, File.separator).trimEnd(File.separatorChar)
        if (f.isNotBlank()) {
            return "$f${File.separator}$fileName"
        }
        return fileName
    }

    fun truncateFile(file: String) {
        val sync = fileSync.getOrPut(file) { Any() }
        try {
            synchronized(sync) { //防止 producer 和 consumer 在同一个进程
                RandomAccessFile(file, "rw").use {
                    val lock = it.channel.lock(0L, Long.MAX_VALUE, true)
                    try {
                        it.setLength(0)
                    } finally {
                        lock.release()
                    }
                }
            }
        } catch (_: FileNotFoundException) {
            val f = File(file)
            FileUtils.createParentDirectories(f)
            f.createNewFile()
            return truncateFile(file)
        }
    }
}
