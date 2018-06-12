package com.grekov.translate.data.api

import com.grekov.translate.data.model.cloud.LangRespone
import com.grekov.translate.data.model.cloud.TranslateResponse
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface TranslateService {

    @POST("/api/v1.5/tr.json/getLangs")
    fun getLangs(@Query("ui") code: String, @Query("key") apiKey: String): Single<LangRespone>

    @FormUrlEncoded
    @POST("/api/v1.5/tr.json/translate")
    fun translate(@Query("lang") lang: String,
                  @Query("ui") ui: String,
                  @Query("key") apiKey: String,
                  @Field("text") text: String): Single<TranslateResponse>
}
