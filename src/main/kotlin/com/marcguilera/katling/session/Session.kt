package com.marcguilera.katling.session

import com.marcguilera.katling.core.toKotlin
import com.marcguilera.katling.core.toScala
import com.marcguilera.katling.simulation.action.SessionAction
import scala.Tuple2
import java.time.Instant

typealias GatlingSession = io.gatling.core.session.Session

/**
 * Only permits immutable operation for the session.
 */
interface ImmutableSession {
    val scenario: String
    val userId: Long
    val startedAt: Instant
    fun getAll(): Map<String, Any>
    fun get(key: String): Any?
    fun toGatling(): GatlingSession

    companion object Factory {
        @JvmStatic
        fun of(session: GatlingSession): ImmutableSession
                = Session.of(session)
    }
}

/**
 * A session is essentially a map each user has keeping state of
 * his run. It's handy for keeping values relevant for each run.
 *
 * Keep in mind that the [Session] itself is immutable and each
 * mutable operation will return a copy so it's meant to be used
 * inside a handler, ie: [SessionAction]
 */
interface Session : ImmutableSession {
    fun set(key: String, value: Any): Session
    fun setAll(map: Map<String, Any>): Session
    fun remove(key: String): Session
    fun removeAll(keys: Iterable<String>): Session
    fun contains(key: String): Boolean
    fun clear(): Session

    companion object Factory {
        @JvmStatic
        fun of(session: GatlingSession): Session
                = BuiltSession(session)
    }

    private data class BuiltSession (
        private val session: GatlingSession
    ) : Session {

        override val scenario: String
            get() =  session.scenario()

        override val startedAt: Instant
            get() = Instant.ofEpochSecond(session.startDate())

        override val userId: Long
            get() = session.userId()

        override fun getAll(): Map<String, Any>
                = session.attributes().toKotlin()

        override fun get(key: String)
                = session.attributes().getOrElse<Any?>(key,null)

        override fun contains(key: String)
                = session.contains(key)

        override fun clear(): Session
                = cpy(session.reset())

        override fun set(key: String, value: Any)
                = cpy(session.set(key, value))

        override fun setAll(map: Map<String, Any>)
                = cpy(session.setAll(map.map { Tuple2(it.key, it.value) }.toScala()))

        override fun removeAll(keys: Iterable<String>)
                = cpy(session.removeAll(keys.toScala().toSeq()))

        override fun remove(key: String)
                = cpy(session.remove(key))

        override fun toGatling()
                = session

        private fun cpy(session: GatlingSession)
                = BuiltSession(session)
    }
}