package com.grekov.translate.presentation

import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import com.grekov.translate.presentation.langs.view.controller.LangsController
import com.grekov.translate.presentation.main.view.activity.MainActivity


open class ConductorNavigator(activity: MainActivity) : Navigator {

    private val router by lazy(
        mode = LazyThreadSafetyMode.NONE,
        initializer = { activity.router }
    )

    override fun goToLangs(from: Boolean, callback : LangsController.TargetLangSelectListener) {
        router.pushController(
            RouterTransaction.with(LangsController(callback , from))
                .pushChangeHandler(VerticalChangeHandler())
                .popChangeHandler(VerticalChangeHandler()))
    }


}
