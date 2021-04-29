package com.labijie.infra.stream.memory

import org.springframework.cloud.stream.binder.ConsumerProperties
import org.springframework.cloud.stream.binder.ProducerProperties
import org.springframework.cloud.stream.provisioning.ConsumerDestination
import org.springframework.cloud.stream.provisioning.ProducerDestination
import org.springframework.cloud.stream.provisioning.ProvisioningProvider

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-27
 */
class MemoryProvisioningProvider : ProvisioningProvider<ConsumerProperties, ProducerProperties> {

    override fun provisionProducerDestination(
        name: String?,
        properties: ProducerProperties
    ): ProducerDestination = MemoryDestination(name.orEmpty())

    override fun provisionConsumerDestination(
        name: String?,
        group: String?,
        properties: ConsumerProperties?
    ): ConsumerDestination = MemoryDestination(name.orEmpty())

    private class MemoryDestination constructor(private val destination: String) : ProducerDestination, ConsumerDestination {
        override fun getName(): String {
            return destination.trim()
        }

        override fun getNameForPartition(partition: Int): String {
            throw UnsupportedOperationException("Partitioning is not implemented for memory messaging.")
        }
    }
}