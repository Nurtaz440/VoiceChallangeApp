package mening.dasturim.myvoiceapp.retrofit

import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {
    @POST("/api/v1/cabinet/synthesize")
    fun synthesize(
        @Query("t") text : String
    )
}