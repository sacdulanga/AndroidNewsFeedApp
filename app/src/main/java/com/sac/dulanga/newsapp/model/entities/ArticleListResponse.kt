package com.sac.dulanga.newsapp.model.entities

import com.sac.dulanga.newsapp.dto.NewsArticle

/**
 * Created by chamithdulanga on 2019-11-02.
 */

data class ArticleListResponse(
        val status: String,
        val totalResults: Int,
        var articles: MutableList<NewsArticle>
)
