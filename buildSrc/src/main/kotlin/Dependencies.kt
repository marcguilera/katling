object Dependencies {
    object Kotlin {
        const val version = "1.3.50"
        private const val prefix = "org.jetbrains.kotlin:kotlin"
        const val stdlib = "$prefix-stdlib-jdk8:$version"
        const val reflect = "$prefix-reflect:$version"
        const val logging = "io.github.microutils:kotlin-logging:1.7.6"
        const val time = "com.sandjelkovic.kxjtime:kxjtime:0.1.0"
    }
    object Gatling {
        private const val version = "3.3.0"
        private const val prefix = "io.gatling:gatling"
        const val core = "$prefix-core:$version"
        const val app = "$prefix-app:$version"
        const val http = "$prefix-http:$version"
    }
    object Test {
        const val assertk = "com.willowtreeapps.assertk:assertk:0.19"
        const val mockito = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
        const val junit = "org.junit.platform:junit-platform-runner:1.0.0"
        object Spek {
            private const val version = "2.0.7"
            private const val prefix = "org.spekframework.spek2:spek"
            const val dsl = "$prefix-dsl-jvm:$version"
            const val junit = "$prefix-runner-junit5:$version"
        }
    }
}