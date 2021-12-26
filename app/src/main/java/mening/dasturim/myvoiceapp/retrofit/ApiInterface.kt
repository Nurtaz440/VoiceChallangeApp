package mening.dasturim.myvoiceapp.retrofit

import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiInterface {
    @POST("/api/v1/cabinet/synthesize")
    fun synthesize(
        @Query("t") text : String
    )

    @Streaming
    @GET
    suspend fun downloadFile(@Url fileUrl:String): retrofit2.Response<ResponseBody>
}