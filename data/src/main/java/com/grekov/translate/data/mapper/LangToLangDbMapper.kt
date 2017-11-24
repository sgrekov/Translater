package com.grekov.translate.data.mapper

import com.grekov.translate.data.model.cache.LangDbEntity
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.domain.utils.BaseDataMapper

import javax.inject.Inject

class LangToLangDbMapper @Inject
constructor() : BaseDataMapper<Lang, LangDbEntity>() {

    override fun transform(item: Lang): LangDbEntity {
        val langDb = LangDbEntity()
        langDb.code = item.code
        langDb.name = item.name
        return langDb
    }
}
