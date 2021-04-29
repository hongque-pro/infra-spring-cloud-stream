package com.labijie.infra.stream.memory

import com.labijie.infra.stream.memory.configuration.MemoryBinderProperties
import org.springframework.cloud.stream.binder.*
import org.springframework.cloud.stream.provisioning.ConsumerDestination
import org.springframework.cloud.stream.provisioning.ProducerDestination
import org.springframework.integration.core.MessageProducer
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHandler

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-27
 */
class MemoryMessageChannelBinder(
    private val configurationProperties: MemoryBinderProperties,
    provisioningProvider: MemoryProvisioningProvider
) :
    AbstractMessageChannelBinder<ConsumerProperties,ProducerProperties,MemoryProvisioningProvider>(arrayOf(), provisioningProvider){

    override fun createConsumerEndpoint(
        destination: ConsumerDestination,
        group: String?,
        properties: ConsumerProperties?
    ): MessageProducer {
        return MemoryIntegrationEndpoint(
            destination)

    }

    override fun createProducerMessageHandler(
        destination: ProducerDestination,
        producerProperties: ProducerProperties?,
        errorChannel: MessageChannel?
    ): MessageHandler {
        return MemoryMessageHandler(this.configurationProperties, destination)
    }
}