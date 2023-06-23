package com.sdt.trproject.services

import com.google.gson.annotations.SerializedName
import com.sdt.trproject.SharedPrefKeys
import com.sdt.trproject.utils.RetrofitRequestService
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface TrainApiService : RetrofitRequestService {
    companion object {
        const val TRAIN_RESERVATION = "/train/reservation"
    }

    @Headers(
        "accept: application/json",
        "content-type: application/json",
    )
    @POST("/train/inquiry")
    fun searchTrainSchedule(@Body requestData: RequestBody): Call<SearchTrainScheduleResponse>

    @POST("/train/seat")
    fun requestTrainSeats(@Body requestData: RequestBody): Call<RequestTrainSeatsResponse>

    @POST("/train/reservation")
    fun requestTrainReservation(@Body requestData: RequestBody): Call<ResponseBody>

    @POST("{path}")
    override fun onRequest(
        @Path("path") requestPath: String,
        @Body requestBody: RequestBody
    ): Call<ResponseBody> {
        return when (requestPath) {
            TRAIN_RESERVATION -> {
                requestTrainReservation(requestBody) as Call<ResponseBody>
            }
            else -> {
                requestTrainReservation(requestBody) as Call<ResponseBody>
            }
        }
    }
}

data class SearchTrainScheduleItem(
    @SerializedName("adultcharge")
    val adultCharge: String,
    @SerializedName("arrplacename")
    val arrPlaceName: String,
    @SerializedName("arrplandtime")
    val arrPlandTime: String,
    @SerializedName("depplacename")
    val depPlaceName: String,
    @SerializedName("depplandtime")
    val depPlandTime: String,
    @SerializedName("traingradename")
    val trainGradeName: String,
    @SerializedName("trainno")
    val trainNo: Int,
    @SerializedName("date")
    val date: String,
    @SerializedName("common")
    val common: Boolean,
    @SerializedName("vip")
    val vip: Boolean
)

data class RequestTrainSeatsItem(
    @SerializedName("seat")
    val seat: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("arriveTime")
    val arrivalTime: String,
    @SerializedName("arriveStation")
    val arrivalStation: String,
    @SerializedName("departTime")
    val departureTime: String,
    @SerializedName("departStation")
    val departureStation: String,
    @SerializedName("carriage")
    val carriage: Int,
    @SerializedName("trainNo")
    val trainNo: String
)


data class SearchTrainScheduleResponse(
    // 응답 데이터 필드 정의
    @SerializedName("result")
    val result: String,
    @SerializedName("data")
    val data: List<SearchTrainScheduleItem>
)

data class RequestTrainSeatsResponse(
    @SerializedName("result")
    val result: String,
    @SerializedName("data")
    val data: List<RequestTrainSeatsItem>
)

data class RequestTrainReservationResponse(
    @SerializedName("result")
    val result: String,
)