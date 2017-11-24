package com.grekov.translate.domain.interactor.history

import com.grekov.translate.domain.interactor.base.UseCaseCompletable
import com.grekov.translate.domain.repository.ITranslateRepository
import io.reactivex.Completable
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class HistoryClearUseCase @Inject constructor(@Named("Output") outputScheduler: Scheduler,
                                              private val translateRepository: ITranslateRepository)
    : UseCaseCompletable<Boolean?>(outputScheduler) {
    override fun buildCompletable(isFavorite: Boolean?): Completable {
        return translateRepository.clearPhrases(isFavorite)
    }
}