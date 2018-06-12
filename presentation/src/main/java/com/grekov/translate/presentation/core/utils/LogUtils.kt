package com.grekov.translate.presentation.core.utils

import com.grekov.translate.BuildConfig

inline fun lazyLog(log : () -> Unit) {
    if (BuildConfig.DEBUG){
        log()
    }
}