package com.marcguilera.katling.simulation.scenario

import com.marcguilera.katling.core.toScala
import com.marcguilera.katling.simulation.action.Action
import com.marcguilera.katling.simulation.action.Actions
import com.marcguilera.katling.simulation.action.builders
import com.marcguilera.katling.simulation.injection.Injection
import com.marcguilera.katling.simulation.injection.Injections
import io.gatling.core.Predef
import io.gatling.core.structure.PopulationBuilder
import mu.KLogging

fun Scenarios.Configurer<*>.scenario(name: String, configure: Scenario.Configurer<*>.() -> Unit) {
    exec(Scenario.builder(name).apply(configure).build())
}

/**
 * Represents an object containing [Injection] instances
 * and [Action] instances and is able to generate a [PopulationBuilder].
 */
interface Scenario {
    /**
     * The arbitrary name for this [Scenario].
     */
    val name: String

    /**
     * The injections for this [Scenario].
     */
    val injections: Iterable<Injection>

    /**
     * The actions for this [Scenario].
     */
    val actions: Iterable<Action>

    /**
     * Converts the scenario into it's Gatling [PopulationBuilder] counterpart.
     */
    fun toGatling(): PopulationBuilder

    companion object Factory {
        @JvmStatic
        fun builder(name: String)
                = Builder(name)
    }

    interface Configurer <T : Configurer<T>>  {
        fun name(name: String): T
        fun inject(injections: Injections): T
        fun exec(actions: Actions): T
    }

    class Builder internal constructor(
        private var name: String
    ) : Configurer<Builder> {

        private val injections: MutableSet<Injection> = mutableSetOf()
        private val actions: MutableSet<Action> = mutableSetOf()

        override fun name(name: String) = apply {
            this.name = name
        }

        override fun inject(injections: Injections) = apply {
            this.injections.addAll(injections)
        }

        override fun exec(actions: Actions) = apply {
            this.actions.addAll(actions)
        }

        fun build(): Scenario {
            require(name.isNotBlank()) { "Name can't be blank" }
            require(injections.any()) { "There should be at least one injection" }
            require(actions.any()) { "There should be at least one action" }

            return BuiltScenario (
                name,
                injections.toSet(),
                actions.toSet()
            )
        }
    }

    private data class BuiltScenario (
        override val name: String,
        override val injections: Iterable<Injection>,
        override val actions: Iterable<Action>
    ) : Scenario {

        companion object : KLogging()

        init {
            logger.debug { "Built scenario: $this" }
        }

        override fun toGatling(): PopulationBuilder {

            val injects = injections
                .map { it.toGatling() }
                .toScala()

            val builders = actions
                .builders()
                .toScala()
                .toSeq()

            val scenario = Predef
                .scenario(name)
                .chain(builders)
                .inject(injects, Predef.openInjectionProfileFactory())

            logger.debug { "Converted $this into $scenario" }

            return scenario
        }
    }
}