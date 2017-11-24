package com.grekov.translate.presentation.langs.presenter

import android.view.View
import assertBatch
import assertCmd
import cmdShould
import com.grekov.translate.domain.IAppPreferencesManager
import com.grekov.translate.domain.elm.Init
import com.grekov.translate.domain.elm.None
import com.grekov.translate.domain.interactor.history.CheckFavoriteUseCase
import com.grekov.translate.domain.interactor.history.MakeFavoriteUseCase
import com.grekov.translate.domain.interactor.lang.LoadLangsByCodeUseCaseSingle
import com.grekov.translate.domain.interactor.translate.MakeTranslateUseCase
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.presentation.core.elm.Program
import com.grekov.translate.presentation.core.elm.TimeTraveller
import com.grekov.translate.presentation.translate.presenter.Translate
import com.grekov.translate.presentation.translate.presenter.TranslatePresenter
import com.grekov.translate.presentation.translate.view.ITranslateView
import io.reactivex.schedulers.Schedulers
import mock
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.Mockito
import stateAssert
import viewAssert
import java.util.*
import kotlin.test.assertEquals

@RunWith(JUnitPlatform::class)
class TranslatePresenterTest : Spek({

    var view: ITranslateView = mock()
    val appPrefs: IAppPreferencesManager = mock()
    val makeTranslateUseCase: MakeTranslateUseCase = mock()
    val makeFavUseCase: MakeFavoriteUseCase = mock()
    val checkFavUseCase: CheckFavoriteUseCase = mock()
    val loadLangsUseCaseSingle: LoadLangsByCodeUseCaseSingle = mock()
    var translatePresenter = TranslatePresenter(
            view,
            Program(Schedulers.trampoline(), TimeTraveller()),
            appPrefs,
            makeTranslateUseCase,
            makeFavUseCase,
            checkFavUseCase,
            loadLangsUseCaseSingle
    )
    Mockito.`when`(view.isAttached()).thenReturn(true)

    describe("translate screen") {

        beforeEachTest {
            Mockito.`when`(view.isAttached()).thenReturn(true)
        }

        afterEachTest {
            Mockito.reset(view)
        }

        given("translate screen init ", {
            val initialState = TranslatePresenter.TranslateState(
                    langFrom = Lang("ru", "russian"),
                    langTo = Lang("en", "english"),
                    navigateTo = Translate())

            val (stateAfterInit, cmdAfterInit) =
                    translatePresenter.update(
                            Init,
                            initialState)

            stateAssert("", {
                assertEquals(initialState.copy(isLoading = true), stateAfterInit)
            })

            viewAssert("view after phrases loaded", {
                translatePresenter.render(stateAfterInit)

                Mockito.verify(view).isAttached()
                Mockito.verify(view).triggerFromText(false)
                Mockito.verify(view).triggerToText(false)
                Mockito.verify(view).triggerProgress(View.VISIBLE)
                Mockito.verify(view).triggerSource(View.GONE)
                Mockito.verify(view).setSource("")
                Mockito.verify(view).triggerTranslate(View.GONE)
                Mockito.verify(view).setTranslate("")
                Mockito.verify(view).setText("")
                Mockito.verify(view).setFromLang("russian")
                Mockito.verify(view).setToLang("english")
                Mockito.verify(view).triggerFavoriteBtn(View.GONE)
                Mockito.verifyNoMoreInteractions(view)
                Mockito.reset(view)
            })

            cmdShould("cmd after translate init", {
                assertCmd(cmdAfterInit, TranslatePresenter.RetrieveLangsFromPrefsCmd::class)
            })

            given("langs loaded from prefs", {
                val (stateAfterPrefsLangs, cmdAfterPrefsLangs) =
                        translatePresenter.update(
                                TranslatePresenter.LangsFromPrefsMsg(Lang("en", "english"), Lang("ru", "russian")),
                                stateAfterInit)

                stateAssert("state after langs loaded from prefs", {
                    assertEquals(stateAfterInit.
                            copy(isLoading = false,
                                    langFrom = Lang("en", "english"),
                                    langTo = Lang("ru", "russian")),
                            stateAfterPrefsLangs)
                })

                viewAssert("view after langs loaded from prefs", {
                    translatePresenter.render(stateAfterPrefsLangs)

                    Mockito.verify(view).isAttached()
                    Mockito.verify(view).triggerFromText(true)
                    Mockito.verify(view).triggerToText(true)
                    Mockito.verify(view).triggerProgress(View.GONE)
                    Mockito.verify(view).triggerSource(View.VISIBLE)
                    Mockito.verify(view).setSource("")
                    Mockito.verify(view).triggerTranslate(View.VISIBLE)
                    Mockito.verify(view).setTranslate("")
                    Mockito.verify(view).setText("")
                    Mockito.verify(view).setFromLang("english")
                    Mockito.verify(view).setToLang("russian")
                    Mockito.verify(view).triggerFavoriteBtn(View.VISIBLE)
                    Mockito.verify(view).setFavoriteBtn(false)
                    Mockito.verifyNoMoreInteractions(view)
                })

                cmdShould("cmd after langs loaded from prefs", {
                    assertCmd(cmdAfterPrefsLangs, None::class)
                })

                given("input text for translate", {
                    val (stateAfterTextChange, cmdAfterTextChange) =
                            translatePresenter.update(
                                    TranslatePresenter.TextChangeMsg("cat"),
                                    stateAfterPrefsLangs)

                    stateAssert("state after text input", {
                        assertEquals(stateAfterPrefsLangs.
                                copy(currentText = "cat"),
                                stateAfterTextChange)
                    })

                    viewAssert("view after text change", {
                        translatePresenter.render(stateAfterTextChange)

                        Mockito.verify(view).isAttached()
                        Mockito.verify(view).triggerFromText(true)
                        Mockito.verify(view).triggerToText(true)
                        Mockito.verify(view).triggerProgress(View.GONE)
                        Mockito.verify(view).triggerSource(View.VISIBLE)
                        Mockito.verify(view).setSource("")
                        Mockito.verify(view).triggerTranslate(View.VISIBLE)
                        Mockito.verify(view).setTranslate("")
                        Mockito.verify(view).setText("cat")
                        Mockito.verify(view).setFromLang("english")
                        Mockito.verify(view).setToLang("russian")
                        Mockito.verify(view).triggerFavoriteBtn(View.VISIBLE)
                        Mockito.verify(view).setFavoriteBtn(false)
                        Mockito.verifyNoMoreInteractions(view)
                    })

                    cmdShould("cmd after text input", {
                        assertCmd(cmdAfterTextChange, TranslatePresenter.CheckFavoriteCmd::class)
                    })

                    given("check favorite result", {
                        val (stateAfterFavCheck, cmdAfterFavCheck) =
                                translatePresenter.update(
                                        TranslatePresenter.FavoriteCheckResultMsg(
                                                "cat",
                                                Lang("en", "english"),
                                                Lang("ru", "russian"),
                                                true),
                                        stateAfterTextChange)

                        stateAssert("state after text input", {
                            assertEquals(stateAfterTextChange.
                                    copy(isFavorite = true),
                                    stateAfterFavCheck)
                        })

                        viewAssert("view after text change", {
                            translatePresenter.render(stateAfterFavCheck)

                            Mockito.verify(view).isAttached()
                            Mockito.verify(view).triggerFromText(true)
                            Mockito.verify(view).triggerToText(true)
                            Mockito.verify(view).triggerProgress(View.GONE)
                            Mockito.verify(view).triggerSource(View.VISIBLE)
                            Mockito.verify(view).setSource("")
                            Mockito.verify(view).triggerTranslate(View.VISIBLE)
                            Mockito.verify(view).setTranslate("")
                            Mockito.verify(view).setText("cat")
                            Mockito.verify(view).setFromLang("english")
                            Mockito.verify(view).setToLang("russian")
                            Mockito.verify(view).triggerFavoriteBtn(View.VISIBLE)
                            Mockito.verify(view).setFavoriteBtn(true)
                            Mockito.verifyNoMoreInteractions(view)
                        })

                        cmdShould("cmd after fav check", {
                            assertCmd(cmdAfterFavCheck, None::class)
                        })
                    })

                    given("translate ", {
                        val (stateAfterStartTranslate, cmdAfterStartTranslate) =
                                translatePresenter.update(
                                        TranslatePresenter.TranslateMsg,
                                        stateAfterTextChange)

                        stateAssert("state after start translate", {
                            assertEquals(stateAfterTextChange.
                                    copy(isLoading = true),
                                    stateAfterStartTranslate)
                        })

                        viewAssert("view after start translate", {
                            translatePresenter.render(stateAfterStartTranslate)

                            Mockito.verify(view).isAttached()
                            Mockito.verify(view).triggerFromText(false)
                            Mockito.verify(view).triggerToText(false)
                            Mockito.verify(view).triggerProgress(View.VISIBLE)
                            Mockito.verify(view).triggerSource(View.GONE)
                            Mockito.verify(view).triggerTranslate(View.GONE)
                            Mockito.verify(view).setFromLang("english")
                            Mockito.verify(view).setToLang("russian")
                            Mockito.verify(view).setText("cat")
                            Mockito.verify(view).setSource("")
                            Mockito.verify(view).setTranslate("")
                            Mockito.verify(view).triggerFavoriteBtn(View.GONE)
                            Mockito.verifyNoMoreInteractions(view)
                        })

                        cmdShould("cmd after start translate", {
                            assertCmd(cmdAfterStartTranslate, TranslatePresenter.TranslateCmd::class)
                        })

                        given("translate result", {
                            val translatedPhrase = Phrase("cat", "кот", "en-ru", false, Date())
                            val (stateAfterTranslateResult, cmdAfterTranslateResult) =
                                    translatePresenter.update(
                                            TranslatePresenter.TranslateResultMsg(translatedPhrase),
                                            stateAfterStartTranslate)

                            stateAssert("state after translate result", {
                                assertEquals(stateAfterStartTranslate.
                                        copy(isLoading = false,
                                                phrase = translatedPhrase),
                                        stateAfterTranslateResult)
                            })

                            viewAssert("view after translate result", {
                                translatePresenter.render(stateAfterTranslateResult)

                                Mockito.verify(view).isAttached()
                                Mockito.verify(view).triggerFromText(true)
                                Mockito.verify(view).triggerToText(true)
                                Mockito.verify(view).triggerProgress(View.GONE)
                                Mockito.verify(view).triggerSource(View.VISIBLE)
                                Mockito.verify(view).triggerTranslate(View.VISIBLE)
                                Mockito.verify(view).setFromLang("english")
                                Mockito.verify(view).setToLang("russian")
                                Mockito.verify(view).setText("cat")
                                Mockito.verify(view).setSource("cat")
                                Mockito.verify(view).setTranslate("кот")
                                Mockito.verify(view).triggerFavoriteBtn(View.VISIBLE)
                                Mockito.verify(view).setFavoriteBtn(false)
                                Mockito.verifyNoMoreInteractions(view)
                            })

                            cmdShould("cmd after translate result", {
                                assertCmd(cmdAfterTranslateResult, None::class)
                            })

                            given("change lang same from", {
                                val (stateAfterChangeLangFromSame, cmdAfterChangeLangFromSame) =
                                        translatePresenter.update(
                                                TranslatePresenter.ChangeLangsMsg(Lang("en", "english"), true),
                                                stateAfterTranslateResult)

                                stateAssert("state after change lang same from", {
                                    assertEquals(stateAfterTranslateResult,
                                            stateAfterChangeLangFromSame)
                                })

                                it("cmd after change lang same from", {
                                    assertCmd(cmdAfterChangeLangFromSame, None::class)
                                })
                            })

                            given("change lang same to", {
                                val (stateAfterChangeLangToSame, cmdAfterChangeLangToSame) =
                                        translatePresenter.update(
                                                TranslatePresenter.ChangeLangsMsg(Lang("ru", "russian"), false),
                                                stateAfterTranslateResult)

                                stateAssert("state after change lang same to", {
                                    assertEquals(stateAfterTranslateResult,
                                            stateAfterChangeLangToSame)
                                })

                                cmdShould("cmd after change lang same to", {
                                    assertCmd(cmdAfterChangeLangToSame,
                                            None::class)
                                })
                            })

                            given("change lang from", {
                                val (stateAfterChangeLangFrom, cmdAfterChangeLangFrom) =
                                        translatePresenter.update(
                                                TranslatePresenter.ChangeLangsMsg(Lang("de", "deutsch"), true),
                                                stateAfterTranslateResult)

                                stateAssert("state after change lang same from", {
                                    assertEquals(stateAfterTranslateResult.copy(
                                            isLoading = true,
                                            langFrom = Lang("de", "deutsch")
                                    ), stateAfterChangeLangFrom)
                                })

                                viewAssert("view after translate result", {
                                    translatePresenter.render(stateAfterChangeLangFrom)
                                    Mockito.verify(view).isAttached()
                                    Mockito.verify(view).triggerFromText(false)
                                    Mockito.verify(view).triggerToText(false)
                                    Mockito.verify(view).triggerProgress(View.VISIBLE)
                                    Mockito.verify(view).triggerSource(View.GONE)
                                    Mockito.verify(view).triggerTranslate(View.GONE)
                                    Mockito.verify(view).setFromLang("deutsch")
                                    Mockito.verify(view).setToLang("russian")
                                    Mockito.verify(view).setText("cat")
                                    Mockito.verify(view).triggerFavoriteBtn(View.GONE)
                                    Mockito.verifyNoMoreInteractions(view)
                                })

                                cmdShould("cmd after change lang from", {
                                    assertBatch(cmdAfterChangeLangFrom,
                                            TranslatePresenter.SaveCurrentLangsCmd::class,
                                            TranslatePresenter.CheckFavoriteCmd::class,
                                            TranslatePresenter.TranslateCmd::class)
                                })
                            })

                            given("change lang to", {
                                val (stateAfterChangeLangTo, cmdAfterChangeLangTo) =
                                        translatePresenter.update(
                                                TranslatePresenter.ChangeLangsMsg(Lang("es", "spanish"), false),
                                                stateAfterTranslateResult)

                                stateAssert("state after change lang same from", {
                                    assertEquals(stateAfterTranslateResult.copy(
                                            isLoading = true,
                                            langTo = Lang("es", "spanish")
                                    ), stateAfterChangeLangTo)
                                })



                                viewAssert("view after change lang same from", {
                                    translatePresenter.render(stateAfterChangeLangTo)

                                    //view after translate result
                                    Mockito.verify(view).isAttached()
                                    Mockito.verify(view).triggerFromText(false)
                                    Mockito.verify(view).triggerToText(false)
                                    Mockito.verify(view).triggerProgress(View.VISIBLE)
                                    Mockito.verify(view).triggerSource(View.GONE)
                                    Mockito.verify(view).triggerTranslate(View.GONE)
                                    Mockito.verify(view).setFromLang("english")
                                    Mockito.verify(view).setToLang("spanish")
                                    Mockito.verify(view).setText("cat")
                                    Mockito.verify(view).triggerFavoriteBtn(View.GONE)
                                    Mockito.verifyNoMoreInteractions(view)
                                })

                                cmdShould("cmd after change lang to", {
                                    assertBatch(cmdAfterChangeLangTo,
                                            TranslatePresenter.SaveCurrentLangsCmd::class,
                                            TranslatePresenter.CheckFavoriteCmd::class,
                                            TranslatePresenter.TranslateCmd::class)
                                })
                            })
                        })
                    })
                })
            })
        })

    }

})