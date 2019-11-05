package com.marcguilera.katling.simulation.injection

import com.marcguilera.katling.core.toScala
import io.gatling.core.controller.inject.open.HeavisideOpenInjection
import mu.KLogging
import java.time.Duration

fun Injections.Configurer<*>.heaviside(users: Long, duration: Duration) {
    inject(HeavisideInjection.of(users, duration))
}

/**
 * An injection which adds the users using a heaviside function during
 * a specific duration.
 *
 * @see https://en.wikipedia.org/wiki/Heaviside_step_function
 */
interface HeavisideInjection : Injection {
    val users: Long
    val duration: Duration

    companion object Factory {
        @JvmStatic
        fun of(users: Long, duration: Duration): HeavisideInjection {
            require(users > 0) { "Users must be positive" }
            require(!duration.isNegative) { "Duration must be positive" }

            return BuiltHeavisideInjection(users, duration)
        }
    }

    private data class BuiltHeavisideInjection (
        override val users: Long,
        override val duration: Duration
    ): AbstractBuiltInjection<HeavisideOpenInjection>(logger), HeavisideInjection {
        companion object : KLogging()
        override fun doToGatling()
                = HeavisideOpenInjection(users, duration.toScala())
    }
}