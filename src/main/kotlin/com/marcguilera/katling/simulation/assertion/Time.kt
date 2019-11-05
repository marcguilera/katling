package com.marcguilera.katling.simulation.assertion

import com.marcguilera.katling.core.toScala
import io.gatling.core.assertion.AssertionWithPathAndTarget
import io.gatling.core.assertion.AssertionWithPathAndTimeMetric
import java.time.Duration

interface Time {

    fun max(): Asserts
    fun min(): Asserts
    fun mean(): Asserts
    fun stdDev(): Asserts
    fun percentile(value: Double): Asserts

    companion object {
        internal fun of(metric: AssertionWithPathAndTimeMetric): Time
                = Configuration(metric)
    }

    data class Configuration(
        private val metric: AssertionWithPathAndTimeMetric
    ) : Time {
        override fun mean()
            = Asserts.of(metric.mean())
        override fun min()
            = Asserts.of(metric.min())
        override fun max()
            = Asserts.of(metric.max())
        override fun stdDev()
            = Asserts.of(metric.stdDev())
        override fun percentile(value: Double)
            = Asserts.of(metric.percentile(value))
    }

    interface Asserts {

        fun isGreaterThanOrEqualTo(duration: Duration): Assertion
        fun isGreaterThan(duration: Duration): Assertion
        fun isLessThanOrEqualTo(duration: Duration): Assertion
        fun isLessThan(duration: Duration): Assertion
        fun isBetween(min: Duration, max: Duration): Assertion
        fun isIn(vararg duration: Duration): Assertion

        companion object {
            internal fun of(metric: AssertionWithPathAndTarget<Any>): Asserts
                = Configuration(metric)
        }

        private data class Configuration(
            val metric: AssertionWithPathAndTarget<Any>
        ) : Asserts {
            override fun isGreaterThanOrEqualTo(duration: Duration)
                    = Assertion.of(metric.gte(duration.toMillis()))
            override fun isLessThanOrEqualTo(duration: Duration)
                    = Assertion.of(metric.lte(duration.toMillis()))
            override fun isGreaterThan(duration: Duration)
                    = Assertion.of(metric.gt(duration.toMillis()))
            override fun isLessThan(duration: Duration)
                    = Assertion.of(metric.lt(duration.toMillis()))
            override fun isIn(vararg duration: Duration)
                    = Assertion.of(metric.`in`(duration.map { it.toMillis(); it as Any }.toScala().toSeq()))
            override fun isBetween(min: Duration, max: Duration)
                    = Assertion.of(metric.between(min.toMillis(), max.toMillis(), false))
        }
    }
}