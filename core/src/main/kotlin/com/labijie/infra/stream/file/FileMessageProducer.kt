package com.labijie.infra.stream.file

import com.labijie.infra.stream.file.configuration.FileBinderProperties
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.provisioning.ConsumerDestination
import org.springframework.integration.endpoint.MessageProducerSupport
import org.springframework.integration.support.MessageBuilder
import org.springframework.util.Base64Utils
import java.io.File


/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-26 14:13
 * @Description:
 */

class FileMessageProducer(
    private val fileBinderProperties: FileBinderProperties,
    private val destination: ConsumerDestination
) : MessageProducerSupport() {

    companion object {
        private val log = LoggerFactory.getLogger(FileMessageProducer::class.java)
    }

    @Volatile
    private var stopped: Boolean = false
    private val file = StreamFileUtils.getDestinationFile(fileBinderProperties.folder, destination.name)


    private var reader: TailReader? = null

    override fun doStart() {
        super.doStart()
        stopped = false
        File(fileBinderProperties.folder).mkdirs()
        StreamFileUtils.truncateFile(file)

        reader = TailReader(file, {
            line->
            try {
                val contentBytes = (line).toByteArray()
                val bytes = Base64Utils.decode(contentBytes)

                val msg = MessageBuilder.withPayload(bytes).build()
                this.sendMessage(msg)

            } catch (e: Exception) {
                log.warn("decode base64 message fault.${System.lineSeparator()}$line", e)
            }
        }){
            if(!this.stopped) {
                this.doStart()
            }
        }

        Thread(reader, "file-message-producer-$file").start()
    }

    override fun doStop() {
        stopped = true
        super.doStop()
        reader?.stop()
    }
}