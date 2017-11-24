package com.grekov.translate.presentation.main.presenter

import android.annotation.SuppressLint
import android.os.Parcelable
import com.grekov.translate.domain.elm.*
import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.presentation.core.elm.Program
import com.grekov.translate.presentation.core.elm.Screen
import com.grekov.translate.presentation.core.elm.State
import com.grekov.translate.presentation.core.presenter.BasePresenter
import com.grekov.translate.presentation.history.presenter.Favorites
import com.grekov.translate.presentation.history.presenter.History
import com.grekov.translate.presentation.main.view.IHomeView
import com.grekov.translate.presentation.translate.presenter.Translate
import io.reactivex.Single
import kotlinx.android.parcel.Parcelize
import timber.log.Timber


@SuppressLint("ParcelCreator")
@Parcelize
data class Home(val stub : Unit = Unit) : Screen(), Parcelable


class HomePresenter(view: IHomeView, program: Program<HomeState>)
    : BasePresenter<IHomeView, HomePresenter.HomeState>(view, program) {


    @Parcelize
    data class HomeState(val tabNum: Int,
                         override val screen: Home = Home(),
                         val activeScreen: Screen,
                         val selectedPhrase: Phrase? = null) : State(screen), Parcelable


    data class TabSelectMsg(val tab: Screen) : Msg()
    data class PhraseSelectMsg(val phrase: Phrase) : Msg()
    object ResetPhraseMsg : HighPriorityMsg()

    override fun initialState(): HomeState {
        return HomeState(0, activeScreen = Translate())
    }

    override fun onInit() {
        Timber.d("onInit")
        addDisposable(program.init(initialState(), this))

        program.accept(Init)
    }


    override fun update(msg: Msg, state: HomeState): Pair<HomeState, Cmd> {
        return when (msg) {
            is TabSelectMsg -> when (msg.tab) {
                is Translate -> Pair(state.copy(tabNum = 0, activeScreen = Translate()), None)
                is History -> Pair(state.copy(tabNum = 1, activeScreen = History()), None)
                is Favorites -> Pair(state.copy(tabNum = 2, activeScreen = Favorites()), None)
                else -> Pair(state, None)
            }
            is PhraseSelectMsg -> Pair(
                    state.copy(tabNum = 0, activeScreen = Translate(), selectedPhrase = msg.phrase), OneShotCmd(ResetPhraseMsg))
            is ResetPhraseMsg -> Pair(state.copy(selectedPhrase = null), None)
            else -> Pair(state, None)
        }
    }

    override fun render(state: HomeState) {
        val view = viewReference.get() ?: return
        if (!view.isAttached()) return

        state.apply {
            view.setTab(tabNum)
            when (activeScreen) {
                is Translate -> view.showTranslate(selectedPhrase)
                is History -> view.showHistory()
                is Favorites -> view.showFavorites()
            }
        }
    }

    override fun call(cmd: Cmd): Single<Msg> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sub(state: HomeState) {}

    override fun travel(screen: Screen, state: State) {
        if (screen is Home && state is HomeState) {
            render(state)
        }
    }

    fun translateSelect() {
        program.accept(TabSelectMsg(Translate()))
    }

    fun historySelect() {
        program.accept(TabSelectMsg(History()))
    }

    fun favoritesSelect() {
        program.accept(TabSelectMsg(Favorites()))
    }

    fun onPhraseClick(model: Phrase) {
        program.accept(PhraseSelectMsg(model))
    }


}
