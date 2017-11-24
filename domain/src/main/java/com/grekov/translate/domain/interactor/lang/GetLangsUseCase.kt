package com.grekov.translate.domain.interactor.lang

import com.grekov.translate.domain.interactor.base.UseCaseCachePolicy
import com.grekov.translate.domain.interactor.base.UseCaseSingle
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.domain.repository.ITranslateRepository
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Named

class GetLangsUseCase @Inject
constructor(@Named("Output") outputScheduler: Scheduler,
            private var translateRepository: ITranslateRepository)
    : UseCaseSingle<List<Lang>, UseCaseCachePolicy>(outputScheduler) {

    override fun buildSingle(params: UseCaseCachePolicy): Single<List<Lang>> = when (params) {
        UseCaseCachePolicy.CacheOnly -> translateRepository.langsFromCache
        UseCaseCachePolicy.CloudFirst -> translateRepository.langsFromCloud
        else -> translateRepository.langsFromCloud
    }
}