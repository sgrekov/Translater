package com.grekov.translate.presentation.core.view

import com.grekov.translate.presentation.core.presenter.IBasePresenter

interface IBaseView {

    fun getPresenter(): IBasePresenter?

    fun isAttached() : Boolean

}
