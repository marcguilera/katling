package com.marcguilera.katling.core

import scala.collection.JavaConverters
import scala.compat.java8.DurationConverters
import java.time.Duration

typealias ScalaFiniteDuration = scala.concurrent.duration.FiniteDuration
typealias ScalaDuration = scala.concurrent.duration.Duration
typealias ScalaMutableMap<K, V> = scala.collection.mutable.Map<K, V>
typealias ScalaMap<K, V> = scala.collection.immutable.Map<K, V>
typealias ScalaList<T> = scala.collection.immutable.List<T>
typealias ScalaIterator<T> = scala.collection.Iterator<T>
typealias ScalaIterable<T> = scala.collection.Iterable<T>

fun Duration.toScala(): ScalaFiniteDuration
        = DurationConverters.toScala(this)

fun <T> Iterable<T>.toScala(): ScalaIterable<T>
        = JavaConverters.iterableAsScalaIterable(this)

fun <T> Iterator<T>.toScala(): ScalaIterator<T>
        = JavaConverters.asScalaIterator(this)

fun <K, V> Map<K,V>.toScala(): ScalaMutableMap<K, V>
        = JavaConverters.mapAsScalaMap(this)

fun <K, V> ScalaMap<K, V>.toKotlin()
        = JavaConverters.mapAsJavaMap(this).toMap()

fun <T> ScalaList<T>.toKotlin()
        = JavaConverters.seqAsJavaList(toList())

fun <K, V> ScalaMutableMap<K, V>.toMap(): ScalaMap<K, V>
        = scala.collection.immutable.`Map$`.`MODULE$`.empty<K, V>().`$plus$plus`(this)