package com.grekov.translate.data.repository


import com.grekov.translate.data.datastore.cache.impl.TranslateLocalDataStore
import com.grekov.translate.data.datastore.cloud.impl.TranslateCloudDataStore
import com.grekov.translate.domain.interactor.history.MakeFavoriteParams
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.domain.repository.ITranslateRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*


class TranslateRepository(private val localDataStore: TranslateLocalDataStore,
                          private val cloudDataStore: TranslateCloudDataStore) : ITranslateRepository {

    override fun translatePhrase(phrase: Phrase): Single<Phrase> {
        return cloudDataStore.translate(phrase.source, phrase.langsLiteral)
                .flatMap { localDataStore.savePhrase(it) }
    }

    override fun getFavoritePhrases(search: String): Observable<List<Phrase>> {
        return localDataStore.getPhrases(true, search)
    }

    override fun getPhrasesHistory(search: String): Observable<List<Phrase>> {
        return localDataStore.getPhrases(null, search)
    }

    override fun makeFavoritePhrase(params: MakeFavoriteParams): Completable {
        return localDataStore.makeFavoritePhrase(params)
    }

    override val langsFromCloud: Single<List<Lang>>
        get() = cloudDataStore.getLangs(Locale.getDefault().language)
                .flatMap { localDataStore.saveLangs(it) }

    override fun loadPhrase(phrase: String, code: String): Single<Phrase> {
        return localDataStore.getPhrase(phrase, code)
    }

    override fun clearPhrases(isFavorite: Boolean?): Completable {
        return if (isFavorite != null && isFavorite) {
            localDataStore.clearAllFavoritesPhrases()
        } else localDataStore.clearPhrases()
    }

    override val langsFromCache: Single<List<Lang>>
        get() = localDataStore.getLangs()

    override fun getLangByCode(code: String): Single<Lang> {
        return localDataStore.getLangByCode(code)
    }

}
