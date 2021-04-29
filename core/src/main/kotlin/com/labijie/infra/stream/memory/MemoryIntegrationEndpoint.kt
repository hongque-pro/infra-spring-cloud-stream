package com.labijie.infra.stream.memory

import com.labijie.infra.stream.memory.queue.MemoryListenerContainer
import org.springframework.cloud.stream.provisioning.ConsumerDestination
import org.springframework.integration.endpoint.MessageProducerSupport
import org.springframework.messaging.Message
import java.util.*
import java.util.function.Consumer

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-27
 */
class MemoryIntegrationEndpoint(
    private val destination: ConsumerDestination
) : MessageProducerSupport(), Consumer<Message<*>> {

    override fun accept(t: Message<*>) {
        this.sendMessage(t)
    }

    override fun onInit() {
        super.onInit()
    }

    private val id = UUID.randomUUID()

    override fun equals(other: Any?): Boolean {
        if(other == null || MemoryIntegrationEndpoint::class.java != other::class.java){
            return false
        }
        return this.id == (other as MemoryIntegrationEndpoint).id
    }

    override fun doStart() {
        MemoryListenerContainer.registerListener(this.destination.name, this)
    }

    override fun doStop() {
        MemoryListenerContainer.unregisterListener(this.destination.name)
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}