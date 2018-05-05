package com.grekov.translate.presentation.translate.presenter

import android.annotation.SuppressLint
import android.os.Parcelable
import com.grekov.translate.domain.IAppPreferencesManager
import com.grekov.translate.domain.elm.BatchCmd
import com.grekov.translate.domain.elm.Cmd
import com.grekov.translate.domain.elm.ErrorMsg
import com.grekov.translate.domain.elm.Idle
import com.grekov.translate.domain.elm.Init
import com.grekov.translate.domain.elm.Msg
import com.grekov.translate.domain.elm.None
import com.grekov.translate.domain.interactor.history.CheckFavoriteUseCase
import com.grekov.translate.domain.interactor.history.MakeFavoriteParams
import com.grekov.translate.domain.interactor.history.MakeFavoriteUseCase
import com.grekov.translate.domain.interactor.lang.LoadLangsByCodeUseCaseSingle
import com.grekov.translate.domain.interactor.translate.MakeTranslateUseCase
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.domain.utils.getLangLiteral
import com.grekov.translate.presentation.Navigator
import com.grekov.translate.presentation.core.elm.Component
import com.grekov.translate.presentation.core.elm.InputBinding
import com.grekov.translate.presentation.core.elm.Program
import com.grekov.translate.presentation.core.elm.Screen
import com.grekov.translate.presentation.core.elm.State
import com.grekov.translate.presentation.core.elm.inView
import com.grekov.translate.presentation.core.presenter.BasePresenter
import com.grekov.translate.presentation.langs.view.controller.LangsController
import com.grekov.translate.presentation.translate.view.ITranslateView
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.functions.Consumer
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@SuppressLint("ParcelCreator")
@Parcelize
data class Translate(val stub: Unit = Unit) : Screen(), Parcelable


