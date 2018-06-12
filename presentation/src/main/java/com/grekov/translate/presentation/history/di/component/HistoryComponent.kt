package com.grekov.translate.presentation.history.di.component


import com.grekov.translate.presentation.core.di.scope.PerScreen
import com.grekov.translate.presentation.history.di.module.HistoryModule
import com.grekov.translate.presentation.history.view.controller.HistoryController
import dagger.Subcomponent

@PerScreen
@Subcomponent(modules = arrayOf(HistoryModule::class))
interface HistoryComponent {

    fun inject(historyController: HistoryController)

}
