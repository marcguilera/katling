package com.marcguilera.katling.simulation.injection

import io.gatling.core.controller.inject.open.AtOnceOpenInjection
import mu.KLogging

fun Injections.Configurer<*>.atOnce(users: Long) {
    inject(AtOnceInjection.of(users))
}

/**
 * An [Injection] which adds all users at the same time.
 */
interface AtOnceInjection : Injection {
    val users: Long

    companion object Factory {
        @JvmStatic
        fun of(users: Long): AtOnceInjection {
            require(users > 0) { "Users must be positive"}

            return BuiltAtOnceInjection(users)
        }
    }

    private data class BuiltAtOnceInjection (
        override val users: Long
    ): AbstractBuiltInjection<AtOnceOpenInjection>(logger), AtOnceInjection {
        companion object : KLogging()
        override fun doToGatling()
                = AtOnceOpenInjection(users)
    }
}