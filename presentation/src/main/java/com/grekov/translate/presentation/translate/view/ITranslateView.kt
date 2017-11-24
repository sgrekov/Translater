package com.grekov.translate.presentation.translate.view

import com.grekov.translate.presentation.core.view.IBaseView

interface ITranslateView : IBaseView {
    fun triggerFromText(enabled: Boolean)
    fun triggerToText(enabled: Boolean)
    fun setFromLang(langName: String)
    fun setToLang(langName: String)
    fun navigate(from: Boolean)
    fun setText(text: String)
    fun triggerProgress(visibility: Int)
    fun setSource(source: String)
    fun setTranslate(translate: String)
    fun triggerSource(visibility: Int)
    fun triggerTranslate(visibility: Int)
    fun setFavoriteBtn(favorite: Boolean)
    fun triggerFavoriteBtn(visibility: Int)

}
