package com.sac.dulanga.newsapp.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by chamithdulanga on 2019-11-02.
 */

@Parcelize
data class User(
        var name: String = "",
        var image: String = "",
        var email: String = "",
        var mobile: String = ""

): Parcelable
