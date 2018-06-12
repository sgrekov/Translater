package com.grekov.translate.presentation.core.elm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class AbstractScreen
abstract class Screen : AbstractScreen(), Parcelable

@Parcelize
class Start : Screen(), Parcelable