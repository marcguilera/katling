package com.marcguilera.katling.simulation

import com.marcguilera.katling.core.toScala
import io.gatling.core.scenario.Simulation.SetUp
import mu.KotlinLogging
import scala.Function0
import scala.concurrent.duration.FiniteDuration
import scala.runtime.BoxedUnit
import kotlin.reflect.jvm.jvmName

typealias GatlingSimulation = io.gatling.core.scenario.Simulation

private val logger = KotlinLogging.logger(GatlingSimulation::class.jvmName)

/**
 * Applies a [Simulation] configuration into this [GatlingSimulation].
 */
internal fun GatlingSimulation.applyConfiguration(simulation: Simulation) = apply {
    with(simulation) {
        logger.debug { "Configuring Gatling simulation with $simulation" }

        before(gatlingBefore)
        after(gatlingAfter)

        setUp(gatlingScenario)
            .maybeMaxDuration(gatlingDuration)
            .assertions(gatlingAssertions)

        logger.debug { "Configured Gatling simulation with $simulation" }

    }
}

private val Simulation.gatlingBefore
    get() = Function0<BoxedUnit> { beforeHook()  ; BoxedUnit.UNIT }

private val Simulation.gatlingAfter
    get() = Function0<BoxedUnit> { afterHook() ; BoxedUnit.UNIT }

private val Simulation.gatlingDuration
    get() = maxDuration?.toScala()

private val Simulation.gatlingAssertions
    get() = assertions.map { it.toGatling() }.toScala()

private val Simulation.gatlingScenario
    get() = scenarios.map { it.toGatling() }.toScala().toSeq()

private fun SetUp.maybeMaxDuration(duration: FiniteDuration?) = apply {
    if (duration != null) maxDuration(duration)
}