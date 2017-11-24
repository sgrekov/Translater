package com.grekov.translate.data.di


import com.grekov.translate.data.datastore.cache.impl.TranslateLocalDataStore
import com.grekov.translate.data.datastore.cloud.impl.TranslateCloudDataStore
import com.grekov.translate.data.repository.TranslateRepository
import com.grekov.translate.domain.repository.ITranslateRepository
import dagger.Module
import dagger.Provides

import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideProfileRepository(translateLocalDataStore: TranslateLocalDataStore,
                                 translateCloudDataStore: TranslateCloudDataStore): ITranslateRepository {
        return TranslateRepository(translateLocalDataStore, translateCloudDataStore)
    }


}
