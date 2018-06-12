package com.grekov.translate.domain.utils

import java.util.*

abstract class BaseDataMapper<F, T> {

    fun transformList(input: Collection<F>): List<T> {
        val output = ArrayList<T>(input.size)
        input.mapTo(output) { transform(it) }
        return output
    }

    abstract fun transform(item: F): T

}
