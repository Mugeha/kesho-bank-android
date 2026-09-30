package com.keshobank.mobile.data.remote

import com.keshobank.mobile.data.remote.dto.LoginResponse
import com.keshobank.mobile.data.remote.dto.StatementResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Maps to the optional backend/ service (Phase 4). #19 (IDOR) and #20 (mass
// assignment) live server-side; this interface just describes the contract
// the app calls.
interface ApiService {

    // Vuln #18: credentials sent as URL query params instead of a request
    // body, so they end up in access logs, proxy logs, and browser/HTTP
    // client history verbatim.
    @GET("api/auth/login")
    suspend fun login(
        @Query("accountNumber") accountNumber: String,
        @Query("password") password: String
    ): Response<LoginResponse>

    @GET("api/accounts/{accountId}/statement")
    suspend fun getStatement(@Path("accountId") accountId: String): Response<StatementResponse>
}
