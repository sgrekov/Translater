package com.grekov.translate.domain.interactor.lang

import com.grekov.translate.domain.interactor.base.UseCaseSingle
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.domain.repository.ITranslateRepository
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Named

class LoadLangsByCodeUseCaseSingle
@Inject constructor(private var translateRepository: ITranslateRepository,
                    @Named("Output") outputScheduler: Scheduler )
    : UseCaseSingle<Pair<Lang, Lang>, String>(outputScheduler) {

    override fun buildSingle(params: String): Single<Pair<Lang, Lang>> {
        val codes = params.split("-")
        return translateRepository
                .getLangByCode(codes[0])
                .flatMap { from ->
                    translateRepository
                            .getLangByCode(codes[1])
                            .map { to -> Pair(from, to) }
                }
    }
}