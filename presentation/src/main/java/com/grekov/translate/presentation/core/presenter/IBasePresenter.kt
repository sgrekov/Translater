package com.grekov.translate.presentation.core.presenter

import android.os.Bundle

interface IBasePresenter {
    fun init()

    fun onResume()

    fun onPause()

    fun onCreateOptionsMenu()

    fun restore(savedInstanceState: Bundle)

    fun saveState(savedInstanceState: Bundle)

    fun destroy()
}
