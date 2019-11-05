package com.marcguilera.katling.simulation.injection

import com.marcguilera.katling.simulation.scenario.Scenario
import mu.KLogging

fun Scenario.Configurer<*>.injections(configure: Injections.Configurer<*>.() -> Unit) {
    inject(Injections.builder().apply(configure).build())
}

interface Injections : Iterable<Injection> {

    companion object Factor {
        @JvmStatic
        fun builder()
                = Builder()
    }

    interface Configurer<T> {
        fun inject(injection: Injection): T
    }

    class Builder internal constructor() : Configurer<Builder> {

        private val injections: MutableSet<Injection> = mutableSetOf()

        override fun inject(injection: Injection) = apply {
            injections.add(injection)
        }

        fun build(): Injections {
            require(injections.any()) { "There must be at least one injection" }

            return BuiltInjections(injections)
        }

    }

    private data class BuiltInjections (
        val injections: Iterable<Injection>
    ) : Injections {

        private companion object : KLogging()

        init {
            logger.debug { "Built injections: $this" }
        }

        override fun iterator()
                = injections.iterator()
    }
}