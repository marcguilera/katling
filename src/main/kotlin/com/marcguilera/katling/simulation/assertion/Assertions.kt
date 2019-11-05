package com.marcguilera.katling.simulation.assertion

import com.marcguilera.katling.simulation.Simulation
import mu.KLogging

fun Simulation.Configurer<*>.assertions(configure: Assertions.Configurer<*>.() -> Unit)
        = assert(Assertions.builder().apply(configure).build())


interface Assertions : Iterable<Assertion> {

    companion object Factor {
        @JvmStatic
        fun builder()
                = Builder()
    }

    interface Configurer <T : Configurer<T>> {
        fun assert(assertion: Assertion): T
    }

    class Builder internal constructor() : Configurer<Builder> {

        private val assertions: MutableSet<Assertion> = mutableSetOf()

        override fun assert(assertion: Assertion) = apply {
            assertions.add(assertion)
        }

        fun build(): Assertions {
            require(assertions.any()) { "There must be at least one assertion" }

            return BuiltAssertions(assertions)
        }

    }

    private data class BuiltAssertions (
        private val assertions: Iterable<Assertion>
    ) : Assertions {

        private companion object : KLogging()

        init {
            logger.debug { "Built assertions: $this" }
        }

        override fun iterator()
                = assertions.iterator()
    }
}