package com.labijie.infra.stream.file

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

    fun getDestinationFile(folder: String, destination: String): String {
        val fileName = destination.toLowerCase() + ".stream"

        val f = folder.trimEnd(File.pathSeparatorChar)
        if(f.isNotBlank()) {
            return "$f${File.pathSeparator}$fileName"
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
                    }finally {
                        lock.release()
                    }
                }
            }
        } catch (_: FileNotFoundException) {
            File(file).createNewFile()
            return truncateFile(file)
        }
    }
}
