package com.sac.dulanga.newsapp.model.entities

import com.sac.dulanga.newsapp.dto.NewsArticle

/**
 * Created by chamithdulanga on 2019-11-02.
 */

data class AuthResponse(
        val request_id: String,
        val known_device: Boolean,
        val verification_id: String
)
