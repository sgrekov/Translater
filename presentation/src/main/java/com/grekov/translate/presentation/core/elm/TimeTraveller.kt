package com.grekov.translate.presentation.core.elm

import com.grekov.translate.domain.elm.Msg
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

data class ConsoleTimeRecord(val screen: Screen, val msg: String, val state: String)
data class TimeRecord(val screen: Screen, val msg: Msg, val state: State)

class TimeTraveller {

    val consoleRecords: MutableList<ConsoleTimeRecord> = mutableListOf()
    val records: MutableList<TimeRecord> = mutableListOf()

    val stateRelay: BehaviorRelay<Pair<Screen, State>> = BehaviorRelay.create()

    var adventureMode: Boolean = false

    fun startTravel(step: Int, slow: Boolean) {
        adventureMode = true
        stateRelay.accept(Pair(Start(), State(Start())))
        val delay: Long = if (slow) 1000 else 20
        Observable
                .fromIterable(
                        records
                                .filterIndexed { index, _ -> index <= step }
                )
                .concatMap { i ->
                    Observable.just(i).delay(delay, TimeUnit.MILLISECONDS)
                }
                .subscribe { (screen, _, state) ->
                    stateRelay.accept(Pair(screen, state))
                }
    }

}