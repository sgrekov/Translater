package com.grekov.translate.data.model.cloud

import com.squareup.moshi.Json

class LangRespone {

    @Json(name = "langs")
    var langs: Map<String, String>? = null

    @Json(name = "dirs")
    internal var dirs: List<String>? = null
}
