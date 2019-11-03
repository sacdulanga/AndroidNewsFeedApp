package com.sac.dulanga.newsapp.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by chamithdulanga on 2019-11-02.
 */

@Parcelize
data class NewsArticle(
        val source: ArticleSource,
        val author: String,
        val title: String,
        val description: String,
        val url: String,
        val urlToImage: String,
        val publishedAt: String,
        val content: String
): Parcelable
