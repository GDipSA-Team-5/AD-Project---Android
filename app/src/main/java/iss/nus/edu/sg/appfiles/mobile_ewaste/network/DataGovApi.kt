package iss.nus.edu.sg.appfiles.mobile_ewaste.network

import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.GeoJsonResponse
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.PollDownloadResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface DataGovApi {
    @GET("v1/public/api/datasets/{datasetId}/poll-download")
    fun pollDownload(@Path("datasetId") datasetId: String): Call<PollDownloadResponse>

    @GET
    fun downloadGeoJson(@Url url: String): Call<GeoJsonResponse>
}
