package com.sac.dulanga.newsapp.model.entities

import com.sac.dulanga.newsapp.dto.NewsArticle

/**
 * Created by chamithdulanga on 2019-11-02.
 */

data class AuthRequest(
        var account_id: String,
        var acs_id: String,
        var bank_id: String,
        var profile_count: String,
        var device_token: String,
        var browser_fingerprint: String
)
