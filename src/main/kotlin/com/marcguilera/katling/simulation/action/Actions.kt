package com.marcguilera.katling.simulation.action

import com.marcguilera.katling.simulation.scenario.Scenario
import mu.KLogging

fun Scenario.Configurer<*>.actions(configure: Actions.Configurer<*>.() -> Unit) {
    exec(Actions.builder().apply(configure).build())
}

interface Actions : Iterable<Action> {

    companion object Factory {
        @JvmStatic
        fun builder()
                = Builder()
    }

    interface Configurer <T : Configurer<T>> {
        fun exec(action: Action): T
    }

    class Builder internal constructor() : Configurer<Builder> {

        private val actions: MutableSet<Action> = mutableSetOf()

        override fun exec(action: Action) = apply {
            actions.add(action)
        }

        fun build(): Actions {
            require(actions.isNotEmpty()) { "There should be at least one action" }

            return BuiltActions(
                actions.toSet()
            )
        }
    }

    private data class BuiltActions (
        private val actions: Iterable<Action>
    ) : Actions {

        private companion object : KLogging()

        init {
            logger.debug { "Built actions: $this" }
        }

        override fun iterator()
                = actions.iterator()
    }
}