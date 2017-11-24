package com.grekov.translate.presentation.core.di.scope


import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy.RUNTIME
import javax.inject.Scope

@Scope
@Retention(RUNTIME)
annotation class PerActivity
