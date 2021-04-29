package com.labijie.infra.stream.file

import com.labijie.infra.stream.file.configuration.FileBinderProperties
import org.springframework.cloud.stream.binder.AbstractMessageChannelBinder
import org.springframework.cloud.stream.binder.ConsumerProperties
import org.springframework.cloud.stream.binder.ProducerProperties
import org.springframework.cloud.stream.provisioning.ConsumerDestination
import org.springframework.cloud.stream.provisioning.ProducerDestination
import org.springframework.integration.core.MessageProducer
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHandler

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-27 20:59
 * @Description:
 */

class FileMessageChannelBinder(
        private val fileBinderProperties: FileBinderProperties,
        fileProvisioningProvider: FileProvisioningProvider
) : AbstractMessageChannelBinder<ConsumerProperties, ProducerProperties, FileProvisioningProvider>(arrayOf(), fileProvisioningProvider) {
    override fun createProducerMessageHandler(destination: ProducerDestination, producerProperties: ProducerProperties?, errorChannel: MessageChannel?): MessageHandler {
        return FileMessageHandler(fileBinderProperties, destination)
    }

    override fun createConsumerEndpoint(destination: ConsumerDestination, group: String?, properties: ConsumerProperties): MessageProducer {
        return FileMessageProducer(fileBinderProperties, destination)
    }

}