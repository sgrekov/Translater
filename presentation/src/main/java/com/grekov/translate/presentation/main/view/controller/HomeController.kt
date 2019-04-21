package com.grekov.translate.presentation.main.view.controller

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.grekov.translate.R
import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.presentation.core.presenter.IBasePresenter
import com.grekov.translate.presentation.core.view.controller.BaseController
import com.grekov.translate.presentation.history.view.controller.HistoryController
import com.grekov.translate.presentation.main.di.component.MainViewComponent
import com.grekov.translate.presentation.main.di.module.HomeScreenModule
import com.grekov.translate.presentation.main.presenter.HomePresenter
import com.grekov.translate.presentation.main.view.IHomeView
import com.grekov.translate.presentation.translate.view.controller.TranslateController
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import javax.inject.Inject


class HomeController : BaseController, IHomeView {

    @BindView(R.id.bottom_navigation) @JvmField var bottomNavigationView: BottomNavigationViewEx? = null
    private lateinit var translateRouter: Router
    private lateinit var historyRouter: Router
    private lateinit var favoritesRouter: Router
    @Inject lateinit var presenter: HomePresenter
    @BindView(R.id.translate_container) lateinit var translateContainer: ViewGroup
    @BindView(R.id.history_container) lateinit var historyContainer: ViewGroup
    @BindView(R.id.favorites_container) lateinit var favoritesContainer: ViewGroup

    companion object {
        const val TRANSLATE_TAG = "TRANSLATE_TAG"
    }

    constructor() : super(Bundle())

    constructor(b: Bundle) : super(b) {
        retainViewMode = RetainViewMode.RETAIN_DETACH
    }

    override val layoutId: Int
        get() = R.layout.home_controller_layout

    override fun onViewBound(view: View) {
        translateRouter = getChildRouter(translateContainer)
        historyRouter = getChildRouter(historyContainer)
        favoritesRouter = getChildRouter(favoritesContainer)

        bottomNavigationView?.setIconVisibility(false)
        bottomNavigationView?.setOnNavigationItemSelectedListener(menuListener)

    }

    internal var menuListener = { item : MenuItem ->
        when (item.itemId) {
            R.id.bottom_menu_translate -> presenter.translateSelect()
            R.id.bottom_menu_history -> presenter.historySelect()
            R.id.bottom_menu_favorites -> presenter.favoritesSelect()
        }
        true
    }

    override fun setupComponent() {
        this.getComponent(MainViewComponent::class.java)
                .plusHomeComponent(HomeScreenModule(this)).inject(this)
    }

    override val title: String = ""

    override fun getPresenter(): IBasePresenter {
        return presenter
    }


    override fun setTab(tabNum: Int) {
        bottomNavigationView?.onNavigationItemSelectedListener = null
        bottomNavigationView?.currentItem = tabNum
        bottomNavigationView?.setOnNavigationItemSelectedListener(menuListener)
    }

    override fun showTranslate(selectedPhrase: Phrase?) {
        if (!translateRouter.hasRootController()) {
            translateRouter.setRoot(RouterTransaction.with(TranslateController()).tag(TRANSLATE_TAG))
        }
        if (selectedPhrase != null) {
            (translateRouter.getControllerWithTag(TRANSLATE_TAG) as TranslateController).setLang(selectedPhrase)
        }
        translateContainer.visibility = View.VISIBLE
        historyContainer.visibility = View.GONE
        favoritesContainer.visibility = View.GONE
    }

    override fun showHistory() {
        if (!historyRouter.hasRootController()) {
            historyRouter.setRoot(RouterTransaction.with(HistoryController(false)))
        }
        translateContainer.visibility = View.GONE
        historyContainer.visibility = View.VISIBLE
        favoritesContainer.visibility = View.GONE
    }

    override fun showFavorites() {
        if (!favoritesRouter.hasRootController()) {
            favoritesRouter.setRoot(RouterTransaction.with(HistoryController(true)))
        }
        translateContainer.visibility = View.GONE
        historyContainer.visibility = View.GONE
        favoritesContainer.visibility = View.VISIBLE
    }

    fun onPhraseClick(model: Phrase) {
        presenter.onPhraseClick(model)
    }
}
