package com.marcguilera.katling.simulation.injection

import com.marcguilera.katling.core.toScala
import io.gatling.core.controller.inject.open.NothingForOpenInjection
import mu.KLogging
import java.time.Duration

fun Injections.Configurer<*>.sleep(duration: Duration) {
    inject(SleepInjection.of(duration))
}

/**
 * An injection which adds no users during a specific duration.
 */
interface SleepInjection : Injection {
    val duration: Duration

    companion object Factory {
        @JvmStatic
        fun of(duration: Duration): SleepInjection {
            require(!duration.isNegative) { "Duration must be positive" }
            return BuiltSleepInjection(duration)
        }
    }

    private data class BuiltSleepInjection (
        override val duration: Duration
    ): AbstractBuiltInjection<NothingForOpenInjection>(logger), SleepInjection {
        companion object : KLogging()
        override fun doToGatling()
                = NothingForOpenInjection(duration.toScala())
    }
}