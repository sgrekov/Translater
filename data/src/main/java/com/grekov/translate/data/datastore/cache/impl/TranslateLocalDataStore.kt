package com.grekov.translate.data.datastore.cache.impl


import com.grekov.translate.data.datastore.cache.ITranslateLocalDataStore
import com.grekov.translate.data.mapper.LangDbToLangMapper
import com.grekov.translate.data.mapper.LangToLangDbMapper
import com.grekov.translate.data.mapper.PhraseDbToPhraseMapper
import com.grekov.translate.data.mapper.PhraseToPhraseDbMapper
import com.grekov.translate.data.model.cache.LangDbEntity
import com.grekov.translate.data.model.cache.PhraseDbEntity
import com.grekov.translate.domain.interactor.history.MakeFavoriteParams
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.domain.utils.getLangLiteral
import io.reactivex.*
import io.requery.Persistable
import io.requery.reactivex.ReactiveEntityStore
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class TranslateLocalDataStore
@Inject constructor(private val dataStore: ReactiveEntityStore<Persistable>,
                    private val langDbToLangMapper: LangDbToLangMapper,
                    private val langToLangDbMapper: LangToLangDbMapper,
                    private val phraseToPhraseDbMapper: PhraseToPhraseDbMapper,
                    private val phraseDbToPhraseMapper: PhraseDbToPhraseMapper,
                    @Named("IO") private val subscribeScheduler: Scheduler) : ITranslateLocalDataStore {

    override fun savePhrase(phrase: Phrase): Single<Phrase> {
        return Observable
                .just(phrase)
                .map { p -> phraseToPhraseDbMapper.transform(p) }
                .flatMap { pDb -> dataStore.upsert(pDb).toObservable() }
                .map { phrase }
                .singleOrError()
                .subscribeOn(subscribeScheduler)
    }

    override fun getPhrases(isFavorite: Boolean?, search: String?): Observable<List<Phrase>> {
        val query = if (search?.isEmpty() == true) "%%" else "%$search%"
        return if (isFavorite != null) {
            dataStore
                    .select(PhraseDbEntity::class.java)
                    .where(PhraseDbEntity.SOURCE.like(query))
                    .and(PhraseDbEntity.FAVORITE.eq(isFavorite))
                    .orderBy(PhraseDbEntity.CREATED.desc())
                    .get()
                    .observableResult()
                    .subscribeOn(subscribeScheduler)
                    .map { result -> result.toList() }
                    .map { phraseDbToPhraseMapper.transformList(it) }
        } else {
            dataStore
                    .select(PhraseDbEntity::class.java)
                    .where(PhraseDbEntity.SOURCE.like(query))
                    .orderBy(PhraseDbEntity.CREATED.desc())
                    .get()
                    .observableResult()
                    .subscribeOn(subscribeScheduler)
                    .map { result -> result.toList() }
                    .map { phraseDbToPhraseMapper.transformList(it) }
        }
    }

    override fun saveLangs(langs: List<Lang>): Single<List<Lang>> {
        return dataStore
                .upsert(langToLangDbMapper.transformList(langs))
                .map { langDbEntities -> langs }
                .subscribeOn(subscribeScheduler)
    }

    override fun getLangs(): Single<List<Lang>> {
        return dataStore
                .select(LangDbEntity::class.java)
                .get().observable().toList()
                .map { langDbToLangMapper.transformList(it) }
                .subscribeOn(subscribeScheduler)
    }

    override fun getLangByCode(code: String): Single<Lang> {
        return dataStore
                .select(LangDbEntity::class.java)
                .where(LangDbEntity.CODE.eq(code))
                .get()
                .observable()
                .subscribeOn(subscribeScheduler)
                .map { langDbToLangMapper.transform(it) }
                .singleOrError()
    }

    override fun clearPhrases(): Completable {
        return dataStore
                .delete(PhraseDbEntity::class.java)
                .get()
                .single()
                .subscribeOn(subscribeScheduler)
                .toCompletable()

    }

    override fun clearAllFavoritesPhrases(): Completable {
        return dataStore
                .update(PhraseDbEntity::class.java)
                .set(PhraseDbEntity.FAVORITE, false)
                .where(PhraseDbEntity.FAVORITE.eq(true))
                .get()
                .single()
                .subscribeOn(subscribeScheduler)
                .toCompletable()
    }

    override fun getPhrase(source: String, lang: String): Single<Phrase> {
        return getPhraseDb(source, lang)
                .map { phraseDb -> phraseDbToPhraseMapper.transform(phraseDb) }
                .toSingle()
                .subscribeOn(subscribeScheduler)
    }

    internal fun getPhraseDb(source: String, lang: String): Maybe<PhraseDbEntity> {
        return dataStore
                .select(PhraseDbEntity::class.java)
                .where(PhraseDbEntity.SOURCE.eq(source))
                .and(PhraseDbEntity.LANG.eq(lang))
                .get()
                .maybe()
                .subscribeOn(subscribeScheduler)
                .doOnSuccess {
                    Timber.d("success")
                }
                .doOnComplete {
                    Timber.d("complete")
                }
    }


    override fun makeFavoritePhrase(params: MakeFavoriteParams): Completable {
        return dataStore
                .update(PhraseDbEntity::class.java)
                .set(PhraseDbEntity.FAVORITE, params.favorite)
                .where(PhraseDbEntity.SOURCE.eq(params.text))
                .and(PhraseDbEntity.LANG.eq(getLangLiteral(params.from, params.to)))
                .get()
                .single()
                .subscribeOn(subscribeScheduler)
                .toCompletable()
    }

}
