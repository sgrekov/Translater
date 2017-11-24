package com.grekov.translate.domain.interactor.history

import com.grekov.translate.domain.interactor.base.UseCaseSingle
import com.grekov.translate.domain.repository.ITranslateRepository
import com.grekov.translate.domain.utils.getLangLiteral
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Named

class CheckFavoriteUseCase @Inject constructor(@Named("Output") scheduler: Scheduler,
                                               private val translateRepository: ITranslateRepository)
    : UseCaseSingle<Boolean, MakeFavoriteParams>(scheduler) {


    override fun buildSingle(params: MakeFavoriteParams): Single<Boolean> {
        return translateRepository
                .loadPhrase(params.text, getLangLiteral(params.from, params.to))
                .map { phrase -> phrase.favorite }
                .onErrorReturn { false }
    }

}