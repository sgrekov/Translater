package com.grekov.translate.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Lang(val code: String, val name: String) : Parcelable

