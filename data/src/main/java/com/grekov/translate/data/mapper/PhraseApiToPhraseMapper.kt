package com.grekov.translate.data.mapper

import com.grekov.translate.data.model.cloud.TranslateResponse
import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.domain.utils.BaseDataMapper
import java.util.*
import javax.inject.Inject

class PhraseApiToPhraseMapper @Inject
constructor()//constructor for injection
    : BaseDataMapper<TranslateResponse, Phrase>() {

    override fun transform(item: TranslateResponse): Phrase {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun transform(translateResponse: TranslateResponse, from: String): Phrase {
        return Phrase(from, translateResponse.text?.get(0), translateResponse.lang!!, false, Date())
    }
}
