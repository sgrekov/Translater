package com.grekov.translate.presentation.main.view

import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.presentation.core.view.IBaseView

interface IHomeView : IBaseView {

    fun setTab(tabNum: Int)

    fun showTranslate(selectedPhrase: Phrase?)

    fun showHistory()

    fun showFavorites()
}