class TranslatePresenter(
    view: ITranslateView,
    program: Program<TranslateState>,
    val appPreferencesManager: IAppPreferencesManager,
    val navigator: Navigator,
    val translateUseCase: MakeTranslateUseCase,
    val makeFavoriteUseCase: MakeFavoriteUseCase,
    val checkFavoriteUseCase: CheckFavoriteUseCase,
    val loadLangsByCodeUseCaseSingle: LoadLangsByCodeUseCaseSingle
) : BasePresenter<ITranslateView, TranslatePresenter.TranslateState>(view, program),
    Component<TranslatePresenter.TranslateState> {

    @SuppressLint("ParcelCreator")
    @Parcelize
    data class TranslateState(
        val langFrom: Lang,
        val langTo: Lang,
        val currentText: String = "",
        val phrase: Phrase? = null,
        val isLoading: Boolean = true,
        val isFavorite: Boolean = false,
        val drop: Boolean = false,
        override val screen: Screen = Translate()
    ) : State(screen), Parcelable

    data class NavigateToLangsMsg(val from: Boolean) : Msg()
    data class ChangeLangsMsg(val lang: Lang, val from: Boolean) : Msg()
    data class LangsByCodeMsg(val from: Lang, val to: Lang) : Msg()
    object RotateLangsMsg : Msg()
    data class TranslateResultMsg(val text: Phrase) : Msg()
    data class LangsFromPrefsMsg(val from: Lang, val to: Lang) : Msg()
    object DropTextMsg : Msg()
    object SaveToFavoritesMsg : Msg()
    object RemoveFromFavoritesMsg : Msg()
    class FavoriteCheckResultMsg(val phrase: String, val from: Lang, val to: Lang, val favorite: Boolean) : Msg()
    data class TextChangeMsg(val text: String) : Msg()
    data class PhraseSelectMsg(val phrase: Phrase) : Msg()
    object TranslateMsg : Msg()

    object RetrieveLangsFromPrefsCmd : Cmd()
    data class SaveCurrentLangsCmd(val from: Lang, val to: Lang) : Cmd()
    data class TranslateCmd(val text: String, val from: Lang, val to: Lang) : Cmd()
    data class MakeFavoriteCmd(val text: String, val from: Lang, val to: Lang) : Cmd()
    data class MakeNotFavoriteCmd(val text: String, val from: Lang, val to: Lang) : Cmd()
    data class CheckFavoriteCmd(val text: String, val from: Lang, val to: Lang) : Cmd()
    data class GetLangsByCodeCmd(val langsLiteral: String) : Cmd()
    data class GoToLangsCmd(val from: Boolean, val langsSelectCallback: LangsController.TargetLangSelectListener) :
        Cmd()

    override fun initialState(): TranslateState {
        val (langFrom, langTo) = if (Locale.getDefault().language == "ru") {
            Pair(Lang("ru", "Русский"), Lang("en", "Английский"))
        } else {
            Pair(Lang("en", "English"), Lang("ru", "Russian"))
        }
        return TranslateState(langFrom = langFrom, langTo = langTo)
    }

    override fun onInit() {

        addDisposable(program.init(initialState(), this))

        program.accept(Init)
    }

    override fun update(msg: Msg, state: TranslateState): Pair<TranslateState, Cmd> {
        return when (msg) {
            is Init -> state.copy(isLoading = true) to RetrieveLangsFromPrefsCmd
            is ChangeLangsMsg -> {
                val newState = if (msg.from) {
                    if (state.langFrom != msg.lang) {
                        state.copy(langFrom = msg.lang, isLoading = true)
                    } else {
                        state
                    }
                } else {
                    if (state.langTo != msg.lang) {
                        state.copy(langTo = msg.lang, isLoading = true)
                    } else {
                        state
                    }
                }
                Pair(
                    newState, if (state === newState) {
                        None
                    } else {
                        BatchCmd(
                            listOf(
                                SaveCurrentLangsCmd(newState.langFrom, newState.langTo),
                                CheckFavoriteCmd(newState.currentText, newState.langFrom, newState.langTo),
                                TranslateCmd(newState.currentText, newState.langFrom, newState.langTo)
                            )
                        )
                    }
                )
            }
            is LangsFromPrefsMsg -> {
                state.copy(isLoading = false, langFrom = msg.from, langTo = msg.to) to None
            }
            is NavigateToLangsMsg -> {
                state to GoToLangsCmd(msg.from, viewReference.get() as LangsController.TargetLangSelectListener)
            }
            is RotateLangsMsg -> {
                val newState = state.copy(langFrom = state.langTo, langTo = state.langFrom)
                newState to
                        BatchCmd(
                            listOf(
                                SaveCurrentLangsCmd(newState.langFrom, newState.langTo),
                                CheckFavoriteCmd(newState.currentText, newState.langFrom, newState.langTo),
                                TranslateCmd(newState.currentText, newState.langFrom, newState.langTo)
                            )
                        )
            }
            is TextChangeMsg -> state.copy(currentText = msg.text) to CheckFavoriteCmd(
                msg.text,
                state.langFrom,
                state.langTo
            )
            is TranslateMsg -> {
                if (state.currentText.isNotBlank()) {
                    state.copy(isLoading = true) to TranslateCmd(state.currentText, state.langFrom, state.langTo)
                } else {
                    state to None
                }
            }
            is TranslateResultMsg -> {
                state.copy(isLoading = false, phrase = msg.text) to None
            }
            is DropTextMsg -> state.copy(currentText = "", phrase = null, isFavorite = false) to None
            is SaveToFavoritesMsg -> state to MakeFavoriteCmd(state.currentText, state.langFrom, state.langTo)
            is RemoveFromFavoritesMsg -> state to MakeNotFavoriteCmd(state.currentText, state.langFrom, state.langTo)
            is FavoriteCheckResultMsg -> {
                if (state.currentText == msg.phrase && state.langFrom == msg.from && state.langTo == msg.to) {
                    state.copy(isFavorite = msg.favorite) to None
                } else {
                    state to None
                }
            }
            is PhraseSelectMsg ->
                state.copy(
                    currentText = msg.phrase.source,
                    phrase = msg.phrase,
                    isFavorite = msg.phrase.favorite
                ) to GetLangsByCodeCmd(msg.phrase.langsLiteral)
            is LangsByCodeMsg -> state.copy(langFrom = msg.from, langTo = msg.to) to None
            is ErrorMsg -> {
                Timber.e(msg.err, "cmd${msg.cmd}")
                state.copy(isLoading = false) to None
            }
            else -> state to None
        }
    }

    override fun render(state: TranslateState) {
        view()?.let { view ->
            state.apply {
                view.setFromLang(langFrom.name)
                view.setToLang(langTo.name)
                view.showFromText(!isLoading)
                view.showToText(!isLoading)
                view.showProgress(isLoading)
                view.showSourceText(!isLoading)
                view.showTranslateText(!isLoading)
                view.showFavoriteBtn(!isLoading)

                if (!isLoading) {
                    phrase?.let {
                        view.setSourceText(it.source)
                        view.setTranslatedText(it.translate ?: "")
                    }
                    view.setFavoriteBtn(isFavorite)
                }

                view.setText(currentText)
                if (phrase == null) {
                    view.setSourceText("")
                    view.setTranslatedText("")
                }

            }
        }
    }

    override fun call(cmd: Cmd): Single<Msg> {
        return when (cmd) {
            is RetrieveLangsFromPrefsCmd ->
                appPreferencesManager.getLangs().map { (from, to) -> LangsFromPrefsMsg(from, to) }
            is SaveCurrentLangsCmd ->
                appPreferencesManager.saveLangs(cmd.from, cmd.to).toSingle { Idle }
            is TranslateCmd ->
                translateUseCase.getSingle(Phrase(cmd.text, null, getLangLiteral(cmd.from, cmd.to), created = Date()))
                    .map { phrase -> TranslateResultMsg(phrase) }
            is MakeFavoriteCmd ->
                makeFavoriteUseCase.getCompletable(MakeFavoriteParams(cmd.text, cmd.from, cmd.to, true))
                    .toSingle { FavoriteCheckResultMsg(cmd.text, cmd.from, cmd.to, true) }
            is MakeNotFavoriteCmd ->
                makeFavoriteUseCase.getCompletable(MakeFavoriteParams(cmd.text, cmd.from, cmd.to, false))
                    .toSingle { FavoriteCheckResultMsg(cmd.text, cmd.from, cmd.to, false) }
            is CheckFavoriteCmd -> {
                checkFavoriteUseCase.getSingle(MakeFavoriteParams(cmd.text, cmd.from, cmd.to, false))
                    .map { result -> FavoriteCheckResultMsg(cmd.text, cmd.from, cmd.to, result) }
            }
            is GetLangsByCodeCmd -> {
                loadLangsByCodeUseCaseSingle.getSingle(cmd.langsLiteral)
                    .map { (first, second) -> LangsByCodeMsg(from = first, to = second) }
            }
            is GoToLangsCmd -> {
                inView {
                    navigator.goToLangs(cmd.from, cmd.langsSelectCallback)
                }
            }
            else -> Single.just(Idle)
        }
    }

    override fun sub(state: TranslateState) {}

    override fun travel(screen: Screen, state: State) {
        if (screen is Translate && state is TranslateState) {
            render(state)
        }
    }

    fun onLangClick(from: Boolean) {
        program.accept(NavigateToLangsMsg(from))
    }

    fun onRotateLangsClick() {
        program.accept(RotateLangsMsg)
    }

    fun onLangSelect(lang: Lang, isFrom: Boolean) {
        program.accept(ChangeLangsMsg(lang, isFrom))
    }

    fun dropTextClick() {
        program.accept(DropTextMsg)
    }

    fun saveToFavClick() {
        program.accept(SaveToFavoritesMsg)
    }

    fun removeFromFavClick() {
        program.accept(RemoveFromFavoritesMsg)
    }

    private fun getState(): TranslateState {
        return program.getState()
    }

    fun onPhraseSelect(selectedPhrase: Phrase) {
        program.accept(PhraseSelectMsg(selectedPhrase))
    }

    fun addSourceTextChanges(sourceInputBinding: InputBinding) {
        sourceInputBinding.addTransformer(ObservableTransformer { textObservable: Observable<CharSequence> ->
            textObservable
                .debounce(500, TimeUnit.MILLISECONDS)
                .doOnNext({ text ->
                    program.accept(TextChangeMsg(text.toString()))
                })
        })

        sourceInputBinding
            .subscribe(Consumer { _ ->
                program.accept(TranslateMsg)
            })

    }
}
