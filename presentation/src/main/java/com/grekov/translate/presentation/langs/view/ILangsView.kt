package com.grekov.translate.presentation.langs.view

import com.grekov.translate.domain.model.Lang
import com.grekov.translate.presentation.core.view.IBaseView

interface ILangsView : IBaseView {
    fun showLangs(langs: List<Lang>)

    fun showProgress()

    fun hideProgress()

    fun showTitle()

    fun showUpdateTitle()

    fun selectLang(model: Lang, isFrom: Boolean)

    fun setErrorTextToEmpty()

    fun showErrorText(show: Boolean)

    fun back()
}
