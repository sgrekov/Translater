package com.grekov.translate.presentation.langs.view.controller

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.grekov.translate.R
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.presentation.core.presenter.IBasePresenter
import com.grekov.translate.presentation.core.utils.BundleBuilder
import com.grekov.translate.presentation.core.view.controller.BaseController
import com.grekov.translate.presentation.langs.di.module.LangsModule
import com.grekov.translate.presentation.langs.presenter.LangsPresenter
import com.grekov.translate.presentation.langs.view.ILangsView
import com.grekov.translate.presentation.langs.view.controller.LangsController.LangsAdapter.ViewHolder
import com.grekov.translate.presentation.main.di.component.MainViewComponent
import com.grekov.translate.presentation.translate.view.controller.TranslateController
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class LangsController(b: Bundle) : BaseController(b), ILangsView {

    @Inject lateinit var langsPresenter: LangsPresenter
    internal lateinit var adapter: LangsAdapter
    lateinit var langsListRV: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var errorTV: TextView
    lateinit var titleTv: TextView
    internal var isFrom: Boolean

    constructor(translateController: TranslateController, isFrom: Boolean) : this(BundleBuilder(Bundle()).putBoolean(IS_FROM, isFrom).build()) {
        targetController = translateController
    }

    init {
        isFrom = b.getBoolean(IS_FROM)
    }

    override val title: String
        get() = getString(R.string.langs_title)

    override fun setupComponent() {
        this.getComponent(MainViewComponent::class.java)
                .plusLangsComponent(
                        LangsModule(this, isFrom))
                .inject(this)
    }

    override fun getPresenter(): IBasePresenter {
        return langsPresenter
    }

    override val layoutId: Int = R.layout.langs_layout

    override fun onViewBound(view: View) {
        Timber.d("translate onViewBound")
        langsListRV = view.findViewById(R.id.langs_list_rv) as RecyclerView
        progressBar = view.findViewById(R.id.langs_list_pb) as ProgressBar
        errorTV = view.findViewById(R.id.langs_list_error_tv) as TextView
        titleTv = view.findViewById(R.id.history_title) as TextView

        adapter = LangsAdapter(LayoutInflater.from(applicationContext), ArrayList())

        langsListRV.layoutManager = LinearLayoutManager(activity)
        langsListRV.adapter = adapter
    }


    override fun showLangs(langs: List<Lang>) {
        adapter.setItems(langs)
        adapter.notifyDataSetChanged()
        langsListRV.visibility = View.VISIBLE
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun showTitle() {
        titleTv.text = title
    }

    override fun showUpdateTitle() {
        titleTv.text = getString(R.string.sync_txt)
    }

    override fun selectLang(model: Lang, isFrom: Boolean) {
        val target = targetController
        router.popController(this)
        if (target != null && target is TargetLangSelectListener) {
            target.onLangPicked(model, isFrom)
        }
    }

    override fun handleBack(): Boolean {
        langsPresenter.handleBack()
        return false
    }

    override fun back() {
        router.popController(this)
    }

    override fun setErrorTextToEmpty() {
        errorTV.visibility = View.VISIBLE
        errorTV.setText(R.string.no_langs_found)
    }

    override fun showErrorText(show: Boolean) {
        if (show) {
            errorTV.visibility = View.VISIBLE
        } else {
            errorTV.visibility = View.GONE
        }
    }

    interface TargetLangSelectListener {
        fun onLangPicked(lang: Lang, isFrom: Boolean)

        fun onLangError()
    }

    internal inner class LangsAdapter(private val inflater: LayoutInflater, private var items: List<Lang>) : RecyclerView.Adapter<ViewHolder>() {

        fun setItems(items: List<Lang>) {
            this.items = items
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(inflater.inflate(R.layout.lang_list_item_layout, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            @BindView(R.id.lang_name) lateinit var langNameTV: TextView

            private var model: Lang? = null

            init {
                ButterKnife.bind(this, itemView)
            }

            @SuppressLint("SetTextI18n")
            fun bind(item: Lang) {
                model = item
                langNameTV.text = item.name
            }

            @OnClick(R.id.lang_list_item)
            fun onRowClick() {
                langsPresenter.onLangClick(model!!)
            }

        }
    }

    companion object {

        val IS_FROM = "is_from"
    }
}
