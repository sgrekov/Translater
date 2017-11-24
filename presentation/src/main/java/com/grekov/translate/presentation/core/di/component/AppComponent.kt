package com.grekov.translate.presentation.core.di.component

import android.content.Context
import com.grekov.translate.data.di.ApiModule
import com.grekov.translate.data.di.CacheModule
import com.grekov.translate.data.di.RepositoryModule
import com.grekov.translate.domain.IAppPreferencesManager
import com.grekov.translate.domain.IResourceManager
import com.grekov.translate.domain.repository.ITranslateRepository
import com.grekov.translate.presentation.TranslateApp
import com.grekov.translate.presentation.core.di.module.AppModule
import com.grekov.translate.presentation.core.elm.TimeTraveller
import dagger.Component
import io.reactivex.Scheduler
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, ApiModule::class, RepositoryModule::class, CacheModule::class))
interface AppComponent {

    @get:Named("PerApplication")
    val context: Context

    val appPreferences: IAppPreferencesManager

    @Named("Output")
    fun provideOutputScheduler(): Scheduler

    @Named("IO")
    fun provideIOScheduler(): Scheduler

    fun provideTimeTraveller(): TimeTraveller

    val resourcesManager: IResourceManager

    val profileRepository: ITranslateRepository

    fun inject(translateApp: TranslateApp)
}