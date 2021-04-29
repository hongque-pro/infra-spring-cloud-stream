package com.labijie.dummy

import com.labijie.infra.stream.file.FileMessageProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.context.annotation.Bean
import org.springframework.integration.support.json.Jackson2JsonObjectMapper
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-04-27 23:51
 * @Description:
 */
@SpringBootApplication
class Application: CommandLineRunner {

    private val mapper = Jackson2JsonObjectMapper().objectMapper

    @Bean
    public fun test() :Consumer<TestData> {
        return Consumer {
            println(mapper.writeValueAsString(it))
        }
    }

    @Autowired
    private lateinit var bridge: StreamBridge

    private var count = 0

    override fun run(vararg args: String?) {
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay({
            count++
            bridge.send("test-out-0", TestData(count.toString()))
        }, 10, 100, TimeUnit.MILLISECONDS)
    }
}

fun main() {
    SpringApplication.run(Application::class.java)
}

data class TestData(var id: String = "") {

}