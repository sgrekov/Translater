package com.grekov.translate.presentation.main.view.activity

import android.os.Bundle
import android.view.ViewGroup
import butterknife.BindView
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.grekov.translate.R
import com.grekov.translate.presentation.core.di.component.AppComponent
import com.grekov.translate.presentation.core.di.contract.HasComponent
import com.grekov.translate.presentation.core.elm.Start
import com.grekov.translate.presentation.core.elm.TimeTraveller
import com.grekov.translate.presentation.core.view.activity.BaseActivity
import com.grekov.translate.presentation.main.di.component.DaggerMainViewComponent
import com.grekov.translate.presentation.main.di.component.MainViewComponent
import com.grekov.translate.presentation.main.view.controller.HomeController
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class MainActivity : BaseActivity(), HasComponent<MainViewComponent> {

    @BindView(R.id.controller_container) lateinit var container: ViewGroup

    override lateinit var component: MainViewComponent

    lateinit var router: Router

    @Inject lateinit var timeTraveller: TimeTraveller

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        router = Conductor.attachRouter(this, container, savedInstanceState)
        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(HomeController()))
        }

        timeTraveller.stateRelay
                .observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ (screen, state) ->
                    if (screen is Start){
                        router.setRoot(RouterTransaction.with(HomeController()))
                    }
                })
    }

    override val layoutId: Int
        get() = R.layout.main_layout

    override fun onBackPressed() {
        if (!router.handleBack()) {
            super.onBackPressed()
        }
    }

    override fun setupComponent(appComponent: AppComponent) {
        component = DaggerMainViewComponent.builder()
                .appComponent(appComponent)
                .activityModule(MainViewComponent.ActivityModule(this))
                .build()
        component.inject(this)
    }

}
