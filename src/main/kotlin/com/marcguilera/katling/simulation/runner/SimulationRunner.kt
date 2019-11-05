package com.marcguilera.katling.simulation.runner

import akka.actor.ActorSystem
import com.marcguilera.katling.core.InstantFactory
import com.marcguilera.katling.core.toScala
import com.marcguilera.katling.simulation.GatlingSimulation
import com.marcguilera.katling.simulation.Simulation
import com.marcguilera.katling.simulation.applyConfiguration
import com.marcguilera.katling.simulation.runner.result.SimulationResult
import io.gatling.app.RunResult
import io.gatling.app.Runner
import io.gatling.commons.util.Clock
import io.gatling.core.config.GatlingConfiguration
import mu.KLogging
import scala.Some
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock

typealias SimulationId = String

fun simulationRunner(configure: SimulationRunner.Configurer<*>.() -> Unit = {}): SimulationRunner
        = SimulationRunner.builder().apply(configure).build()

/**
 * Represents an object able to run simulations. It is configurable
 * but most of the times the defaults are enough.
 */
interface SimulationRunner {

    /**
     * The factory to get the current time used internally by Gatling.
     */
    val instants: InstantFactory

    /**
     * The name of the actor system to use.
     */
    val name: String

    /**
     * An arbitrary set of properties to run the simulation.
     */
    val configuration: Map<String, String>

    /**
     * Runs the simulation.
     */
    fun run(simulation: Simulation): SimulationResult

    companion object Factory {
        @JvmStatic
        fun builder()
                = Builder()
    }

    interface Configurer <T : Configurer<T>> {
        fun name(name: String): T
        fun instants(instants: InstantFactory): T
        fun configuration(configuration: Map<String, String>): T
    }

    class Builder internal constructor() : Configurer<Builder> {
        private var name: String = "GatlingSystem"
        private var instants: InstantFactory = InstantFactory.UTC
        private var configuration: MutableMap<String, String> = mutableMapOf()

        override fun name(name: String) = apply {
            this.name = name
        }

        override fun instants(instants: InstantFactory) = apply {
            this.instants = instants
        }

        override fun configuration(configuration: Map<String, String>) = apply {
            this.configuration.putAll(configuration)
        }

        fun build(): SimulationRunner {
            require(name.isNotBlank()) { "Name can't be blank" }

            return BuiltSimulationRunner(name, instants, configuration.toMap())
        }

    }

    private class BuiltSimulationRunner (
        override var name: String,
        override val instants: InstantFactory,
        override val configuration: Map<String, String>
    ) : SimulationRunner {

        private companion object : KLogging()

        init {
            logger.debug { "Built runner: $this" }
        }

        override fun run(simulation: Simulation): SimulationResult {

            logger.debug { "Running simulation: $simulation" }

            val actors = ActorSystem.create(name, GatlingConfiguration.loadActorSystemConfiguration())
            val clock = Clock { instants.now().toEpochMilli() }
            val config = GatlingConfiguration.load(configuration.toScala())

            val runResult = try {
                simulation.run(actors, clock, config)
            } finally {
                actors.terminate()
            }

            val result = SimulationResult.of(runResult)

            logger.debug { "Done running simulation with result: $result" }

            return result
        }

        private fun Simulation.run(actors: ActorSystem, clock: Clock, config: GatlingConfiguration)
                = RunnableGatlingSimulation.run(actors, clock, config, this)
    }

    // Currently gatling only supports spawning simulations from classes so we need to do this sneaky static hack.
    class RunnableGatlingSimulation internal constructor() : GatlingSimulation() {

        companion object {
            private val mutex = Semaphore(1)
            private lateinit var simulation: Simulation

            internal fun run(actors: ActorSystem, clock: Clock, config: GatlingConfiguration, simulation: Simulation): RunResult {
                mutex.acquire()
                this.simulation = simulation
                try {
                    return doRun(actors, clock, config)
                } catch (exception: Exception) {
                    mutex.release()
                    throw exception
                }
            }

            @Suppress("UNCHECKED_CAST")
            private fun doRun(actors: ActorSystem, clock: Clock, config: GatlingConfiguration)
                    = Runner(actors, clock, config)
                        .run(Some(RunnableGatlingSimulation::class.java as Class<GatlingSimulation>))
        }

        init {
            applyConfiguration(simulation)
            mutex.release()
        }
    }
}