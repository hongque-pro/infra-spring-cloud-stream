package com.labijie.infra.stream.file

import org.apache.commons.io.input.Tailer
import org.apache.commons.io.input.TailerListener
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.Exception
import java.nio.charset.Charset

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-28 3:31
 * @Description:
 */

internal class TailReader(
    file: String,
    callback: (line:String)->Unit,
    private val stopCallback:(r: TailReader)->Unit,
) : Tailer(File(file), Charsets.UTF_8, Listener(file, callback), 1000, false, false, 4096){

    companion object{
        private val logger = LoggerFactory.getLogger(TailReader::class.java)
    }

    override fun stop() {
        super.stop()
        stopCallback.invoke(this)
    }

    class Listener(private val file:String, private val callback: (line:String)->Unit): TailerListener {
        override fun init(tailer: Tailer) {

        }

        override fun fileNotFound() {
            logger.error("file not found:${System.lineSeparator()} $file")
        }

        override fun fileRotated() {

        }

        override fun handle(line: String?) {
            if(!line.isNullOrBlank()) {
                this.callback.invoke(line)
            }
        }

        override fun handle(ex: Exception?) {
            logger.error("Tail listener fault ( file: ${this.file} )", ex)

        }
    }
}
