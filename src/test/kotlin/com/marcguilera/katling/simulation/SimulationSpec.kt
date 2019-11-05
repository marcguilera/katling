package com.marcguilera.katling.simulation

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import com.marcguilera.katling.simulation.assertion.Assertion
import com.marcguilera.katling.simulation.assertion.assertions
import com.marcguilera.katling.simulation.scenario.Scenario
import com.marcguilera.katling.simulation.scenario.scenarios
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.sandjelkovic.kxjtime.seconds
import com.sandjelkovic.kxjtime.unaryMinus
import org.spekframework.spek2.Spek

object SimulationSpec : Spek({

    val before: () -> Unit = mock()
    val after: () -> Unit = mock()
    val duration = 1.seconds
    val scenario: Scenario = mock()
    val assertion: Assertion = mock()

    group("Legal arguments") {

        val simulation = simulation {
            maxDuration(duration)
            before(before)
            after(after)
            scenarios {
                exec(scenario)
            }
            assertions {
                assert(assertion)
            }
        }

        test("maxDuration is set") {
            assertThat(simulation.maxDuration)
                .isEqualTo(duration)
        }
        test("before contains the callback") {
            simulation.beforeHook()
            verify(before, times(1)).invoke()
        }
        test("after contains the callback") {
            simulation.afterHook()
            verify(after, times(1)).invoke()
        }
        test("scenario is set") {
            assertThat(simulation.scenarios)
                .contains(scenario)
        }
        test("assertion is set") {
            assertThat(simulation.assertions)
                .contains(assertion)
        }
    }

    group("Illegal arguments") {
        test("throws when no scenario is set") {
            assertThat {
                simulation { }
            }.isFailure()
        }
        test("throws when maxDuration is negative") {
            assertThat {
                simulation { maxDuration(-duration) }
            }.isFailure()
        }
    }
})