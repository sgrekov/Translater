package com.grekov.translate.data.datastore.cloud.impl

import com.grekov.translate.data.BuildConfig
import com.grekov.translate.data.api.TranslateService
import com.grekov.translate.data.datastore.cloud.ITranslateCloudDataStore
import com.grekov.translate.data.mapper.LangApiToLangMapper
import com.grekov.translate.data.mapper.PhraseApiToPhraseMapper
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.domain.model.Phrase
import io.reactivex.Scheduler
import io.reactivex.Single

import javax.inject.Inject
import javax.inject.Named

class TranslateCloudDataStore @Inject
constructor(private val service: TranslateService,
            private val langApiToLangMapper: LangApiToLangMapper,
            private val phraseApiToPhraseMapper: PhraseApiToPhraseMapper,
            @Named("IO") private val subscribeScheduler: Scheduler) : ITranslateCloudDataStore {

    override fun getLangs(code: String): Single<List<Lang>> {
        return service
                .getLangs(code, BuildConfig.API_TOKEN)
                .map { langApiToLangMapper.transform(it) }
                .subscribeOn(subscribeScheduler)
    }

    override fun translate(text: String, lang: String): Single<Phrase> {
        return service.translate(lang, "ru", BuildConfig.API_TOKEN, text)
                .map { response -> phraseApiToPhraseMapper.transform(response, text) }
                .subscribeOn(subscribeScheduler)
    }
}
