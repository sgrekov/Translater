package com.grekov.translate.data.mapper

import com.grekov.translate.data.model.cache.PhraseDbEntity
import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.domain.utils.BaseDataMapper
import javax.inject.Inject

class PhraseToPhraseDbMapper @Inject constructor() : BaseDataMapper<Phrase, PhraseDbEntity>() {

    override fun transform(item: Phrase): PhraseDbEntity {
        val phraseDb = PhraseDbEntity()
        phraseDb.lang = item.langsLiteral
        phraseDb.source = item.source
        phraseDb.translate = item.translate
        phraseDb.created = item.created
        return phraseDb
    }
}