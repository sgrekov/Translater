package com.grekov.translate.presentation.history.presenter

import android.annotation.SuppressLint
import android.os.Parcelable
import android.view.View
import com.grekov.translate.R
import com.grekov.translate.domain.IResourceManager
import com.grekov.translate.domain.elm.*
import com.grekov.translate.domain.interactor.history.HistoryClearUseCase
import com.grekov.translate.domain.interactor.history.HistoryLoadedMsg
import com.grekov.translate.domain.interactor.history.HistoryPhrasesParams
import com.grekov.translate.domain.interactor.history.HistoryPhrasesSub
import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.presentation.core.elm.*
import com.grekov.translate.presentation.core.presenter.BasePresenter
import com.grekov.translate.presentation.history.view.IHistoryView
import io.reactivex.Single
import io.reactivex.functions.Consumer
import kotlinx.android.parcel.Parcelize
import timber.log.Timber

@SuppressLint("ParcelCreator")
@Parcelize
data class History(val stub : Unit = Unit) : Screen(), Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Favorites(val stub : Unit = Unit) : Screen(), Parcelable

class HistoryPresenter(view: IHistoryView,
                       program: Program<HistoryState>,
                       val isFavorite: Boolean,
                       val resourceManager: IResourceManager,
                       val historyPhrasesSub: HistoryPhrasesSub,
                       val historyClearUseCase: HistoryClearUseCase)
    : BasePresenter<IHistoryView, HistoryPresenter.HistoryState>(view, program), Component<HistoryPresenter.HistoryState> {

    @Parcelize
    data class HistoryState(
            val isFavorite: Boolean,
            val phrases: List<Phrase> = listOf(),
            val searchText: String,
            override val screen: Screen) : State(screen), Parcelable

    data class FilterPhrasesMsg(val query: String) : Msg()
    class ClearHistoryMsg : Msg()

    class ClearHistoryCmd(val isFavorite: Boolean) : Cmd()

    init {
        Timber.d("HistoryPresenter isFavorite:$isFavorite")
    }

    override fun initialState(): HistoryState {
        return HistoryState(isFavorite = isFavorite, searchText = "", screen = if (isFavorite) Favorites() else History())
    }

    override fun onInit() {
        addDisposable(
                program.init(
                        initialState(),
                        this))
        program.accept(Init)
    }

    override fun update(msg: Msg, state: HistoryState): Pair<HistoryState, Cmd> {
        return when (msg) {
            is Init -> {
                Pair(state, None)
            }
            is FilterPhrasesMsg -> {
                Pair(state.copy(searchText = msg.query), None)
            }
            is ClearHistoryMsg -> {
                Pair(state, ClearHistoryCmd(state.isFavorite))
            }
            is HistoryLoadedMsg -> {
                Pair(state.copy(phrases = msg.phrases), None)
            }
            is ErrorMsg -> {
                Timber.e(msg.err)
                Pair(state, None)
            }
            else -> Pair(state, None)
        }
    }

    override fun render(state: HistoryState) {
        val view = viewReference.get() ?: return
        if (!view.isAttached()) return

        state.apply {
            view.setTitle(
                    if (isFavorite) resourceManager.getString(R.string.favorites_title)
                    else resourceManager.getString(R.string.history_title))
            view.setFilterHint(
                    if (isFavorite) resourceManager.getString(R.string.favorites_search_hint)
                    else resourceManager.getString(R.string.history_search_hint))
            if (phrases.isNotEmpty()) {
                view.triggerEmptyText(View.GONE)
            } else {
                view.triggerEmptyText(View.VISIBLE)
            }
            view.setPhrases(phrases)
            view.setSearchText(searchText)
        }
    }

    override fun call(cmd: Cmd): Single<Msg> {
        return when (cmd) {
            is ClearHistoryCmd -> {
                historyClearUseCase
                        .getCompletable(if (cmd.isFavorite) cmd.isFavorite else null)
                        .andThen(Single.just(Init as Msg))
            }
            else -> Single.just(Idle)
        }
    }

    override fun sub(state: HistoryState) {
        program.addSub(
                historyPhrasesSub,
                HistoryPhrasesParams(state.searchText, state.isFavorite))
    }

    override fun travel(screen: Screen, state: State) {
        if (((screen is History && !isFavorite) || (screen is Favorites && isFavorite))
                && state is HistoryState) {
            render(state)
        }
    }

    fun addFilterChanges(inputBinding: InputBinding) {
        inputBinding
                .subscribe(Consumer { text -> program.accept(FilterPhrasesMsg(text.toString())) })
    }

    fun clearClick() {
        program.accept(ClearHistoryMsg())
    }

    override fun getBundleKey(): String {
        return isFavorite.toString()
    }


}

