package com.grekov.translate.domain.interactor.history

import com.grekov.translate.domain.interactor.base.UseCaseCompletable
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.domain.repository.ITranslateRepository
import io.reactivex.Completable
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

data class MakeFavoriteParams(val text: String, val from: Lang, val to: Lang, val favorite : Boolean)

class MakeFavoriteUseCase @Inject constructor(@Named("Output") outputScheduler: Scheduler,
                                              private val translateRepository: ITranslateRepository)
    : UseCaseCompletable<MakeFavoriteParams>(outputScheduler) {
    override fun buildCompletable(params: MakeFavoriteParams): Completable {
        return translateRepository.makeFavoritePhrase(params)
    }
}