package com.grekov.translate.domain.interactor.translate

import com.grekov.translate.domain.interactor.base.UseCaseSingle
import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.domain.repository.ITranslateRepository
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Named

class MakeTranslateUseCase @Inject
constructor(@Named("Output") outputScheduler: Scheduler,
            @Named("IO") protected val scheduler: Scheduler,
            private val repository: ITranslateRepository) : UseCaseSingle<Phrase, Phrase>(outputScheduler) {

    override fun buildSingle(params: Phrase): Single<Phrase> {
        return repository
                .translatePhrase(params)
                .subscribeOn(scheduler)
    }
}
