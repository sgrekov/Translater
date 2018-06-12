package com.grekov.translate.data.di

import android.content.Context
import com.grekov.translate.data.BuildConfig
import com.grekov.translate.data.model.cache.Models
import dagger.Module
import dagger.Provides
import io.requery.Persistable
import io.requery.android.sqlite.DatabaseSource
import io.requery.reactivex.ReactiveEntityStore
import io.requery.reactivex.ReactiveSupport
import io.requery.sql.EntityDataStore
import io.requery.sql.TableCreationMode
import javax.inject.Named
import javax.inject.Singleton

@Module
class CacheModule {


    @Singleton
    @Provides
    fun provideDatastore(@Named("PerApplication") context: Context): ReactiveEntityStore<Persistable> {
        // override onUpgrade translate handle migrating translate a new version
        val source = DatabaseSource(context, Models.DEFAULT, DATABASE_FILE_NAME, 1)
        if (BuildConfig.DEBUG) {
            source.setTableCreationMode(TableCreationMode.DROP_CREATE)
        }
        val configuration = source.configuration
        return ReactiveSupport.toReactiveStore(
                EntityDataStore(configuration))
    }

    companion object {
        val DATABASE_FILE_NAME = "cache.db"
    }


}
