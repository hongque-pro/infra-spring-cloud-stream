package com.labijie.infra.stream.memory.configuration

import com.labijie.infra.stream.memory.MemoryMessageChannelBinder
import com.labijie.infra.stream.memory.MemoryProvisioningProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.stream.binder.Binder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-27
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(Binder::class)
@EnableConfigurationProperties(MemoryBinderProperties::class)
class MemoryBinderAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun memoryMessageChannelBinder(
        configurationProperties: MemoryBinderProperties,
    ): MemoryMessageChannelBinder {
        val provisioningProvider = MemoryProvisioningProvider()

        return MemoryMessageChannelBinder(
            configurationProperties,
            provisioningProvider)
    }
}