package com.marcguilera.katling.simulation.assertion

typealias GatlingAssertion = io.gatling.commons.stats.assertion.Assertion

interface Assertion {

    companion object Factory : ScopeFactory by Scope {
        internal fun of(assertion: GatlingAssertion): Assertion
            = BuiltAssertion(assertion)
    }

    private data class BuiltAssertion (
        private val assertion: GatlingAssertion
    ) : Assertion {
        override fun toGatling() = assertion
    }

    fun toGatling(): GatlingAssertion
}