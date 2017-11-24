package com.grekov.translate.domain

import com.grekov.translate.domain.model.Lang
import io.reactivex.Completable
import io.reactivex.Single


interface IAppPreferencesManager {

    fun getLangs() : Single<Pair<Lang, Lang>>

    fun saveLangs(from: Lang, to : Lang) : Completable
}
