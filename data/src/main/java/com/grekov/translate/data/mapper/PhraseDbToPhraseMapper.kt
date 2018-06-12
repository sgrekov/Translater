package com.grekov.translate.data.mapper

import com.grekov.translate.data.model.cache.PhraseDbEntity
import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.domain.utils.BaseDataMapper
import javax.inject.Inject

class PhraseDbToPhraseMapper @Inject constructor() : BaseDataMapper<PhraseDbEntity, Phrase>() {


    override fun transform(item: PhraseDbEntity): Phrase {
        return Phrase(item.source,
                item.translate,
                item.lang,
                item.favorite,
                item.created)
    }
}