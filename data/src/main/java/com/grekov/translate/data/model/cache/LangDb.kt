package com.grekov.translate.data.model.cache

import io.requery.Entity
import io.requery.Key
import io.requery.Persistable

@Entity
interface LangDb : Persistable {

    @get:Key
    var code: String

    var name: String

}
