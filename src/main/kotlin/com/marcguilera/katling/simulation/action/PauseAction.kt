package com.marcguilera.katling.simulation.action

import com.marcguilera.katling.core.ScalaDuration
import com.marcguilera.katling.core.toScala
import com.marcguilera.katling.expression.Expression
import com.marcguilera.katling.session.Session
import io.gatling.core.action.builder.PauseBuilder
import mu.KLogging
import scala.Some
import java.time.Duration

fun Actions.Configurer<*>.pause(expression: Expression<Duration>) {
    exec(PauseAction.of(expression))
}

/**
 * An [Action] which pauses the simulation for a given duration. The duration
 * may be static of computed from the state of the [Session] at execution time.
 */
interface PauseAction : Action {
    val duration: Expression<Duration>

    companion object Factory {
        @JvmStatic
        fun of(expression: Expression<Duration>): PauseAction
                = BuiltPauseAction(expression)
    }

    private data class BuiltPauseAction (
        override val duration: Expression<Duration>
    ) : AbstractBuiltAction(logger), PauseAction {

        companion object : KLogging()

        override fun doToGatling()
                = duration
                    .map { it.toScala() as ScalaDuration }
                    .let { listOf(PauseBuilder(it.toGatling(), Some.empty())) }
                    .toChain()
    }
}