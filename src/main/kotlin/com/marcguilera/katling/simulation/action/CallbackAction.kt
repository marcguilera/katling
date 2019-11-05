package com.marcguilera.katling.simulation.action

import com.marcguilera.katling.session.ImmutableSession
import com.marcguilera.katling.session.Session
import io.gatling.core.structure.ChainBuilder
import mu.KLogging

fun Actions.Configurer<*>.callback(callback: (ImmutableSession) -> Any) {
    exec(CallbackAction.of(callback))
}

/**
 * A convenience action which executes it's callback. Very useful for
 * triggering side effects like logging. It may or may not use the [ImmutableSession]
 * but will never modify it.
 */
interface CallbackAction : Action {
    val callback: (Session) -> Any

    companion object Factory {
        @JvmStatic
        fun of(callback: (ImmutableSession) -> Any): CallbackAction
                = BuiltCallbackAction(callback)
    }

    private data class BuiltCallbackAction (
        override val callback: (ImmutableSession) -> Any
    ) : AbstractBuiltAction(logger), CallbackAction {

        companion object : KLogging()

        override fun doToGatling(): ChainBuilder
                = chainOf(listOf(callbackHookOf(callback)))
    }
}