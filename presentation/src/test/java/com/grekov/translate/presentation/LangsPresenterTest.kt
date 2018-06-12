package com.grekov.translate.presentation

import com.grekov.translate.domain.elm.Idle
import com.grekov.translate.domain.elm.Init
import com.grekov.translate.domain.interactor.lang.GetLangsUseCase
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.domain.repository.ITranslateRepository
import com.grekov.translate.presentation.core.elm.Program
import com.grekov.translate.presentation.langs.presenter.Langs
import com.grekov.translate.presentation.langs.presenter.LangsPresenter
import com.grekov.translate.presentation.langs.view.ILangsView
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

class LangsPresenterTest {

    lateinit var view: ILangsView
    lateinit var presenter: LangsPresenter
    lateinit var useCase: GetLangsUseCase
    lateinit var repo: ITranslateRepository
    lateinit var program: Program<LangsPresenter.LangsState>

    @Before
    fun setUp() {
        view = Mockito.mock(ILangsView::class.java)
        repo = Mockito.mock(ITranslateRepository::class.java)
        useCase = GetLangsUseCase(Schedulers.trampoline(), repo)
        program = Program(Schedulers.trampoline())
        presenter = LangsPresenter(view, program, useCase, true)

        Mockito.`when`(repo.langsFromCache)
                .thenReturn(Single.just(cacheLangs()))
        Mockito.`when`(repo.langsFromCloud)
                .thenReturn(
                        Single.just(cloudLangs()))
        Mockito.`when`(view.isAttached())
                .thenReturn(true)
    }

    private fun initState(): LangsPresenter.LangsState {
        return LangsPresenter.LangsState(true, true, true, Langs(true), listOf())
    }

    private fun withCacheState(): LangsPresenter.LangsState {
        return initState().copy(langs = cacheLangs(), isLoading = false)
    }

    private fun cacheLangs(): List<Lang> {
        return listOf(
                Lang("en", "English"),
                Lang("ru", "Russian"))
    }

    private fun cloudLangs(): List<Lang> {
        return listOf(
                Lang("ru", "Russian"),
                Lang("en", "English"),
                Lang("fr", "French"),
                Lang("de", "German"))
    }

    @Test
    fun initTest() {
        val upd = presenter.update(Init, initState())
        presenter.render(upd.first)
        val cacheMsg = presenter.call(upd.second).blockingGet()

        Mockito.verify(view).isAttached()
        Mockito.verify(view).showUpdateTitle()
        Mockito.verify(view).showProgress()
        Mockito.verify(view).showErrorText(false)
        Mockito.verifyNoMoreInteractions(view)
        Assert.assertThat(cacheMsg, instanceOf(LangsPresenter.LangsFromCacheMsg::class.java))
        Assert.assertEquals(2, (cacheMsg as LangsPresenter.LangsFromCacheMsg).langs?.size)
    }

    @Test
    fun afterCache1Test() {
        val upd = presenter.update(LangsPresenter.LangsFromCacheMsg(cacheLangs()), initState())
        presenter.render(upd.first)
        val cloudMsg = presenter.call(upd.second).blockingGet()

        Mockito.verify(view).isAttached()
        Mockito.verify(view).showUpdateTitle()
        Mockito.verify(view).hideProgress()
        Mockito.verify(view).showLangs(cacheLangs())
        Mockito.verify(view).showErrorText(false)
        Mockito.verifyNoMoreInteractions(view)
        Assert.assertThat(cloudMsg, instanceOf(LangsPresenter.LangsFromCloudMsg::class.java))
        val state = upd.first
        Assert.assertEquals(false, state.isLoading)
        Assert.assertEquals(cacheLangs(), state.langs)
    }

    @Test
    fun afterCache2Test() {
        val upd = presenter.update(LangsPresenter.LangsFromCacheMsg(listOf()), initState())
        presenter.render(upd.first)
        val cloudMsg = presenter.call(upd.second).blockingGet()

        Mockito.verify(view).isAttached()
        Mockito.verify(view).showUpdateTitle()
        Mockito.verify(view).showProgress()
        Mockito.verify(view).showErrorText(false)
        Mockito.verifyNoMoreInteractions(view)
        Assert.assertThat(cloudMsg, instanceOf(LangsPresenter.LangsFromCloudMsg::class.java))
        val state = upd.first
        Assert.assertEquals(false, state.isLoading)
        Assert.assertEquals(listOf<Lang>(), state.langs)
    }

    @Test
    fun afterCloudTest() {
        val upd = presenter.update(LangsPresenter.LangsFromCloudMsg(cloudLangs()), withCacheState())
        presenter.render(upd.first)
        val idleMsg = presenter.call(upd.second).blockingGet()

        Mockito.verify(view).isAttached()
        Mockito.verify(view).showTitle()
        Mockito.verify(view).hideProgress()
        Mockito.verify(view).showErrorText(false)
        Mockito.verify(view).showLangs(cloudLangs())
        Mockito.verifyNoMoreInteractions(view)

        Assert.assertThat(idleMsg, instanceOf(Idle::class.java))
        val state = upd.first
        Assert.assertEquals(false, state.isLoading)
        Assert.assertEquals(false, state.isSyncing)
        Assert.assertEquals(true, state.isFrom)
        Assert.assertEquals(cloudLangs(), state.langs)
    }

    @Test
    fun afterCloudEmptyTest() {
        val upd = presenter.update(LangsPresenter.LangsFromCloudMsg(listOf()), withCacheState())
        presenter.render(upd.first)
        val idleMsg = presenter.call(upd.second).blockingGet()

        Mockito.verify(view).isAttached()
        Mockito.verify(view).showTitle()
        Mockito.verify(view).hideProgress()
        Mockito.verify(view).showErrorText(true)
        Mockito.verify(view).setErrorTextToEmpty()
        Mockito.verifyNoMoreInteractions(view)

        Assert.assertThat(idleMsg, instanceOf(Idle::class.java))
        val state = upd.first
        Assert.assertEquals(false, state.isLoading)
        Assert.assertEquals(false, state.isSyncing)
        Assert.assertEquals(true, state.isFrom)
        Assert.assertEquals(listOf<Lang>(), state.langs)
    }

}