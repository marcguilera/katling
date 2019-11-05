package com.marcguilera.katling.simulation.scenario

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import com.marcguilera.katling.simulation.action.Action
import com.marcguilera.katling.simulation.action.actions
import com.marcguilera.katling.simulation.injection.Injection
import com.marcguilera.katling.simulation.injection.injections
import com.marcguilera.katling.simulation.simulation
import com.nhaarman.mockitokotlin2.mock
import org.spekframework.spek2.Spek

object ScenarioSpec : Spek({
    val name = "name"
    val injection: Injection = mock()
    val action: Action = mock()

    group("Legal arguments") {

        val scenario = simulation {
            scenarios {
                scenario(name) {
                    injections {
                        inject(injection)
                    }
                    actions {
                        exec(action)
                    }
                }
            }
        }.scenarios.first()

        test("name is set") {
            assertThat(scenario.name)
                .isEqualTo(name)
        }
        test("injection is set") {
            assertThat(scenario.injections)
                .contains(injection)
        }
        test("action is set") {
            assertThat(scenario.actions)
                .contains(action)
        }
    }

    group("Illegal arguments") {
        test("throws when name is not set") {
            assertThat {
                simulation {
                    scenarios {
                        scenario("") {
                            injections {
                                inject(injection)
                            }
                            actions {
                                exec(action)
                            }
                        }
                    }
                }
            }.isFailure()
        }
        test("throws when injection is not set") {
            assertThat {
                simulation {
                    scenarios {
                        scenario(name) {
                            actions {
                                exec(action)
                            }
                        }
                    }
                }
            }.isFailure()
        }

        test("throws when action is not set") {
            assertThat {
                simulation {
                    scenarios {
                        scenario(name) {
                            injections {
                                inject(injection)
                            }
                        }
                    }
                }
            }.isFailure()
        }
    }
})