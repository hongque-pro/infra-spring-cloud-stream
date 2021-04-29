package com.labijie.infra.stream.file

import org.springframework.cloud.stream.binder.ConsumerProperties
import org.springframework.cloud.stream.binder.ProducerProperties
import org.springframework.cloud.stream.provisioning.ConsumerDestination
import org.springframework.cloud.stream.provisioning.ProducerDestination
import org.springframework.cloud.stream.provisioning.ProvisioningProvider
import java.io.FileDescriptor

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-26 14:07
 * @Description:
 */
class FileProvisioningProvider : ProvisioningProvider<ConsumerProperties, ProducerProperties> {

    override fun provisionProducerDestination(name: String?, properties: ProducerProperties?): ProducerDestination {
        return FileMessageDestination(name.orEmpty())
    }

    override fun provisionConsumerDestination(
        name: String?,
        group: String?,
        properties: ConsumerProperties?
    ): ConsumerDestination {
        return FileMessageDestination(name.orEmpty())
    }

    private class FileMessageDestination constructor(private val destination: String) : ProducerDestination, ConsumerDestination {
        override fun getName(): String {
            return destination.trim()
        }

        override fun getNameForPartition(partition: Int): String {
            throw UnsupportedOperationException("Partitioning is not implemented for file messaging.")
        }
    }
}