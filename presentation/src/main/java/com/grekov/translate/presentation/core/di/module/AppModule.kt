package com.grekov.translate.presentation.core.di.module

import android.content.Context
import com.grekov.translate.domain.IAppPreferencesManager
import com.grekov.translate.domain.IResourceManager
import com.grekov.translate.presentation.TranslateApp
import com.grekov.translate.presentation.core.elm.TimeTraveller
import com.grekov.translate.presentation.core.framework.AndroidResourceManager
import com.grekov.translate.presentation.core.utils.AppPreferencesManager
import com.grekov.translate.presentation.core.utils.ScreenJsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named
import javax.inject.Singleton


@Module
class AppModule(private val app: TranslateApp) {

    val timeTraveller: TimeTraveller = TimeTraveller()

    @Provides
    @Singleton
    fun provideApplication(): TranslateApp {
        return app
    }

    @Provides
    @Singleton
    @Named("PerApplication")
    fun provideContext(): Context {
        return app
    }

    @Provides
    @Singleton
    fun provideResource(@Named("PerApplication") context: Context): IResourceManager {
        return AndroidResourceManager(context)
    }

    @Provides
    @Named("Output")
    fun provideOutputScheduler(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    @Provides
    @Named("IO")
    fun provideIOScheduler(): Scheduler {
        return Schedulers.io()
    }

    @Provides
    @Singleton
    fun provideAppPreferences(@Named("PerApplication") context: Context): IAppPreferencesManager {
        return AppPreferencesManager(context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE))
    }

    @Provides
    @Singleton
    fun provideTimeTraveller(): TimeTraveller {
        return timeTraveller
    }

    @Provides
    @Singleton
    @Named("presentation")
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
                .add(ScreenJsonAdapter())
                .build()
    }

}
