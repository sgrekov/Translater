package com.grekov.translate.presentation.langs.di.module

import com.grekov.translate.domain.interactor.lang.GetLangsUseCase
import com.grekov.translate.presentation.core.di.scope.PerScreen
import com.grekov.translate.presentation.core.elm.Program
import com.grekov.translate.presentation.core.elm.TimeTraveller
import com.grekov.translate.presentation.langs.presenter.LangsPresenter
import com.grekov.translate.presentation.langs.view.ILangsView
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers

@Module
class LangsModule(internal var view: ILangsView, val isFrom: Boolean) {

    @Provides
    @PerScreen
    fun provideLangsPresenter(timeTraveller: TimeTraveller, getLangsUseCase: GetLangsUseCase): LangsPresenter {
        return LangsPresenter(view, Program(AndroidSchedulers.mainThread(), timeTraveller), getLangsUseCase, isFrom)
    }
}
