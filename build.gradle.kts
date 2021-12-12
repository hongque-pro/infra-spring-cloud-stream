
plugins {
    id("com.labijie.infra") version Versions.infraPlugin
}

allprojects {
    group = "com.labijie.infra"
    version = "1.2.1"

    infra {
        useDefault {
            includeSource = true
            infraBomVersion = Versions.infraBom
            kotlinVersion = Versions.kotlin
            useMavenProxy = false
        }

        useNexusPublish()
    }
}
subprojects {
    if(!project.name.startsWith("dummy")){
        infra {
            usePublish {
                description = "simple binder implement for spring cloud stream application"
                githubUrl("hongque-pro", "infra-spring-cloud-stream")
                artifactId {
                    "spring-cloud-stream-binder-${it.name}"
                }
            }
        }
    }
}




