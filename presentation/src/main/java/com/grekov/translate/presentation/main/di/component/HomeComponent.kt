package com.grekov.translate.presentation.main.di.component

import com.grekov.translate.presentation.core.di.scope.PerScreen
import com.grekov.translate.presentation.main.di.module.HomeScreenModule
import com.grekov.translate.presentation.main.view.controller.HomeController
import dagger.Subcomponent

@PerScreen
@Subcomponent(modules = arrayOf(HomeScreenModule::class))
interface HomeComponent {

    fun inject(homeController: HomeController)

}
