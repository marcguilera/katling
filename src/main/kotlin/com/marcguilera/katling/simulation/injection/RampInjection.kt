package com.marcguilera.katling.simulation.injection

import com.marcguilera.katling.core.toScala
import io.gatling.core.controller.inject.open.RampOpenInjection
import mu.KLogging
import java.time.Duration

fun Injections.Configurer<*>.ramp(users: Long, duration: Duration) {
    inject(RampInjection.of(users, duration))
}

/**
 * An [Injection] which adds the users evenly during a specific
 * duration.
 */
interface RampInjection : Injection {
    val users: Long
    val duration: Duration

    companion object Factory {
        @JvmStatic
        fun of(users: Long, duration: Duration): RampInjection {
            require(users > 0) { "Users must be positive" }
            require(!duration.isNegative) { "Duration must be positive" }

            return BuiltRampInjection(users, duration)
        }
    }

    private data class BuiltRampInjection (
        override val users: Long,
        override val duration: Duration
    ) : AbstractBuiltInjection<RampOpenInjection>(logger), RampInjection {
        companion object : KLogging()
        override fun doToGatling()
                = RampOpenInjection(users, duration.toScala())
    }
}
