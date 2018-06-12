package com.grekov.translate.domain.interactor.base

import com.grekov.translate.domain.elm.Msg
import io.reactivex.Observable
import io.reactivex.Scheduler

abstract class ElmSubscription<T : Msg, in PARAMS>(protected val outputScheduler: Scheduler) {
    private var observable: Observable<T>? = null
    private var params: PARAMS? = null

    protected abstract fun buildObservable(params: PARAMS): Observable<T>

    fun getObservable(params: PARAMS): Pair<Observable<T>, Boolean> {
        var recreated = false
        if (observable == null || this.params != null && this.params != params) {
            this.params = params
            recreated = true
            observable = buildObservable(params).observeOn(outputScheduler)
        }
        return Pair(observable!!, recreated)
    }

}
