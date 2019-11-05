package com.marcguilera.katling.simulation.assertion

import io.gatling.core.Predef
import io.gatling.core.assertion.AssertionWithPath

interface ScopeFactory {
    fun global(): Scope
    fun forAll(): Scope
}

interface Scope {

    fun allRequests(): Count
    fun failedRequests(): Count
    fun successfulRequests(): Count
    fun responseTime(): Time

    companion object Factory : ScopeFactory {
        @JvmStatic
        override fun global(): Scope
            = Configuration(Predef.global(Predef.configuration()))
        @JvmStatic
        override fun forAll(): Scope
            = Configuration(Predef.forAll(Predef.configuration()))
    }

    private data class Configuration internal constructor(
        private val path: AssertionWithPath
    ) : Scope {
        override fun responseTime()
            = Time.of(path.responseTime())
        override fun allRequests()
            = Count.of(path.allRequests())
        override fun failedRequests()
            = Count.of(path.failedRequests())
        override fun successfulRequests()
            = Count.of(path.successfulRequests())
    }
}
