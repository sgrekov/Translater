package com.grekov.translate.domain.interactor.base

import io.reactivex.Scheduler
import io.reactivex.Single

abstract class UseCaseSingle<T, in P>(protected val outputScheduler: Scheduler) {

    protected abstract fun buildSingle(params: P): Single<T>

    fun getSingle(params: P): Single<T> {
        return buildSingle(params).observeOn(outputScheduler)
    }

}
