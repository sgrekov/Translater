package com.grekov.translate.presentation.core.elm

import com.grekov.translate.domain.elm.Idle
import com.grekov.translate.domain.elm.Msg
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

inline fun inView(crossinline operations : () -> Unit) : Single<Msg> {
    return Single.fromCallable {
        operations()
    }.subscribeOn(AndroidSchedulers.mainThread()).map { Idle }
}