package com.grekov.translate.data.api

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class ApiHeaders : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded")
        val request = builder.build()
        return chain.proceed(request)
    }
}
