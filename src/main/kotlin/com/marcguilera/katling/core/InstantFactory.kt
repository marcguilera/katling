package com.marcguilera.katling.core

import java.time.Clock
import java.time.Instant

/**
 * A factory able to get the current time, used internally by Gatling
 * to figure out times.
 */
interface InstantFactory {
    /**
     * Gets the current instant.
     */
    fun now(): Instant

    object UTC : InstantFactory {
        override fun now(): Instant
                = Instant.now(Clock.systemUTC())
    }
}