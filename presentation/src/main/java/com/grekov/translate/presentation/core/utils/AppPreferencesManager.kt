package com.grekov.translate.presentation.core.utils


import android.content.SharedPreferences
import com.grekov.translate.domain.IAppPreferencesManager
import com.grekov.translate.domain.model.Lang
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

import javax.inject.Singleton

@Singleton
class AppPreferencesManager(private val preferences: SharedPreferences) : IAppPreferencesManager {

    companion object {
        const val KEY_FROM_CODE: String = "key_from_code"
        const val KEY_FROM_NAME: String = "key_from_name"
        const val KEY_TO_CODE: String = "key_to_code"
        const val KEY_TO_NAME: String = "key_to_name"
    }

    override fun getLangs(): Single<Pair<Lang, Lang>> {
        return Single.fromCallable {
            val fromCode = preferences.getString(KEY_FROM_CODE, null)
            val fromName = preferences.getString(KEY_FROM_NAME, null)
            val langFrom = if (fromCode != null && fromName != null) {
                Lang(fromCode, fromName)
            } else {
                if (Locale.getDefault().language == "ru") {
                    Lang("ru", "Русский")
                } else {
                    Lang("en", "English")
                }
            }
            val toCode = preferences.getString(KEY_TO_CODE, null)
            val toName = preferences.getString(KEY_TO_NAME, null)
            val langTo = if (toCode != null && toName != null) {
                Lang(toCode, toName)
            } else {
                if (Locale.getDefault().language == "ru") {
                    Lang("en", "Английский")
                } else {
                    Lang("ru", "Russian")
                }
            }
            return@fromCallable Pair(langFrom, langTo)
        }
    }

    override fun saveLangs(from: Lang, to: Lang): Completable {
        return Completable.fromCallable {
            val editor = preferences.edit()
            editor.putString(KEY_FROM_NAME, from.name)
            editor.putString(KEY_FROM_CODE, from.code)
            editor.putString(KEY_TO_NAME, to.name)
            editor.putString(KEY_TO_CODE, to.code)
            return@fromCallable editor.commit()
        }
    }


}
