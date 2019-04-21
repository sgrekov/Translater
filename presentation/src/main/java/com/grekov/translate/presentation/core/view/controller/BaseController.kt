package com.grekov.translate.presentation.core.view.controller


import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.Controller
import com.grekov.translate.presentation.core.di.contract.HasComponent
import com.grekov.translate.presentation.core.view.IBaseView
import timber.log.Timber

abstract class BaseController : Controller, IBaseView {

    private var unbinder: Unbinder? = null
    private var restoredBundle: Bundle? = null
    private var created: Boolean = false

    protected constructor() : super() {}

    protected constructor(args: Bundle?) : super(args) {}

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(layoutId, container, false)
        unbinder = ButterKnife.bind(this, view)
        if (!created) {
            setupComponent()
        }

        onViewBound(view)

        if (!created) {
            getPresenter().init()
            created = true
        }

        restoredBundle?.let { getPresenter().restore(it) }
        restoredBundle = null
        return view
    }

    @CallSuper
    override fun onAttach(view: View) {
        getPresenter().onResume()
    }


    @CallSuper
    override fun onDetach(view: View) {
        getPresenter().onPause()
    }


    abstract val title: String

    protected abstract fun setupComponent()

    protected fun <C> getComponent(componentType: Class<C>): C {
        return componentType.cast((activity as HasComponent<C>).component)
    }


    fun getString(@StringRes stringResId: Int): String {
        if (activity == null) {
            Timber.d("getActivity() is null!")
            return ""
        } else {
            return activity!!.getString(stringResId)
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        getPresenter().saveState(outState)
        super.onSaveInstanceState(outState)
    }

    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        restoredBundle = savedInstanceState
        super.onRestoreInstanceState(savedInstanceState)
    }

    @get:LayoutRes
    protected abstract val layoutId: Int

    protected abstract fun onViewBound(view: View)

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        unbinder?.unbind()
    }

    override fun onDestroy() {
        getPresenter().destroy()
    }

    override fun handleBack(): Boolean {
        return super.handleBack()
    }

}
