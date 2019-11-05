package com.marcguilera.katling.simulation.runner.result

import com.marcguilera.katling.core.toKotlin
import com.marcguilera.katling.simulation.runner.SimulationId
import io.gatling.app.RunResult
import io.gatling.charts.stats.LogFileReader
import io.gatling.commons.stats.assertion.AssertionValidator
import io.gatling.core.Predef
import java.time.Duration
import java.time.Duration.between
import java.time.Instant.ofEpochMilli

interface SimulationResult {
    val id: SimulationId
    val duration: Duration
    val assertions: Iterable<AssertionResult>

    companion object Factory {
        internal fun of(result: RunResult): SimulationResult {
            val id = result.runId()
            val config = Predef.configuration()
            val reader = LogFileReader(id, config)
            val duration = between(ofEpochMilli(reader.runStart()), ofEpochMilli(reader.runEnd()))
            val assertions = AssertionValidator
                .validateAssertions(reader)
                .toKotlin()
                .map { AssertionResult.of(it) }

            return BuiltSimulationResult(
                id,
                duration,
                assertions
            )
        }
    }

    private data class BuiltSimulationResult (
        override val id: SimulationId,
        override val duration: Duration,
        override val assertions: Iterable<AssertionResult>
    ) : SimulationResult

}