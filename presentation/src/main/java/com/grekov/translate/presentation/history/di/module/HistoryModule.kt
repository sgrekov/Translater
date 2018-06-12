package com.grekov.translate.presentation.history.di.module

import com.grekov.translate.domain.IResourceManager
import com.grekov.translate.domain.interactor.history.HistoryClearUseCase
import com.grekov.translate.domain.interactor.history.HistoryPhrasesSub
import com.grekov.translate.presentation.core.di.scope.PerScreen
import com.grekov.translate.presentation.core.elm.Program
import com.grekov.translate.presentation.core.elm.TimeTraveller
import com.grekov.translate.presentation.history.presenter.HistoryPresenter
import com.grekov.translate.presentation.history.view.IHistoryView
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers

@Module
class HistoryModule(internal var view: IHistoryView, internal var isFavorite: Boolean) {

    @Provides
    @PerScreen
    fun provideHistoryPresenter(resourceManager: IResourceManager,
                                timeTraveller : TimeTraveller,
                                historyPhrasesSub: HistoryPhrasesSub,
                                historyClearUseCase: HistoryClearUseCase): HistoryPresenter {
        return HistoryPresenter(view,
                Program(AndroidSchedulers.mainThread(), timeTraveller),
                isFavorite,
                resourceManager,
                historyPhrasesSub,
                historyClearUseCase)
    }
}
