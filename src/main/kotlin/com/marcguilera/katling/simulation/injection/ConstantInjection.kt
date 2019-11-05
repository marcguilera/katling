package com.marcguilera.katling.simulation.injection

import com.marcguilera.katling.core.toScala
import io.gatling.core.controller.inject.open.ConstantRateOpenInjection
import mu.KLogging
import java.time.Duration

fun Injections.Configurer<*>.constant(rate: Double, duration: Duration) {
    inject(ConstantInjection.of(rate, duration))
}

/**
 * An [Injection] which adds users at a constant pace.
 */
interface ConstantInjection : Injection {
    val rate: Double
    val duration: Duration

    companion object Factory {

        @JvmStatic
        fun of(rate: Double, duration: Duration): ConstantInjection {
            require(rate > 0) { "Rate must be positive" }
            require(!duration.isNegative) { "Duration must be positive" }

            return BuiltConstantInjection(rate, duration)
        }
    }

    private data class BuiltConstantInjection (
        override val rate: Double,
        override val duration: Duration
    ): AbstractBuiltInjection<ConstantRateOpenInjection>(logger), ConstantInjection {
        companion object : KLogging()
        override fun doToGatling()
                = ConstantRateOpenInjection(rate, duration.toScala())
    }
}