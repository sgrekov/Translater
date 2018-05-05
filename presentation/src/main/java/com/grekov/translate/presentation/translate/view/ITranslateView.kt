package com.grekov.translate.presentation.translate.view

import com.grekov.translate.presentation.core.view.IBaseView

interface ITranslateView : IBaseView {
    fun showFromText(enabled: Boolean)
    fun showToText(enabled: Boolean)
    fun setFromLang(langName: String)
    fun setToLang(langName: String)
    fun setText(text: String)
    fun showProgress(show: Boolean)
    fun setSourceText(source: String)
    fun setTranslatedText(translate: String)
    fun showSourceText(show: Boolean)
    fun showTranslateText(show: Boolean)
    fun setFavoriteBtn(favorite: Boolean)
    fun showFavoriteBtn(show: Boolean)

}
