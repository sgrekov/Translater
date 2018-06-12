package com.grekov.translate.presentation

import com.grekov.translate.presentation.langs.view.controller.LangsController

interface Navigator {
    fun goToLangs(from: Boolean, callback: LangsController.TargetLangSelectListener)
}