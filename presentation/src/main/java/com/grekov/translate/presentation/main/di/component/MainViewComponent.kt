package com.grekov.translate.presentation.main.di.component


import com.grekov.translate.presentation.ConductorNavigator
import com.grekov.translate.presentation.Navigator
import com.grekov.translate.presentation.core.di.component.AppComponent
import com.grekov.translate.presentation.core.di.scope.PerActivity
import com.grekov.translate.presentation.history.di.component.HistoryComponent
import com.grekov.translate.presentation.history.di.module.HistoryModule
import com.grekov.translate.presentation.langs.di.component.LangsComponent
import com.grekov.translate.presentation.langs.di.module.LangsModule
import com.grekov.translate.presentation.main.di.module.HomeScreenModule
import com.grekov.translate.presentation.main.view.activity.MainActivity
import com.grekov.translate.presentation.translate.di.component.TranslateComponent
import com.grekov.translate.presentation.translate.di.module.TranslateModule
import dagger.Component
import dagger.Module
import dagger.Provides


@PerActivity
@Component(dependencies = arrayOf(AppComponent::class), modules = [(MainViewComponent.ActivityModule::class)])
interface MainViewComponent {

    @Module
    class ActivityModule(val activity: MainActivity) {

        @Provides
        @PerActivity
        fun navigator(): Navigator = ConductorNavigator(activity)
    }

    fun inject(mainActivity: MainActivity)

    fun plusHomeComponent(homeScreenModule: HomeScreenModule): HomeComponent

    fun plusTranslateComponent(translateModule: TranslateModule): TranslateComponent

    fun plusLangsComponent(langsModule: LangsModule): LangsComponent

    fun plusHistoryComponent(historyModule: HistoryModule): HistoryComponent
}
