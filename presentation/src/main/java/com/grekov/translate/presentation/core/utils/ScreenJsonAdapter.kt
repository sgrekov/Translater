package com.grekov.translate.presentation.core.utils

import com.grekov.translate.presentation.core.elm.Screen
import com.grekov.translate.presentation.main.presenter.Home
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class ScreenJsonAdapter {
    @FromJson
    fun screenFromJson(screen: String): Screen {
        return Home()
    }

    @ToJson
    fun screenToJson(screen: Screen): String {
        return screen.javaClass.simpleName
    }
}