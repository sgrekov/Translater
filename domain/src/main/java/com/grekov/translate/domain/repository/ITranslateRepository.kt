package com.grekov.translate.domain.repository

import com.grekov.translate.domain.interactor.history.MakeFavoriteParams
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.domain.model.Phrase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface ITranslateRepository {

    fun translatePhrase(phrase: Phrase): Single<Phrase>

    fun getFavoritePhrases(search: String): Observable<List<Phrase>>

    fun getPhrasesHistory(search: String): Observable<List<Phrase>>

    fun makeFavoritePhrase(params: MakeFavoriteParams): Completable

    val langsFromCache: Single<List<Lang>>

    val langsFromCloud: Single<List<Lang>>

    fun loadPhrase(phrase: String, code: String): Single<Phrase>

    fun clearPhrases(isFavorite: Boolean?): Completable

    fun getLangByCode(code: String): Single<Lang>
}
