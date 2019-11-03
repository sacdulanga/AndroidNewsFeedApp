package com.sac.dulanga.newsapp.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by chamithdulanga on 2019-11-02.
 */

@Parcelize
data class ArticleSource(
        val name: String ,
        val id: String
): Parcelable
