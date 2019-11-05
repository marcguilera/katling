package com.marcguilera.katling.simulation.scenario

import com.marcguilera.katling.simulation.Simulation
import mu.KLogging

fun Simulation.Configurer<*>.scenarios(configure: Scenarios.Configurer<*>.() -> Unit) {
    exec(Scenarios.builder().apply(configure).build())
}

/**
 * A list of [Scenario] instances.
 */
interface Scenarios : Iterable<Scenario> {

    companion object Factor {
        @JvmStatic
        fun builder()
                = Builder()
    }

    interface Configurer <T : Configurer<T>> {
        fun exec(scenario: Scenario): T
    }

    class Builder internal constructor() : Configurer<Builder> {

        private val scenarios: MutableSet<Scenario> = mutableSetOf()

        override fun exec(scenario: Scenario) = apply {
            scenarios.add(scenario)
        }

        fun build(): Scenarios {
            require(scenarios.isNotEmpty()) { "There must be at least one scenario" }

            return BuiltScenarios(scenarios.toSet())
        }

    }

    private data class BuiltScenarios (
        private val scenarios: Iterable<Scenario>
    ) : Scenarios {

        private companion object : KLogging()

        init {
            logger.debug { "Built scenarios: $this" }
        }

        override fun iterator()
                = scenarios.iterator()
    }
}