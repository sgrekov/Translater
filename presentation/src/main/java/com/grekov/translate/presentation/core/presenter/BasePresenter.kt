package com.grekov.translate.presentation.core.presenter

import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.CallSuper
import com.grekov.translate.presentation.core.elm.Component
import com.grekov.translate.presentation.core.elm.Program
import com.grekov.translate.presentation.core.elm.State
import com.grekov.translate.presentation.core.view.IBaseView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.lang.ref.WeakReference

abstract class BasePresenter<V : IBaseView, S : State>(view: V, val program : Program<S>) : IBasePresenter, Component<S> {

    protected val viewReference: WeakReference<V> = WeakReference(view)
    private val allSubscriptions = CompositeDisposable()


    override fun init() {
        program.restoredState?.let {
            addDisposable(program.init(it, this))
            program.restoredState = null
        } ?: run {
            onInit()
        }
    }

    abstract fun initialState() : S

    protected open fun onInit() {
        //template method
    }

    @CallSuper
    override fun onResume() {
        program.render()
    }


    @CallSuper
    override fun onPause() {
    }

    override fun destroy() {
        if (!allSubscriptions.isDisposed) {
            Timber.d("unsubscribe all")
            allSubscriptions.dispose()
        }
        onDestroy()
    }


    protected open fun onDestroy() {
        //template method
    }

    protected fun addDisposable(disposable: Disposable) {
        if (!allSubscriptions.isDisposed) {
            allSubscriptions.add(disposable)
        }
    }

    /**
     * Bundle key has to define uniquiness of presenter for persisting to bundle
     * If you have only one instance of presenter in app, you can leave it blank
     * But if you use more that one instances of the same presenter class, then you need to provide unique key
     * in order to persist correct state of presenter
     */
    open fun getBundleKey() : String {
        return ""
    }


    override fun onCreateOptionsMenu() {
        //implement when needed
    }

    override fun restore(savedInstanceState: Bundle) {
        val stateKey = "state_${this.javaClass.canonicalName}_${getBundleKey()}"
        Timber.d("restore state for key $stateKey")
        val stateParcel = savedInstanceState.getParcelable<Parcelable>(stateKey)
        stateParcel?.takeIf { it is State }.let {
            program.restoredState = stateParcel as S
        }
    }

    override fun saveState(savedInstanceState: Bundle) {
        val stateKey = "state_${this.javaClass.canonicalName}_${getBundleKey()}"
        Timber.d("save state for key $stateKey")
        savedInstanceState.putParcelable(stateKey, program.getState())
    }

}
