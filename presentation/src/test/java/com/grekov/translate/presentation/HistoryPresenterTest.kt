package com.grekov.translate.presentation

import android.view.View
import assertCmd
import cmdShould
import com.grekov.translate.R
import com.grekov.translate.domain.IResourceManager
import com.grekov.translate.domain.elm.Init
import com.grekov.translate.domain.elm.None
import com.grekov.translate.domain.interactor.history.HistoryClearUseCase
import com.grekov.translate.domain.interactor.history.HistoryLoadedMsg
import com.grekov.translate.domain.interactor.history.HistoryPhrasesSub
import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.presentation.core.elm.Program
import com.grekov.translate.presentation.core.elm.TimeTraveller
import com.grekov.translate.presentation.history.presenter.History
import com.grekov.translate.presentation.history.presenter.HistoryPresenter
import com.grekov.translate.presentation.history.view.IHistoryView
import io.reactivex.schedulers.Schedulers
import mock
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import stateAssert
import viewAssert
import java.util.*
import kotlin.test.assertEquals

class HistoryPresenterTest : Spek({

    var view: IHistoryView = mock()
    val resourceManager: IResourceManager = mock()
    val historyPhrasesSub: HistoryPhrasesSub = mock()
    val historyClearUseCase: HistoryClearUseCase = mock()
    var historyPresenter = HistoryPresenter(
            view,
            Program(Schedulers.trampoline(), TimeTraveller()),
            false,
            resourceManager,
            historyPhrasesSub,
            historyClearUseCase)
    Mockito.`when`(view.isAttached()).thenReturn(true)
    Mockito.`when`(resourceManager.getString(R.string.history_title)).thenReturn("History")
    Mockito.`when`(resourceManager.getString(R.string.history_search_hint)).thenReturn("search in history")

    describe("history screen") {

        beforeEachTest {
            Mockito.`when`(view.isAttached()).thenReturn(true)
        }

        afterEachTest {
            Mockito.reset(view)
        }

        given("history screen init", {

            val initialState = HistoryPresenter.HistoryState(false, listOf(), "", History())
            val (stateAfterInit, cmdAfterInit) = historyPresenter.update(Init, initialState)

            stateAssert("", {
                assertEquals(initialState, stateAfterInit)
            })

            viewAssert("", {
                historyPresenter.render(stateAfterInit)

                verify(view).isAttached()
                verify(view).setTitle("History")
                verify(view).setFilterHint("search in history")
                verify(view).triggerEmptyText(View.VISIBLE)
                verify(view).setSearchText("")
                verify(view).setPhrases(listOf())
                verifyNoMoreInteractions(view)
            })

            cmdShould("after init", {
                assertCmd(cmdAfterInit, None::class)
            })

            given("phrases history loaded ", {
                val phrases = listOf(
                        Phrase("cat", "кошка", "en-ru", false, Date()),
                        Phrase("dog", "собака", "en-ru", false, Date()),
                        Phrase("door", "дверь", "en-ru", false, Date()),
                        Phrase("light", "свет", "en-ru", false, Date())
                )

                val (stateAfterLoaded, cmdAfterLoaded) =
                        historyPresenter.update(
                                HistoryLoadedMsg(phrases),
                                stateAfterInit)

                stateAssert("state after phrases loaded", {
                    assertEquals(stateAfterInit.copy(phrases = phrases), stateAfterLoaded)
                })

                viewAssert("view after phrases loaded", {
                    historyPresenter.render(stateAfterLoaded)

                    verify(view).isAttached()
                    verify(view).setTitle("History")
                    verify(view).setFilterHint("search in history")
                    verify(view).triggerEmptyText(View.GONE)
                    verify(view).setSearchText("")
                    verify(view).setPhrases(phrases)
                    verifyNoMoreInteractions(view)
                })


                cmdShould("cmd after phrases loaded", {
                    assertCmd(cmdAfterLoaded, None::class)
                })

            })

        })
    }
})