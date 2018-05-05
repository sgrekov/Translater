package com.grekov.translate.presentation.translate.view.controller

import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.bluelinelabs.conductor.Controller
import com.grekov.translate.R
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.presentation.core.elm.InputBinding
import com.grekov.translate.presentation.core.framework.show
import com.grekov.translate.presentation.core.presenter.IBasePresenter
import com.grekov.translate.presentation.core.view.controller.BaseController
import com.grekov.translate.presentation.langs.view.controller.LangsController
import com.grekov.translate.presentation.main.di.component.MainViewComponent
import com.grekov.translate.presentation.translate.di.module.TranslateModule
import com.grekov.translate.presentation.translate.presenter.TranslatePresenter
import com.grekov.translate.presentation.translate.view.ITranslateView
import timber.log.Timber
import javax.inject.Inject

class TranslateController : BaseController(), ITranslateView, LangsController.TargetLangSelectListener {

    @Inject lateinit var translatePresenter: TranslatePresenter
    lateinit var langFromTv: TextView
    lateinit var langToTv: TextView
    lateinit var langArrowTv: TextView
    lateinit var sourceTextEt: EditText
    lateinit var dropTextTv: TextView
    lateinit var translatedTextTv: TextView
    lateinit var textTranscriptionTv: TextView
    lateinit var saveToFavTv: TextView
    lateinit var removeFromFavTv: TextView
    lateinit var loadingPB: ProgressBar
    lateinit var sourceTextInputBinding: InputBinding


    init {
        retainViewMode = Controller.RetainViewMode.RETAIN_DETACH
    }

    override fun getPresenter(): IBasePresenter {
        return translatePresenter
    }

    override val title: String
        get() = ""

    override fun setupComponent() {
        this.getComponent(MainViewComponent::class.java)
                .plusTranslateComponent(TranslateModule(this)).inject(this)
    }

    override val layoutId: Int
        get() = R.layout.translate_layout

    override fun onViewBound(view: View) {
        Timber.d("translate onViewBound")
        langFromTv = view.findViewById(R.id.lang_from_tv) as TextView
        langToTv = view.findViewById(R.id.lang_to_tv) as TextView
        langArrowTv = view.findViewById(R.id.lang_arrow_tv) as TextView
        sourceTextEt = view.findViewById(R.id.translate_text_et) as EditText
        dropTextTv = view.findViewById(R.id.drop_text_tv) as TextView
        translatedTextTv = view.findViewById(R.id.translated_text_tv) as TextView
        textTranscriptionTv = view.findViewById(R.id.original_text_transcript_tv) as TextView
        saveToFavTv = view.findViewById(R.id.save_to_fav_tv) as TextView
        removeFromFavTv = view.findViewById(R.id.remove_from_fav_tv) as TextView
        loadingPB = view.findViewById(R.id.loading) as ProgressBar

        langFromTv.setOnClickListener { view1 -> translatePresenter?.onLangClick(true) }
        langToTv.setOnClickListener { view1 -> translatePresenter?.onLangClick(false) }
        langArrowTv.setOnClickListener { view1 -> translatePresenter?.onRotateLangsClick() }
        dropTextTv.setOnClickListener { view1 -> translatePresenter?.dropTextClick() }
        saveToFavTv.setOnClickListener { view1 -> translatePresenter?.saveToFavClick() }
        removeFromFavTv.setOnClickListener { view1 -> translatePresenter?.removeFromFavClick() }
        sourceTextInputBinding = InputBinding(sourceTextEt)
        translatePresenter.addSourceTextChanges(sourceTextInputBinding)
    }

    override fun onLangPicked(lang: Lang, isFrom: Boolean) {
        translatePresenter.onLangSelect(lang, isFrom)
    }

    override fun onLangError() {

    }

    override fun showFromText(enable: Boolean) {
        langFromTv.isEnabled = enable
    }

    override fun showToText(enabled: Boolean) {
        langToTv.isEnabled = enabled
    }

    override fun setFromLang(langName: String) {
        langFromTv.text = langName
    }

    override fun setToLang(langName: String) {
        langToTv.text = langName
    }

    override fun setText(text: String) {
        sourceTextInputBinding.bindInput(text)
    }

    override fun showProgress(show: Boolean) {
        loadingPB.show(show)
    }

    override fun showFavoriteBtn(show: Boolean) {
        saveToFavTv.show(show)
    }

    override fun setSourceText(source: String) {
        textTranscriptionTv.text = source
    }

    override fun setTranslatedText(translate: String) {
        translatedTextTv.text = translate
    }

    override fun showSourceText(show: Boolean) {
        textTranscriptionTv.show(show)
    }

    override fun showTranslateText(show: Boolean) {
        translatedTextTv.show(show)
    }

    override fun setFavoriteBtn(favorite: Boolean) {
        if (favorite) {
            saveToFavTv.visibility = View.GONE
            removeFromFavTv.visibility = View.VISIBLE
        } else {
            saveToFavTv.visibility = View.VISIBLE
            removeFromFavTv.visibility = View.GONE
        }
    }

    fun setLang(selectedPhrase: Phrase) {
        translatePresenter.onPhraseSelect(selectedPhrase)
    }
}
