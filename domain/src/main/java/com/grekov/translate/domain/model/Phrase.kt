package com.grekov.translate.domain.model

import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.util.*

//annotation class CustomParceler(val parcelerClass: KClass<out Parceler<*>>)
//
//@Parcelize
//class DateParcelable(val data: Date) : Parcelable {
//    companion object : Parceler<DateParcelable> {
//        override fun create(parcel: Parcel): DateParcelable {
//            return DateParcelable(Date(parcel.readLong()))
//        }
//
//        override fun DateParcelable.write(parcel: Parcel, flags: Int) {
//            parcel.writeLong(data.time)
//        }
//    }
//}

@PaperParcel
data class Phrase(val source: String,
                  val translate: String?,
                  val langsLiteral: String,
                  val favorite: Boolean = false,
                  var created: Date) : PaperParcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelPhrase.CREATOR
    }
}
