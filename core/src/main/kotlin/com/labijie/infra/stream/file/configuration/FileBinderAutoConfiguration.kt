package com.labijie.infra.stream.file.configuration

import com.labijie.infra.stream.file.FileMessageChannelBinder
import com.labijie.infra.stream.file.FileProvisioningProvider
import com.labijie.infra.stream.file.StreamFileUtils
import com.labijie.infra.stream.file.configuration.FileBinderProperties.Companion.ConfigKey
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-27 22:05
 * @Description:
 */

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FileBinderProperties::class)
class FileBinderAutoConfiguration {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(FileBinderAutoConfiguration::class.java)
    }

    @Bean
    @ConditionalOnMissingBean
    fun fileMessageChannelBinder(
            fileBinderProperties: FileBinderProperties,
    ): FileMessageChannelBinder {

        if(fileBinderProperties.folder.isBlank()){
            logger.warn("The value of '$ConfigKey.folder' is not specified, and the '${StreamFileUtils.defaultFolder}' is used as the stream file directory")
        }

        val provisioningProvider = FileProvisioningProvider()

        return FileMessageChannelBinder(
                fileBinderProperties,
                provisioningProvider)
    }
}