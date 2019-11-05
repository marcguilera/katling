package com.marcguilera.katling.core

/**
 * Convenience method to add a head and a tail to the this [Iterable]
 */
internal fun <T> Iterable<T>.wrap(before: T, after: T)
        = mutableListOf(before).also { it.addAll(this); it.add(after) }.asIterable()

/**
 * Convenience method to append many elements to this one.
 */
internal fun <T> T.join(vararg next: T)
        = mutableListOf(this).also { it.addAll(next) }.asIterable()

internal fun <T> Iterable<T>.repeat()
        = generateSequence(this) { it }.flatten().asIterable()

internal fun <T> Iterable<T>.bounce(): Iterable<T> {
    var i = 0L
    return generateSequence(this) { if (i++ % 2 == 0L) it else it.reversed() }.flatten().asIterable()
}