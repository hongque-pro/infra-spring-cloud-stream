# Infra Spring Cloud Stream Binder (for development)

使用内存队列、文件作为中间件 的 spring cloud stream 标准实现.

![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)
![Maven Central](https://img.shields.io/maven-central/v/com.labijie.infra/spring-cloud-stream-binder-core.svg?color=orange)


## 为什么需要这个项目，应用场景是什么？

1. 贫穷架构：单进程单点部署的程序，因为贫穷的原因，可以节省昂贵的中间件开销。
2. 演示类场景： 对于 DEMO, 演示程序，项目早期的项目程序，无需中间件也可以使用 spring cloud stream 。
3. 平滑迁移：无需更改任何代码，仅改变一行配置就可以平滑过度到真正的 "cloud stream" 模式。
4. 本机开发：代码拿回家，更改一行代码即可本机调试

## 实现：

- **memory**: 基于内存队列的 stream 实现
- **file**: 基于文件系统的 stream 实现

> memory 实现要求 input binding 和 out binding 在同一进程   
> file 实现保存的文件名为 <destination>.stream, input 和 out 必须一致   
   
**注意:** file 实现在高并发时可能出现丢失消息的情况，不应该被用于生产环境，仅供开发时使用

# Quick Start

**添加依赖**

```groovy
dependencies {
    implementation "com.labijie.infra:spring-cloud-stream-binder-core:<version>"
}
```


## 使用 spring cloud stream

参考文档：https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/spring-cloud-stream.html#spring-cloud-stream-overview-introducing

### 简单例子 (Kotlin)

```kotlin
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

data class TestData(var id: String = "")

```

### 配置

```yaml
spring:
  cloud:
    stream:
      default-binder: file
      bindings:
        test-in-0:
          destination: 'test'
        test-out-0:
          destination: 'test'
```

> spring cloud stream 的标准配置模式都支持，这，是一个标准实现！
       

### 可选配置

```yaml
spring:
  cloud:
    stream:
      file:
        default:
          folder: '~/stream-files'
      memory:
        default:
          queue-size: 2048
          worker-pool-size: 1
```

说明：

|配置         | 默认值           |                                      说明 |
| ------------- |:-------------:|----------------------------------------:|
|spring.cloud.stream.memory.default.worker-pool-size| -1 |           工作线程池大小，当小于 0 时使用 CPU 核心数 1 半 | 
|spring.cloud.stream.memory.default.queue-size| 2048 |                  阻塞队列大小，当队列满了以后将丢弃最早的消息 | 
|spring.cloud.stream.file.default.folder| "" | 存放 stream file 的目录，默认为用户目录( user.home ) | 

> 所有的配置都能够在 IDEA 中智能提示

### 开发环境兼容性：
 
 |组件|版本|说明|
 |--------|--------|--------|
 |   kotlin    |      1.4.10    |           |
 |   jdk    |      1.8   |           |
 |   spring boot    |      2.4.5    |           |
 |  spring cloud    |      2020.0.2    |   通过 BOM 控制版本，因为 cloud 组件版本混乱，无法统一指定  |
 |   spring framework    |      5.3.6   |           |
 |   spring dpendency management    |      1.0.10.RELEASE    |         

### 祝你使用愉快 ！
