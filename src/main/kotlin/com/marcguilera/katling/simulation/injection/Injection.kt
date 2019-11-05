package com.marcguilera.katling.simulation.injection

import io.gatling.core.controller.inject.open.OpenInjectionStep
import mu.KLogger

/**
 * Represents an injection instruction able to configure
 * a [OpenInjectionStep].
 */
interface Injection {
    /**
     * Builds the Gatling [OpenInjectionStep] from this
     * [Injection].
     */
    fun toGatling(): OpenInjectionStep
}

internal abstract class AbstractBuiltInjection<T : OpenInjectionStep>(
    protected val logger: KLogger
) : Injection {

    init {
        logger.debug { "Built injection: $this" }
    }

    override fun toGatling(): T {
        val injection = doToGatling()
        logger.debug { "Converted $this into $injection" }
        return injection
    }

    protected abstract fun doToGatling(): T
}



