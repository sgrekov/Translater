package com.grekov.translate.data.datastore.cache

import com.grekov.translate.domain.interactor.history.MakeFavoriteParams
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.domain.model.Phrase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface ITranslateLocalDataStore {

    fun savePhrase(phrase: Phrase): Single<Phrase>

    fun getPhrases(isFavorite: Boolean?, search: String?): Observable<List<Phrase>>

    fun saveLangs(langs: List<Lang>): Single<List<Lang>>

    fun getLangs(): Single<List<Lang>>

    fun clearPhrases(): Completable

    fun clearAllFavoritesPhrases(): Completable

    fun getPhrase(source: String, lang: String): Single<Phrase>

    fun makeFavoritePhrase(params: MakeFavoriteParams): Completable

    fun getLangByCode(code: String): Single<Lang>
}
