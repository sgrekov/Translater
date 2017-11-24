package com.grekov.translate.presentation.history.view

import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.presentation.core.view.IBaseView

interface IHistoryView : IBaseView {
    fun setTitle(s: String)

    fun triggerEmptyText(visibility: Int)

    fun setPhrases(phrases: List<Phrase>)

    fun setFilterHint(hint: String)
    fun setSearchText(searchText: String)
}
