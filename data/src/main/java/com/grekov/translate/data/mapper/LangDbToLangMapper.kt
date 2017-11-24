package com.grekov.translate.data.mapper

import com.grekov.translate.data.model.cache.LangDbEntity
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.domain.utils.BaseDataMapper

import javax.inject.Inject


class LangDbToLangMapper @Inject
constructor() : BaseDataMapper<LangDbEntity, Lang>() {

    override fun transform(item: LangDbEntity): Lang {
        return Lang(item.code, item.name)
    }
}
