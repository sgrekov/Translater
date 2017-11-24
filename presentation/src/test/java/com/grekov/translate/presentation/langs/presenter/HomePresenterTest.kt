package com.grekov.translate.presentation.langs.presenter

import assertCmd
import cmdShould
import com.grekov.translate.domain.elm.Init
import com.grekov.translate.domain.elm.None
import com.grekov.translate.domain.elm.OneShotCmd
import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.presentation.core.elm.Program
import com.grekov.translate.presentation.core.elm.TimeTraveller
import com.grekov.translate.presentation.history.presenter.Favorites
import com.grekov.translate.presentation.history.presenter.History
import com.grekov.translate.presentation.main.presenter.HomePresenter
import com.grekov.translate.presentation.main.view.IHomeView
import com.grekov.translate.presentation.translate.presenter.Translate
import io.reactivex.schedulers.Schedulers
import mock
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.Mockito
import stateAssert
import viewAssert
import java.util.*
import kotlin.test.assertEquals

@RunWith(JUnitPlatform::class)
class HomePresenterTest : Spek({

    var view: IHomeView = mock()
    var homePresenter = HomePresenter(view, Program(Schedulers.trampoline(), TimeTraveller()))

    describe("main screen") {

        given("app started", {

            beforeEachTest {
                Mockito.`when`(view.isAttached()).thenReturn(true)
            }

            afterEachTest {
                Mockito.reset(view)
            }

            val (stateAfterInit, cmdAfterInit) = homePresenter.update(Init, homePresenter.initialState())

            stateAssert("", {
                assertEquals(stateAfterInit, homePresenter.initialState())
            })

            viewAssert("", {
                homePresenter.render(stateAfterInit)

                Mockito.verify(view).isAttached()
                Mockito.verify(view).setTab(0)
                Mockito.verify(view).showTranslate(null)
                Mockito.verifyNoMoreInteractions(view)
            })

            cmdShould("cmd after init", {
                assertCmd(cmdAfterInit, None::class)
            })

            given("click on history tab", {
                val (stateAfterHistoryClick, cmdAfterHistoryClick) = homePresenter.update(HomePresenter.TabSelectMsg(History()), stateAfterInit)

                stateAssert("state after click on history tab", {
                    assertEquals(stateAfterInit.copy(tabNum = 1, activeScreen = History()), stateAfterHistoryClick)
                })

                viewAssert("view after click on history tab", {
                    homePresenter.render(stateAfterHistoryClick)

                    Mockito.verify(view).isAttached()
                    Mockito.verify(view).setTab(1)
                    Mockito.verify(view).showHistory()
                    Mockito.verifyNoMoreInteractions(view)
                })

                cmdShould("cmd after click on history tab", {
                    assertCmd(cmdAfterHistoryClick, None::class)
                })

                given("click on phrase from history", {
                    val phrase = Phrase("cat", "кошка", "en-ru", false, Date())
                    val (stateAfterPhraseClick, cmdAfterPhraseClick) = homePresenter
                            .update(
                                    HomePresenter.PhraseSelectMsg(Phrase("cat", "кошка", "en-ru", false, Date())),
                                    stateAfterHistoryClick)
                    stateAssert("state after click on phrase from history", {
                        assertEquals(stateAfterHistoryClick
                                .copy(tabNum = 0,
                                        activeScreen = Translate(),
                                        selectedPhrase = phrase),
                                stateAfterPhraseClick)
                    })

                    viewAssert("view after click on phrase from history", {
                        homePresenter.render(stateAfterPhraseClick)

                        Mockito.verify(view).isAttached()
                        Mockito.verify(view).setTab(0)
                        Mockito.verify(view).showTranslate(phrase)
                        Mockito.verifyNoMoreInteractions(view)
                    })

                    cmdShould("cmd after click on phrase from history", {
                        assertCmd(cmdAfterPhraseClick, OneShotCmd::class)
                    })
                })
            })

            given("click on favorites tab", {
                val (stateAfterFavoritesClick, cmdAfterFavoritesClick) = homePresenter.update(HomePresenter.TabSelectMsg(Favorites()), stateAfterInit)

                stateAssert("state after click on favorites tab", {
                    assertEquals(stateAfterInit.copy(tabNum = 2, activeScreen = Favorites()), stateAfterFavoritesClick)
                })

                viewAssert("view after click on favorites tab", {
                    homePresenter.render(stateAfterFavoritesClick)

                    Mockito.verify(view).isAttached()
                    Mockito.verify(view).setTab(2)
                    Mockito.verify(view).showFavorites()
                    Mockito.verifyNoMoreInteractions(view)
                })

                cmdShould("cmd after click on favorites tab", {
                    assertCmd(cmdAfterFavoritesClick, None::class)
                })
            })

        })
    }
})