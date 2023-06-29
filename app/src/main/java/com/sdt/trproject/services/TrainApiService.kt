package com.sdt.trproject.services

import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface TrainApiService {

    @Headers(
        "accept: application/json",
        "content-type: application/json",
    )

    @POST("/train/inquiry")
    fun requestTrainSchedule(@Body requestData: RequestBody): Call<RequestTrainScheduleResponse>

    @POST("/train/seat")
    fun requestTrainSeats(@Body requestData: RequestBody): Call<RequestTrainSeatsResponse>

    @POST("/train/reservation")
    fun requestTrainReservation(@Body requestData: RequestBody): Call<RequestTrainReservationResponse>

//    @POST("{path}")
//    override fun onRequest(
//        @Path("path") requestPath: String,
//        @Body requestBody: RequestBody
//    ): Call<ResponseBody> {
//        println("requestPath : $requestPath")
//        println("requestBody : $requestBody")
//        return when (requestPath) {
//            TRAIN_RESERVATION -> {
//                println("TRAIN_RESERVATION")
//                requestTrainReservation(requestBody) as Call<ResponseBody>
//            }
//            else -> {
//                println("TRAIN_RESERVATION???")
//                requestTrainReservation(requestBody) as Call<ResponseBody>
//            }
//        }
//    }
}

data class RequestTrainScheduleItem(
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


data class RequestTrainScheduleResponse(
    // 응답 데이터 필드 정의
    @SerializedName("result")
    val result: String,
    @SerializedName("data")
    val data: List<RequestTrainScheduleItem>
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
    @SerializedName("data")
    val data: RequestTrainReservationItem,
    @SerializedName("message")
    val message: String
)

data class RequestTrainReservationItem(
    @SerializedName("reservationId")
    val reservationId: String,
)