package com.marcguilera.katling.simulation.action

import com.marcguilera.katling.core.toKotlin
import com.marcguilera.katling.core.toScala
import com.marcguilera.katling.core.wrap
import com.marcguilera.katling.expression.Expression
import com.marcguilera.katling.expression.expressionOf
import com.marcguilera.katling.session.Session
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.action.builder.SessionHookBuilder
import io.gatling.core.structure.ChainBuilder
import mu.KLogger

internal fun chainOf(builders: Iterable<ActionBuilder>)
        = builders.toList().toScala().toList().let(::ChainBuilder)

internal fun Action.builders()
        = toGatling().builders()

internal fun ChainBuilder.builders(): Iterable<ActionBuilder>
        = actionBuilders().toKotlin()

internal fun Iterable<Action>.builders(): Iterable<ActionBuilder>
        = map { it.builders() }.flatten()

internal fun Iterable<ActionBuilder>.toChain()
        = let(::chainOf)

internal fun sessionHookOf(delegate: (Session) -> Session)
        = sessionHookOf(expressionOf(delegate))

internal fun sessionHookOf(expression: Expression<Session>)
        = expression
            .map { it.toGatling() }
            .toGatling()
            .let { SessionHookBuilder(it, true) }

internal fun callbackHookOf(handler: (Session) -> Any)
        = sessionHookOf { handler(it); it }