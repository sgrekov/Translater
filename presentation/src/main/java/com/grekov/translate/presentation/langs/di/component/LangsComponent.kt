package com.grekov.translate.presentation.langs.di.component

import com.grekov.translate.presentation.core.di.scope.PerScreen
import com.grekov.translate.presentation.langs.di.module.LangsModule
import com.grekov.translate.presentation.langs.view.controller.LangsController
import dagger.Subcomponent

@PerScreen
@Subcomponent(modules = arrayOf(LangsModule::class))
interface LangsComponent {

    fun inject(langsController: LangsController)

}
