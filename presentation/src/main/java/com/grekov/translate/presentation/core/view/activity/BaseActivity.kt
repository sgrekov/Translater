package com.grekov.translate.presentation.core.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import butterknife.ButterKnife
import com.grekov.translate.presentation.TranslateApp
import com.grekov.translate.presentation.core.di.component.AppComponent
import timber.log.Timber

abstract class BaseActivity : AppCompatActivity() {


    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {

        setupComponent((application as TranslateApp).component())

        super.onCreate(savedInstanceState)
        if (layoutId != -1) {
            setContentView(layoutId)
        }
        ButterKnife.bind(this)
    }

    @get:LayoutRes
    protected abstract val layoutId: Int

    @CallSuper
    override fun onRestart() {
        super.onRestart()
        Timber.d("onRestart: ")
    }

    @CallSuper
    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.d("onSaveInstanceState: ")
    }

    @CallSuper
    override fun onStop() {
        super.onStop()
        Timber.d("onStop: ")
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy: ")
    }

    /**
     * setup dagger component with AppComponent and object specific module (called source onCreate)
     *
     * @param appComponent
     */
    protected abstract fun setupComponent(appComponent: AppComponent)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("onActivityResult: ")
        super.onActivityResult(requestCode, resultCode, data)
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        Timber.d("onPause: ")
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        Timber.d("onResume: ")
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        Timber.d("onStart: ")
    }


}
