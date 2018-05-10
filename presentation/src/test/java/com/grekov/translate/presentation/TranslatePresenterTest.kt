package com.grekov.translate.presentation

import com.Spec
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
import com.grekov.translate.presentation.translate.elm.ChangeLangsMsg
import com.grekov.translate.presentation.translate.elm.CheckFavoriteCmd
import com.grekov.translate.presentation.translate.elm.FavoriteCheckResultMsg
import com.grekov.translate.presentation.translate.elm.LangsFromPrefsMsg
import com.grekov.translate.presentation.translate.elm.RetrieveLangsFromPrefsCmd
import com.grekov.translate.presentation.translate.elm.RotateLangsMsg
import com.grekov.translate.presentation.translate.elm.SaveCurrentLangsCmd
import com.grekov.translate.presentation.translate.elm.TextChangeMsg
import com.grekov.translate.presentation.translate.elm.TranslateCmd
import com.grekov.translate.presentation.translate.elm.TranslateMsg
import com.grekov.translate.presentation.translate.elm.TranslateResultMsg
import com.grekov.translate.presentation.translate.presenter.TranslatePresenter
import com.grekov.translate.presentation.translate.view.ITranslateView
import io.reactivex.schedulers.Schedulers
import mock
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.mockito.Mockito
import java.util.Date

class TranslatePresenterTest : Spek({

    var view: ITranslateView = mock()
    val appPrefs: IAppPreferencesManager = mock()
    val makeTranslateUseCase: MakeTranslateUseCase = mock()
    val makeFavUseCase: MakeFavoriteUseCase = mock()
    val checkFavUseCase: CheckFavoriteUseCase = mock()
    val loadLangsUseCaseSingle: LoadLangsByCodeUseCaseSingle = mock()
    val navigator: Navigator = mock()

    var translatePresenter = TranslatePresenter(
        view,
        Program(Schedulers.trampoline(), TimeTraveller()),
        appPrefs,
        navigator,
        makeTranslateUseCase,
        makeFavUseCase,
        checkFavUseCase,
        loadLangsUseCaseSingle
    )
    Mockito.`when`(view.isAttached()).thenReturn(true)

    val spec = Spec(translatePresenter)

    describe("translate screen") {

        beforeEachTest {
            Mockito.`when`(view.isAttached()).thenReturn(true)
        }

        afterEachTest {
            Mockito.reset(view)
        }

        val russianLang = Lang("ru", "russian")
        val englishLang = Lang("en", "english")
        val germanLang = Lang("de", "deutsch")

        given("translate screen init ", {
            val initialState = TranslatePresenter.TranslateState(
                langFrom = russianLang,
                langTo = englishLang
            )

            spec
                .withState(initialState)
                .whenUpdate(Init)
                .thenCmd(RetrieveLangsFromPrefsCmd)
                .andState { it.copy(isLoading = true) }
                .whenUpdate(
                    LangsFromPrefsMsg(
                        englishLang,
                        russianLang
                    )
                )
                .thenCmd(None).andState {
                    it.copy(
                        isLoading = false,
                        langFrom = englishLang,
                        langTo = russianLang
                    )
                }.whenUpdate(TextChangeMsg("cat"))
                .thenCmd(
                    CheckFavoriteCmd(
                        "cat",
                        englishLang,
                        russianLang
                    )
                )
                .andState { it.copy(currentText = "cat") }
                .whenUpdate(
                    FavoriteCheckResultMsg(
                        "cat",
                        englishLang,
                        russianLang,
                        true
                    )
                )
                .thenCmd(None).andState { it.copy(isFavorite = true) }

            val phrase = Phrase("cat", "кот", "en-ru", false, Date())

            given("start translate ", {
                spec
                    .whenUpdate(TranslateMsg)
                    .thenCmd(
                        TranslateCmd(
                            "cat",
                            englishLang,
                            russianLang
                        )
                    ).andState { it.copy(isLoading = true) }
                    .whenUpdate(
                        TranslateResultMsg(
                            phrase
                        )
                    ).thenCmd(None).andState { it.copy(isLoading = false, phrase = phrase) }

                given("change lang from", {

                    spec
                        .whenUpdate(
                            ChangeLangsMsg(
                                germanLang,
                                true
                            )
                        )
                        .thenState {
                            it.copy(
                                isLoading = true,
                                langFrom = germanLang
                            )
                        }
                        .andCmdBatch(
                            SaveCurrentLangsCmd(
                                germanLang,
                                russianLang
                            ),
                            CheckFavoriteCmd(
                                "cat",
                                germanLang,
                                russianLang
                            ),
                            TranslateCmd(
                                "cat",
                                germanLang,
                                russianLang
                            )
                        )

                    given("on flip langs click") {
                        spec
                            .whenUpdate(RotateLangsMsg)
                            .thenState {
                                it.copy(
                                    langTo = germanLang,
                                    langFrom = russianLang
                                )
                            }
                            .andCmdBatch(
                                SaveCurrentLangsCmd(
                                    russianLang,
                                    germanLang
                                ),
                                CheckFavoriteCmd(
                                    "cat",
                                    russianLang,
                                    germanLang
                                ),
                                TranslateCmd(
                                    "cat",
                                    russianLang,
                                    germanLang
                                )
                            )
                    }

                })
            })

        })

    }

})