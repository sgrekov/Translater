package com.grekov.translate.data.mapper

import com.grekov.translate.data.model.cloud.LangRespone
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.domain.utils.BaseDataMapper
import java.util.*
import javax.inject.Inject

class LangApiToLangMapper @Inject
constructor()//constructor for injection
    : BaseDataMapper<LangRespone, List<Lang>>() {

    override fun transform(item: LangRespone): List<Lang> {
        val langs = LinkedList<Lang>()
        item.langs?.let {
            for ((key, value) in it) {
                langs.add(Lang(key, value))
            }
        }
        return langs
    }
}