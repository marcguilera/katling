package com.marcguilera.katling.simulation.action

import com.marcguilera.katling.expression.Expression
import com.marcguilera.katling.session.Session
import mu.KLogging

fun Actions.Configurer<*>.session(expression: Expression<Session>) {
    exec(SessionAction.of(expression))
}

/**
 * An [Action] which is able to read and write to the [Session]. Handy
 * for keeping state for each user.
 */
interface SessionAction : Action {

    val expression: Expression<Session>

    companion object Factory {
        @JvmStatic
        fun of(expression: Expression<Session>): SessionAction
                = BuiltSessionAction(expression)
    }

    private data class BuiltSessionAction (
        override val expression: Expression<Session>
    ) : AbstractBuiltAction(logger), SessionAction {

        companion object : KLogging()

        override fun doToGatling()
                = listOf(sessionHookOf(expression))
                    .toChain()
    }
}