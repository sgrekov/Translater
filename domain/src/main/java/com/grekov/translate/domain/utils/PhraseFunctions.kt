package com.grekov.translate.domain.utils

import com.grekov.translate.domain.model.Lang

fun getLangLiteral(from: Lang, to: Lang): String {
    return "${from.code.toLowerCase()}-${to.code.toLowerCase()}"
}