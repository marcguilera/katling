package com.marcguilera.katling.simulation.runner.result

import io.gatling.commons.stats.assertion.AssertionResult as GatlingAssertionResult

interface AssertionResult {
    val isSuccess: Boolean
    val message: String

    companion object Factory {
        internal fun of(assertion: GatlingAssertionResult): AssertionResult
                = BuiltAssertionResult(assertion.result(), assertion.message())
    }

    private data class BuiltAssertionResult (
        override val isSuccess: Boolean,
        override val message: String
    ) : AssertionResult
}

