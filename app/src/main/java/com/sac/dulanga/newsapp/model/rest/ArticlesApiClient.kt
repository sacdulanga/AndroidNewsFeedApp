package com.sac.dulanga.newsapp.model.rest

import com.sac.dulanga.newsapp.model.entities.ArticleListResponse
import com.sac.dulanga.newsapp.model.entities.AuthRequest
import com.sac.dulanga.newsapp.model.entities.AuthResponse
import io.reactivex.Single
import retrofit2.http.*

/**
 * Created by chamithdulanga on 2019-11-02.
 */

interface ArticlesApiClient {

    @GET("top-headlines")
    fun getMyArticles(@Query ("country") country: String, @Query("apiKey") apiKey: String): Single<ArticleListResponse>

    @GET("everything")
    fun getFilteredArticles(@Query ("q") query: String, @Query("apiKey") apiKey: String): Single<ArticleListResponse>


    @POST("auth-request")
    @Headers("Content-Type: application/json")
    fun postSampleAPI(@Header ("x-api-key") apiKey: String, @Body authRequest: AuthRequest): Single<AuthResponse>

}
