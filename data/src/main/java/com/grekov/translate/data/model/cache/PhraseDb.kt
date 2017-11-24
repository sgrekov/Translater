package com.grekov.translate.data.model.cache

import io.requery.Entity
import io.requery.Key
import java.util.*

@Entity
interface PhraseDb {

    var created: Date

    @get:Key
    var source: String

    var translate: String

    @get:Key
    var lang: String

    var favorite: Boolean

}
