package com.marcguilera.katling.session.feeder

import com.marcguilera.katling.core.*

typealias FeederItem<T> = Map<String, T>
typealias GatlingFeeder<T> = ScalaIterator<ScalaMap<String, T>>

fun <T> feederOf(items: Iterable<FeederItem<T>>)
        = Feeder.iterate(items)

fun <T> feederOf(callback: () -> FeederItem<T>?)
        = Feeder.delegate(callback)

/**
 * A feeder allows you to define chains of data going into
 * the session.
 */
interface Feeder<T> {

    fun <O> map(mapper: (FeederItem<T>) -> FeederItem<O>): Feeder<O>

    /**
     * When it finishes it starts over.
     */
    fun circular(): Feeder<T>

    /**
     * When it finishes it goes back to the beginning.
     */
    fun bounce(): Feeder<T>

    /**
     * Converts this [Feeder] into its gatling counterpart.
     */
    fun toGatling(): GatlingFeeder<T>

    companion object {
        /**
         * Creates a feeder based on the given [Iterable].
         */
        @JvmStatic
        fun <T> iterate(items: Iterable<FeederItem<T>>) : Feeder<T>
                = IterableFeeder(items)

        /**
         * Creates a feeder which calls the given function on every
         * invocation. If the callback returns `null`, the feeder ends.
         */
        @JvmStatic
        fun <T> delegate(callback: () -> FeederItem<T>?) : Feeder<T>
                = IterableFeeder(generateSequence(callback).asIterable())
    }

    private class IterableFeeder<T> (
        val iterable: Iterable<FeederItem<T>>
    ) : Feeder<T> {

        override fun <O> map(mapper: (FeederItem<T>) -> FeederItem<O>): Feeder<O>
                = iterable
                    .asSequence()
                    .map(mapper)
                    .asIterable()
                    .let(::IterableFeeder)

        override fun circular(): Feeder<T>
                = iterable
                    .repeat()
                    .let(::IterableFeeder)

        override fun bounce(): Feeder<T>
                = iterable
                    .bounce()
                    .let(::IterableFeeder)

        override fun toGatling()
                = iterable
                    .map { it.toScala().toMap() }
                    .iterator()
                    .toScala()
    }
}