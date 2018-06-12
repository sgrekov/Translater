package com.grekov.translate.domain.interactor.base

import io.reactivex.Completable
import io.reactivex.Scheduler

abstract class UseCaseCompletable<in P>(private val outputScheduler: Scheduler) {

    protected abstract fun buildCompletable(params: P): Completable

    fun getCompletable(params: P): Completable {
        return buildCompletable(params).observeOn(outputScheduler)
    }

}