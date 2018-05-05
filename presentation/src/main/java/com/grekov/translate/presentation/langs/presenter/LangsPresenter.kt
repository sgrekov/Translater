package com.grekov.translate.presentation.langs.presenter

import android.annotation.SuppressLint
import android.os.Parcelable
import com.grekov.translate.domain.elm.Cmd
import com.grekov.translate.domain.elm.ErrorMsg
import com.grekov.translate.domain.elm.HighPriorityMsg
import com.grekov.translate.domain.elm.Idle
import com.grekov.translate.domain.elm.Init
import com.grekov.translate.domain.elm.Msg
import com.grekov.translate.domain.elm.None
import com.grekov.translate.domain.interactor.base.UseCaseCachePolicy
import com.grekov.translate.domain.interactor.lang.GetLangsUseCase
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.presentation.core.elm.Component
import com.grekov.translate.presentation.core.elm.Program
import com.grekov.translate.presentation.core.elm.Screen
import com.grekov.translate.presentation.core.elm.State
import com.grekov.translate.presentation.core.elm.inView
import com.grekov.translate.presentation.core.presenter.BasePresenter
import com.grekov.translate.presentation.langs.view.ILangsView
import io.reactivex.Single
import kotlinx.android.parcel.Parcelize
import timber.log.Timber

@SuppressLint("ParcelCreator")
@Parcelize
data class Langs(val from: Boolean) : Screen(), Parcelable

typealias ReturnFromLangs = Pair<Boolean, Lang?> //first - return from screen, Lang - selected lang

class LangsPresenter(
    view: ILangsView,
    program: Program<LangsState>,
    private val getLangsUseCase: GetLangsUseCase,
    private val from: Boolean
) : BasePresenter<ILangsView, LangsPresenter.LangsState>(view, program), Component<LangsPresenter.LangsState> {

    @Parcelize
    data class LangsState(
        val isFrom: Boolean,
        val isLoading: Boolean,
        val isSyncing: Boolean,
        override val screen: Screen,
        val langs: List<Lang>
    ) : State(screen), Parcelable

    data class LangsFromCacheMsg(val langs: List<Lang>? = null) : Msg()
    data class LangsFromCloudMsg(val langs: List<Lang>) : Msg()
    data class SelectLangMsg(val lang: Lang) : Msg()
    object HandleBackMsg : HighPriorityMsg()

    class LangsFromCacheCmd(val resultMsg: Msg? = null) : Cmd()
    class LangsFromCloudCmd : Cmd()
    class SelectLangCmd(val lang: Lang?) : Cmd()

    override fun initialState(): LangsState {
        return LangsState(
            isFrom = this.from,
            isLoading = true,
            isSyncing = true,
            langs = listOf(),
            screen = Langs(from)
        )
    }

    override fun onInit() {
        addDisposable(
            program.init(
                initialState(),
                this
            )
        )

        program.accept(Init)
    }

    /*
    * This is pure functions without any side effects, like reducers in Redux
     */
    override fun update(msg: Msg, state: LangsState): Pair<LangsState, Cmd> {
        return when (msg) {
            is Init -> state to LangsFromCacheCmd(resultMsg = LangsFromCacheMsg())
            is LangsFromCacheMsg ->
                state.copy(isLoading = false, langs = msg.langs ?: state.langs) to LangsFromCloudCmd()
            is LangsFromCloudMsg ->
                state.copy(isSyncing = false, langs = msg.langs) to None
            is SelectLangMsg ->
                state to SelectLangCmd(msg.lang)
            is HandleBackMsg ->
                state to SelectLangCmd(null)
            is ErrorMsg -> {
                Timber.e(msg.err)
                state to None
            }
            else -> state to None
        }
    }

    /*
    * render our view(call Activity, Fragment or Controller)
     */
    override fun render(state: LangsState) {
        view()?.let { view ->
            state.apply {
                if (isSyncing) {
                    view.showUpdateTitle()
                } else {
                    view.showTitle()
                }

                if (langs.isEmpty()) {
                    if (isLoading || isSyncing) {
                        view.showProgress()
                        view.showErrorText(false)
                    } else {
                        view.hideProgress()
                        view.setErrorTextToEmpty()
                        view.showErrorText(true)
                    }
                }

                if (langs.isNotEmpty()) {
                    view.hideProgress()
                    view.showErrorText(false)
                    view.showLangs(langs)
                }
            }
        }
    }

    /*
    * Here come side effects
     */
    override fun call(cmd: Cmd): Single<Msg> {
        return when (cmd) {
            is LangsFromCacheCmd ->
                getLangsUseCase
                    .getSingle(UseCaseCachePolicy.CacheOnly)
                    .map { langs ->
                        when (cmd.resultMsg) {
                            is LangsFromCacheMsg -> LangsFromCacheMsg(langs)
                            else -> LangsFromCacheMsg(langs)
                        }
                    }
            is LangsFromCloudCmd ->
                getLangsUseCase
                    .getSingle(UseCaseCachePolicy.CloudFirst)
                    .map { langs -> LangsFromCloudMsg(langs) }
            is SelectLangCmd ->
                inView {
                    cmd.lang?.let {
                        viewReference.get()?.selectLang(it, program.getState().isFrom)
                    } ?: viewReference.get()?.back()
                }
            else -> Single.just(Idle)
        }
    }

    override fun sub(state: LangsState) {}

    override fun travel(screen: Screen, state: State) {
        if (screen is Langs && state is LangsState) {
            render(state)
        }
    }

    fun onLangClick(model: Lang) {
        program.accept(SelectLangMsg(model))
    }

    fun handleBack() {
        program.accept(HandleBackMsg)
    }

}
