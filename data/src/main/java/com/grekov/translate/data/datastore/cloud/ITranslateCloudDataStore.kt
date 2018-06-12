package com.grekov.translate.data.datastore.cloud

import com.grekov.translate.domain.model.Lang
import com.grekov.translate.domain.model.Phrase
import io.reactivex.Single

interface ITranslateCloudDataStore {

    fun getLangs(code: String): Single<List<Lang>>

    fun translate(text: String, lang: String): Single<Phrase>

}
