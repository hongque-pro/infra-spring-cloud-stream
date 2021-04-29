package com.labijie.infra.stream.file.configuration

import com.labijie.infra.stream.file.configuration.FileBinderProperties.Companion.ConfigKey
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-27 22:05
 * @Description:
 */
@ConfigurationProperties(prefix = ConfigKey)
data class FileBinderProperties(var folder: String = ""){
    companion object {
        const val ConfigKey = "spring.cloud.stream.file.default"
    }
}