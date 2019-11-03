package com.sac.dulanga.newsapp.model.rest

import com.sac.dulanga.newsapp.model.entities.ArticleListResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by chamithdulanga on 2019-11-02.
 */

interface ArticlesApiClient {

    @GET("top-headlines")
    fun getMyArticles(@Query ("country") country: String, @Query("apiKey") apiKey: String): Single<ArticleListResponse>

    @GET("everything")
    fun getFilteredArticles(@Query ("q") query: String, @Query("apiKey") apiKey: String): Single<ArticleListResponse>

}
