package com.grekov.translate.presentation.translate.di.module

import com.grekov.translate.domain.IAppPreferencesManager
import com.grekov.translate.domain.interactor.history.CheckFavoriteUseCase
import com.grekov.translate.domain.interactor.history.MakeFavoriteUseCase
import com.grekov.translate.domain.interactor.lang.LoadLangsByCodeUseCaseSingle
import com.grekov.translate.domain.interactor.translate.MakeTranslateUseCase
import com.grekov.translate.presentation.Navigator
import com.grekov.translate.presentation.core.di.scope.PerScreen
import com.grekov.translate.presentation.core.elm.Program
import com.grekov.translate.presentation.core.elm.TimeTraveller
import com.grekov.translate.presentation.translate.presenter.TranslatePresenter
import com.grekov.translate.presentation.translate.view.ITranslateView
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers

@Module
class TranslateModule(internal var view: ITranslateView) {

    @Provides
    @PerScreen
    fun provideTranslatePresenter(appPreferencesManager: IAppPreferencesManager,
                                  timeTraveller : TimeTraveller,
                                  navigator: Navigator,
                                  makeTranslateUseCase: MakeTranslateUseCase,
                                  makeFavoriteUseCase: MakeFavoriteUseCase,
                                  checkFavoriteUseCase: CheckFavoriteUseCase,
                                  loadLangsByCodeUseCaseSingle: LoadLangsByCodeUseCaseSingle): TranslatePresenter {
        return TranslatePresenter(
                view,
                Program(AndroidSchedulers.mainThread(), timeTraveller),
                appPreferencesManager,
                navigator,
                makeTranslateUseCase,
                makeFavoriteUseCase,
                checkFavoriteUseCase,
                loadLangsByCodeUseCaseSingle)
    }
}
