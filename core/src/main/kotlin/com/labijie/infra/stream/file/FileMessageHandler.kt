package com.labijie.infra.stream.file

import com.labijie.infra.stream.file.configuration.FileBinderProperties
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.provisioning.ProducerDestination
import org.springframework.integration.handler.AbstractMessageHandler
import org.springframework.messaging.Message
import org.springframework.util.Base64Utils
import java.io.File
import java.io.FileNotFoundException
import java.io.RandomAccessFile
import java.nio.channels.FileLock
import java.time.Duration

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-27 21:05
 * @Description:
 */

class FileMessageHandler(
        fileBinderProperties: FileBinderProperties,
        destination: ProducerDestination) : AbstractMessageHandler() {
    companion object {
        @JvmStatic
        private val log = LoggerFactory.getLogger(FileMessageHandler::class.java)
    }


    private val tryLockTimeout = Duration.ofSeconds(10).toMillis()

    private val file = StreamFileUtils.getDestinationFile(fileBinderProperties.folder, destination.name)

    private var access: RandomAccessFile? = null
    private val accessSync = Any()

    private fun getAccessFile(): RandomAccessFile? {
        if (access == null) {
            synchronized(accessSync) {
                if (File(file).exists()) {
                    try{
                        access = RandomAccessFile(file, "rw").apply {
                            this.seek(this.length())
                        }
                    }catch (_: FileNotFoundException){

                    }
                }
            }
        }
        return access
    }

    private fun tryLockWithTimeout(f: RandomAccessFile): FileLock? {
        val startTime = System.currentTimeMillis()
        var lock: FileLock? = f.channel.tryLock()
        while (lock == null && System.currentTimeMillis() - startTime < tryLockTimeout) {
            Thread.sleep(1000)
            lock = f.channel.tryLock(0L, Long.MAX_VALUE, true)
        }
        if (lock == null){
            log.warn("Wait stream file lock more than ${tryLockTimeout / 1000} seconds, message skipped")
        }
        return lock
    }

    override fun handleMessageInternal(message: Message<*>) {
        if(message.payload !is ByteArray){
            log.error("incoming message payload is not byte array.")
            return
        }

        val bytes = message.payload as ByteArray
        val f = this.getAccessFile() ?: return
        f.run {
            writePayload(this, bytes)
        }
    }

    private fun writePayload(f: RandomAccessFile, payload: ByteArray) {
        val lock = tryLockWithTimeout(f) ?: return
        lock.run {
            try {
                val content = (Base64Utils.encodeToString(payload) + System.lineSeparator())
                f.writeBytes(content)
            } finally {
                lock.release()
            }
        }
    }
}