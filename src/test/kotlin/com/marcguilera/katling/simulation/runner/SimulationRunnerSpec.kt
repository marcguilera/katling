package com.marcguilera.katling.simulation.runner

import com.marcguilera.katling.expression.expressionOf
import com.marcguilera.katling.simulation.action.actions
import com.marcguilera.katling.simulation.action.callback
import com.marcguilera.katling.simulation.action.pause
import com.marcguilera.katling.simulation.injection.atOnce
import com.marcguilera.katling.simulation.injection.injections
import com.marcguilera.katling.simulation.injection.ramp
import com.marcguilera.katling.simulation.scenario.scenario
import com.marcguilera.katling.simulation.scenario.scenarios
import com.marcguilera.katling.simulation.simulation
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.sandjelkovic.kxjtime.seconds
import org.spekframework.spek2.Spek

object SimulationRunnerSpec : Spek({

    val users = 10L
    val ramp = 1.seconds
    val pause = 1.seconds
    val callback: () -> Unit = mock()

    group("Valid configuration") {
        val runner = simulationRunner()

        val simulation = simulation {
            scenarios {
                scenario("scenario") {
                    injections {
                        atOnce(users)
                        ramp(users, ramp)
                    }
                    actions {
                        pause(expressionOf(pause))
                        callback { callback() }
                    }
                }
            }
        }

        test("The simulation runs") {
            runner.run(simulation)
        }

        test ("The actions have been run") {
            verify(callback, times(20)).invoke()
        }
    }
})
