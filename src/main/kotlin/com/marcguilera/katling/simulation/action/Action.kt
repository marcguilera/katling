package com.marcguilera.katling.simulation.action

import com.marcguilera.katling.simulation.scenario.Scenario
import io.gatling.core.structure.ChainBuilder
import mu.KLogger

/**
 * Represents an action performed within a [Scenario].
 * An action is the a single unit of test or a step within it.
 */
interface Action {
    /**
     * Converts this action to its Gatling [ChainBuilder] counterpart.
     */
    fun toGatling(): ChainBuilder
}

internal abstract class AbstractBuiltAction (
    protected val logger: KLogger
) : Action {

    init {
        logger.debug { "Built action: $this" }
    }

    override fun toGatling()
            = doToGatling()
                .builders()
                .toChain()
                .also { logger.debug { "Converted $this into $it" } }

    protected abstract fun doToGatling(): ChainBuilder
}