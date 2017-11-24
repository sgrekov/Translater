package com.grekov.translate.presentation.translate.di.component

import com.grekov.translate.presentation.core.di.scope.PerScreen
import com.grekov.translate.presentation.translate.di.module.TranslateModule
import com.grekov.translate.presentation.translate.view.controller.TranslateController
import dagger.Subcomponent

@PerScreen
@Subcomponent(modules = arrayOf(TranslateModule::class))
interface TranslateComponent {

    fun inject(translateController: TranslateController)
}
