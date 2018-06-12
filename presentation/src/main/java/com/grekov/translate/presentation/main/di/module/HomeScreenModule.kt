package com.grekov.translate.presentation.main.di.module

import com.grekov.translate.presentation.core.di.scope.PerScreen
import com.grekov.translate.presentation.core.elm.Program
import com.grekov.translate.presentation.core.elm.TimeTraveller
import com.grekov.translate.presentation.main.presenter.HomePresenter
import com.grekov.translate.presentation.main.view.IHomeView
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers

@Module
class HomeScreenModule(internal var view: IHomeView) {

    @Provides
    @PerScreen
    fun provideHomePresenter(timeTraveller: TimeTraveller): HomePresenter {
        return HomePresenter(view, Program(AndroidSchedulers.mainThread(), timeTraveller))
    }
}

