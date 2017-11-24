package com.grekov.translate.presentation.core.framework

import android.content.Context
import com.grekov.translate.domain.IResourceManager

class AndroidResourceManager(val context: Context) : IResourceManager {

    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}