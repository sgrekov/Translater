package com.grekov.translate.presentation.history.view.controller

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bluelinelabs.conductor.Controller
import com.grekov.translate.R
import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.presentation.core.elm.InputBinding
import com.grekov.translate.presentation.core.presenter.IBasePresenter
import com.grekov.translate.presentation.core.utils.BundleBuilder
import com.grekov.translate.presentation.core.view.controller.BaseController
import com.grekov.translate.presentation.history.di.module.HistoryModule
import com.grekov.translate.presentation.history.presenter.HistoryPresenter
import com.grekov.translate.presentation.history.view.IHistoryView
import com.grekov.translate.presentation.main.di.component.MainViewComponent
import com.grekov.translate.presentation.main.view.controller.HomeController
import javax.inject.Inject
import javax.inject.Provider

class HistoryController(bundle: Bundle) : BaseController(bundle), IHistoryView {

    internal var isFavorite: Boolean = false

    @Inject lateinit var presenterProvider: Provider<HistoryPresenter>
    var historyPresenter: HistoryPresenter? = null
    @BindView(R.id.history_title) lateinit var titleTV: TextView
    @BindView(R.id.clear_history) lateinit var clearTV: TextView
    @BindView(R.id.empty_text) lateinit var emptyTV: TextView
    @BindView(R.id.filter) lateinit var filterEt: EditText
    @BindView(R.id.history_list_rv) lateinit var historyRv: RecyclerView
    private var adapter: PhrasesAdapter? = null
    lateinit var filterBinding : InputBinding

    companion object {
        private val KEY_IS_FAVORITE = "key_favorite"
    }

    constructor(isFavorite: Boolean) : this(BundleBuilder(Bundle()).putBoolean(KEY_IS_FAVORITE, isFavorite).build()) {}

    init {
        this.isFavorite = bundle.getBoolean(KEY_IS_FAVORITE)
        retainViewMode = Controller.RetainViewMode.RETAIN_DETACH
    }

    override fun getPresenter(): IBasePresenter? {
        return historyPresenter
    }

    override val title = ""

    override fun setupComponent() {
        this.getComponent(MainViewComponent::class.java).plusHistoryComponent(HistoryModule(this, isFavorite)).inject(this)
    }

    override fun firstInitPresenter() {
        if (historyPresenter == null) {
            historyPresenter = presenterProvider.get()
        }
    }

    override val layoutId = R.layout.history_layout

    override fun onContextAvailable(context: Context) {
        super.onContextAvailable(context)
        adapter = PhrasesAdapter(LayoutInflater.from(applicationContext), listOf())
    }

    override fun onViewBound(view: View) {
        clearTV.setOnClickListener { view1 -> historyPresenter?.clearClick() }
        filterBinding = InputBinding(filterEt)
        historyPresenter?.addFilterChanges(filterBinding)
        historyRv.layoutManager = LinearLayoutManager(activity)
        historyRv.adapter = adapter
    }

    override fun setTitle(title: String) {
        titleTV.text = title
    }

    override fun triggerEmptyText(visibility: Int) {
        emptyTV.visibility = visibility
    }

    override fun setPhrases(phrases: List<Phrase>) {
        historyRv.visibility = View.VISIBLE
        adapter?.items = phrases
        adapter?.notifyDataSetChanged()
    }

    override fun setFilterHint(hint: String) {
        filterEt.hint = hint
    }

    override fun setSearchText(searchText: String) {
        filterBinding.bindInput(searchText)
    }

    internal inner class PhrasesAdapter(private val inflater: LayoutInflater, var items: List<Phrase>) : RecyclerView.Adapter<PhrasesAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhrasesAdapter.ViewHolder {
            return ViewHolder(inflater.inflate(R.layout.phrase_list_item_layout, parent, false))
        }

        override fun onBindViewHolder(holder: PhrasesAdapter.ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            @BindView(R.id.source)
            lateinit var sourceTV: TextView
            @BindView(R.id.translated)
            lateinit var translatedTV: TextView
            @BindView(R.id.lang)
            lateinit var langTV: TextView

            private var model: Phrase? = null

            init {
                ButterKnife.bind(this, itemView)
            }

            @SuppressLint("SetTextI18n")
            fun bind(item: Phrase) {
                model = item
                sourceTV.text = item.source
                translatedTV.text = item.translate
                langTV.text = item.langsLiteral
            }

            @OnClick(R.id.phrase_list_layout)
            fun onRowClick() {
                (parentController as HomeController).onPhraseClick(model!!)
            }

        }
    }
}
