package com.marcguilera.katling.simulation.assertion

import com.marcguilera.katling.core.toScala
import io.gatling.core.assertion.AssertionWithPathAndCountMetric
import io.gatling.core.assertion.AssertionWithPathAndTarget

interface Count {
    fun count(): Asserts
    fun percent(): Asserts

    companion object {
        internal fun of(metric: AssertionWithPathAndCountMetric): Count
                = Configuration(metric)
    }

    private data class Configuration(
        private val metric: AssertionWithPathAndCountMetric
    ) : Count {
        override fun count()
            = Asserts.of(metric.count())
        override fun percent()
            = Asserts.of(metric.percent())
    }

    interface Asserts {

        fun isGreaterThanOrEqualTo(num: Number): Assertion
        fun isLessThanOrEqualTo(num: Number): Assertion
        fun isEqualTo(num: Number): Assertion
        fun isGreaterThan(num: Number): Assertion
        fun isLessThan(num: Number): Assertion
        fun isBetween(min: Number, max: Number): Assertion
        fun isIn(vararg num: Number): Assertion

        companion object {
            internal fun of(metric: AssertionWithPathAndTarget<Any>): Asserts
                    = BuiltAsserts(metric)
        }

        data class BuiltAsserts(
            private val metric: AssertionWithPathAndTarget<Any>
        ) : Asserts {
            override fun isGreaterThan(num: Number)
                    = Assertion.of(metric.gt(num))
            override fun isGreaterThanOrEqualTo(num: Number)
                    = Assertion.of(metric.gte(num))
            override fun isLessThan(num: Number)
                    = Assertion.of(metric.lt(num))
            override fun isLessThanOrEqualTo(num: Number)
                    = Assertion.of(metric.lte(num))
            override fun isEqualTo(num: Number)
                    = Assertion.of(metric.`is`(num))
            override fun isBetween(min: Number, max: Number)
                    = Assertion.of(metric.between(min, max, false))
            override fun isIn(vararg num: Number)
                    = Assertion.of(metric.`in`(num.map { it as Any }.toScala().toSeq()))
        }

    }
}
