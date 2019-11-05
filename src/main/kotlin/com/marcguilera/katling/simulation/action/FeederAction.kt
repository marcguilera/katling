package com.marcguilera.katling.simulation.action

import com.marcguilera.katling.expression.Expression
import com.marcguilera.katling.expression.expressionOf
import com.marcguilera.katling.session.feeder.Feeder
import io.gatling.core.action.builder.FeedBuilder
import io.gatling.core.structure.ChainBuilder
import mu.KLogging
import scala.Function0

fun Actions.Configurer<*>.feed(feeder: Feeder<*>, number: Expression<Int> = FeederAction.ONE) {
    exec(FeederAction.of(feeder, number))
}

interface FeederAction<T> : Action {

    /**
     * The feeder to pull data from.
     */
    val feeder: Feeder<T>

    /**
     * Number of records to pull each time.
     */
    val number: Expression<Int>

    companion object {

        internal val ONE = expressionOf(1)

        @JvmStatic
        fun <T> of(feeder: Feeder<T>, number: Expression<Int> = ONE): FeederAction<T>
                = BuiltFeederAction(feeder, number)
    }

    private data class BuiltFeederAction <T> (
        override val feeder: Feeder<T>,
        override val number: Expression<Int>
    ) : AbstractBuiltAction(logger), FeederAction<T> {

        companion object : KLogging()

        override fun doToGatling(): ChainBuilder {
            val items = number
                .map { it as Any }
                .toGatling()

            val feed = feeder
                .map { it.mapValues { entry -> entry.value as Any } }
                .toGatling()

            return listOf(FeedBuilder(Function0{feed}, items))
                .toChain()
        }
    }
}