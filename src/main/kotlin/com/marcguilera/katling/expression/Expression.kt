package com.marcguilera.katling.expression

import com.marcguilera.katling.session.GatlingSession
import com.marcguilera.katling.session.Session
import io.gatling.commons.validation.Success
import io.gatling.commons.validation.Validation

typealias GatlingExpression<T> = scala.Function1<GatlingSession, Validation<T>>

fun <T> expressionOf(constant: T)
        = Expression.of(constant)

fun <T> expressionOf(delegate: (Session) -> T)
        = Expression.of(delegate)

/**
 * Represents a Gatling expression, which is nothing more
 * than a validated scala function taking the session
 * as a parameter and returning it or something else.
 */
interface Expression<T> {

    /**
     * Computes the result of the expression.
     */
    operator fun invoke(session: Session): T

    /**
     * Converts the result into something else. Usually
     * handy to interact internally with Scala/Gatling specific types.
     */
    fun <O> map(mapper: (T) -> O): Expression<O>

    /**
     * Converts this [Expression] into the equivalent Gatling one.
     */
    fun toGatling(): GatlingExpression<T>

    companion object Factory {
        @JvmStatic
        fun <T> of(constant: T): Expression<T>
                = ConstantExpression(constant)
        @JvmStatic
        fun <T> of(delegate: (Session) -> T): Expression<T>
                = DelegateExpression(delegate)
    }

    private open class DelegateExpression<T> (
        val delegate: (Session) -> T
    ) : Expression<T> {
        override fun invoke(session: Session)
                = delegate(session)
        override fun <O> map(mapper: (T) -> O)
                = DelegateExpression { delegate(it).let(mapper) }
        override fun toGatling()
                = GatlingExpression<T> { Success(Session.of(it).let(delegate)) }
    }

    private class ConstantExpression<T> (
        val constant: T
    ) : Expression<T> {
        override fun invoke(session: Session)
                = constant
        override fun <O> map(mapper: (T) -> O)
                = ConstantExpression(constant.let(mapper))
        override fun toGatling()
                = GatlingExpression<T> { Success(constant) }
    }
}