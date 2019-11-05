package com.marcguilera.katling.simulation

import com.marcguilera.katling.simulation.assertion.Assertion
import com.marcguilera.katling.simulation.assertion.Assertions
import com.marcguilera.katling.simulation.scenario.Scenario
import com.marcguilera.katling.simulation.scenario.Scenarios
import mu.KLogging
import java.time.Duration
import io.gatling.core.scenario.Simulation as GatlingSimulation

typealias BeforeCallback = () -> Any
typealias AfterCallback = () -> Any

/**
 * Creates a simulation.
 */
fun simulation(configure: Simulation.Configurer<*>.() -> Unit): Simulation
        = Simulation.builder().apply(configure).build()

/**
 * Represents a configurable object able to produce a [GatlingSimulation].
 * It's the entry point of any tests and contains [Scenario] instances.
 */
interface Simulation {
    /**
     * Hooks to be run before this [Simulation].
     */
    val beforeHook: BeforeCallback

    /**
     * Hooks to be run before this [Simulation].
     */
    val afterHook: AfterCallback

    /**
     * The optional maximum time this [Simulation] is allowed to run for.
     */
    val maxDuration: Duration?

    /**
     * The scenarios to execute.
     */
    val scenarios: Iterable<Scenario>

    /**
     * The assertions to check for.
     */
    val assertions: Iterable<Assertion>

    /**
     * Converts this [Simulation] into its [GatlingSimulation] counterpart.
     */
    fun toGatling(): GatlingSimulation

    companion object Factory {
        @JvmStatic
        fun builder()
                = Builder()
    }

    interface Configurer <T : Configurer<T>> {
        fun maxDuration(duration: Duration): T
        fun before(before: BeforeCallback): T
        fun after(after: AfterCallback): T
        fun exec(scenarios: Scenarios): T
        fun assert(assertions: Assertions): T
    }

    class Builder internal constructor() : Configurer<Builder> {
        private val beforeHooks: MutableSet<BeforeCallback> = mutableSetOf()
        private val afterHooks: MutableSet<BeforeCallback> = mutableSetOf()
        private val assertions: MutableSet<Assertion> = mutableSetOf()
        private val scenarios: MutableSet<Scenario> = mutableSetOf()
        private var maxDuration: Duration? = null

        override fun maxDuration(duration: Duration) = apply {
            maxDuration = duration
        }

        override fun before(before: BeforeCallback) = apply {
            beforeHooks.add(before)
        }

        override fun after(after: AfterCallback) = apply {
            afterHooks.add(after)
        }

        override fun exec(scenarios: Scenarios) = apply {
            this.scenarios.addAll(scenarios)
        }

        override fun assert(assertions: Assertions) = apply {
            this.assertions.addAll(assertions)
        }

        fun build(): Simulation {
            require(scenarios.any()) { "There should be at least one scenario" }
            require(maxDuration?.isNegative?.not() ?: true) { "maxDuration must be positive" }

            return BuiltSimulation (
                beforeHooks.flatten(),
                afterHooks.flatten(),
                scenarios.toSet(),
                assertions.toSet(),
                maxDuration
            )
        }

        private fun Iterable<() -> Any>.flatten()
                = map { { it() } }.let { { it.forEach { it() } } }
    }

    private data class BuiltSimulation(
        override val beforeHook: BeforeCallback,
        override val afterHook: AfterCallback,
        override val scenarios: Iterable<Scenario>,
        override val assertions: Iterable<Assertion>,
        override val maxDuration: Duration?
    ) : Simulation {

        companion object : KLogging()

        init {
            logger.debug { "Built simulation: $this" }
        }

        override fun toGatling(): GatlingSimulation {
            val simulation = BuiltGatlingSimulation()
                .applyConfiguration(this)

            logger.debug { "Converted $this into $simulation" }
            return simulation
        }
    }

    private class BuiltGatlingSimulation : GatlingSimulation()
}