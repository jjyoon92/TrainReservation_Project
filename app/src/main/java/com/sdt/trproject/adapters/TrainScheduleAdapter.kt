package com.sdt.trproject.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.sdt.trproject.R
import com.sdt.trproject.ReservationDetailActivity
import com.sdt.trproject.services.*
import com.sdt.trproject.utils.RetrofitModule
//import com.sdt.trproject.utils.handle
import com.sdt.trproject.utils.requestBody
import com.sdt.trproject.utils.showToast
import dagger.hilt.android.qualifiers.ActivityContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TrainScheduleAdapter @Inject constructor(
    @ActivityContext private val context: Context,
    private var trainApiService: TrainApiService,

    ) : RecyclerView.Adapter<TrainScheduleAdapter.ViewHolder>() {
    private val scheduleItems: MutableList<RequestTrainScheduleItem> = mutableListOf()
    private val seatItems: MutableList<RequestTrainSeatsItem> = mutableListOf()
    private var lastCheckedPosition = -1
    private var selectedRadio = -1;
    private var currentPosition = -1
    private var remainingSeats: List<String>? = null

    private lateinit var recyclerView: RecyclerView
    private var departureStation: String = ""
    private var arrivalStation: String = ""
    private var departureDate: String = ""
    private var departureTime: Int = 0
    private var returnDate: String = ""
    private var returnTime: Int = 0
    private var adultCount: Int = 0
    private var childCount: Int = 0
    private var oldCount: Int = 0


    fun initData(
        recyclerView: RecyclerView,
        departureStation: String,
        arrivalStation: String,
        departureDate: String,
        departureTime: Int,
        returnDate: String,
        returnTime: Int,
        adultCount: Int,
        childCount: Int,
        oldCount: Int
    ) {
        this.recyclerView = recyclerView
        this.departureStation = departureStation
    }


//    private val client: OkHttpClient = OkHttpClient.Builder()
//        .cookieJar(JavaNetCookieJar(CookieManager()))
////        .cookieJar(AppCookieJar(context))
//        .build()

//    init {
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BuildConfig.SERVER_ADDR)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(client)
//            .build()
//
//        trainApiService = retrofit.create(TrainApiService::class.java)
//    }


    fun setData(data: List<RequestTrainScheduleItem>) {
        scheduleItems.clear()
        scheduleItems.addAll(data)
        notifyDataSetChanged() // 리스트의 크기와 아이템이 둘 다 변경되는 경우 ( 대체 가능? )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrainScheduleAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.train_schedule_list_item, parent, false)
        return ViewHolder(
            view,
            departureStation,
            arrivalStation,
            departureDate,
            adultCount,
            childCount,
            oldCount
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = scheduleItems[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int {
        return scheduleItems.size
    }

    inner class ViewHolder(
        itemView: View,
        departureStation: String?,
        arrivalStation: String?,
        departureDate: String?,
        adultCount: Int?,
        childCount: Int?,
        oldCount: Int?
    ) :
        RecyclerView.ViewHolder(itemView) {

        private val context: Context
            get() = itemView.context

        private val departureStation: String? = departureStation
        private val arrivalStation: String? = arrivalStation
        private val departureDate: String? = departureDate
        private val adultCount: Int? = adultCount
        private val childCount: Int? = childCount
        private val oldCount: Int? = oldCount

        private val selectedColor = ContextCompat.getColor(context, R.color.primary)
        private var selectedPosition = RecyclerView.NO_POSITION


        private val tvTrainNo: TextView = itemView.findViewById(R.id.tvTrainScheduleTrainNo)
        private val tvDepartureStation: TextView =
            itemView.findViewById(R.id.tvTrainScheduleDepartureStation)
        private val tvDepartureTime: TextView =
            itemView.findViewById(R.id.tvTrainScheduleDepartureTime)
        private val tvArrivalStation: TextView =
            itemView.findViewById(R.id.tvTrainScheduleArrivalStation)
        private val tvArrivalTime: TextView = itemView.findViewById(R.id.tvTrainScheduleArrivalTime)
        private val tvEstimatedTime: TextView = itemView.findViewById(R.id.tvEstimatedTime)

        val seatGradeSelectRadioGroup =
            itemView.findViewById<RadioGroup>(R.id.radioGroupSeatGradeSelect)
        val radioBtnPremiumSeatSelect =
            itemView.findViewById<RadioButton>(R.id.radioBtnPremiumSeatSelect)
        val radioBtnStandardSeatSelect =
            itemView.findViewById<RadioButton>(R.id.radioBtnStandardSeatSelect)
        val trainScheduleDropdownMenu =
            itemView.findViewById<LinearLayout>(R.id.trainScheduleDropdownMenuLl)
        val btnTrainScheduleReservation =
            itemView.findViewById<Button>(R.id.btnTrainScheduleReservation)

        val standardSeatText = SpannableString("일반실")
        val premiumSeatText = SpannableString("특실")


        init {


//            btnTrainScheduleReservation.setOnClickListener {
//                sendRequestReservationTrain(items[adapterPosition])
//            }

        }

        fun sendRequestReservationTrain(
            item: RequestTrainScheduleItem,
            carriage: Int,
            seat: String
        ) {
            val trainNo = item.trainNo
            val carriage = carriage
            val seat = seat
            val departureStation = item.depPlaceName
            val departureTime = item.depPlandTime
            val arrivalStation = item.arrPlaceName
            val arrivalTime = item.arrPlandTime
            val date = item.date

            val jsonObject = JSONObject()
            jsonObject.put("trainNo", trainNo)
            jsonObject.put("carriage", carriage)
            jsonObject.put("seat", seat)
            jsonObject.put("departStation", departureStation)
            jsonObject.put("departTime", departureTime)
            jsonObject.put("arriveStation", arrivalStation)
            jsonObject.put("arriveTime", arrivalTime)
            jsonObject.put("date", date)

            val jsonArray = JSONArray()
            jsonArray.put(jsonObject)

            println("trainNo : $trainNo, carriage : $carriage, seat : $seat, departStation : $departureStation, departTime : $departureTime, arriveStation : $arrivalStation, arriveTime : $arrivalTime, date : $date  ")
            println("jsonArray : $jsonArray")

            val requestBody = jsonArray.requestBody()

            val call = trainApiService.requestTrainReservation(requestBody)

            RetrofitModule.executeCall(call, object: RetrofitModule.ApiResponseCallback<RequestTrainReservationResponse> {
                override fun onSuccess(response: RequestTrainReservationResponse) {
                    println("response.toString() : ${response.toString()}")
                    println("id 확인 : ${response.data.reservationId}")
                    handleRequestTrainReservationResponse(response, scheduleItems[adapterPosition])
                }

                override fun onFailure(errorMessage: String) {
                    println("Error: $errorMessage")
                }
            })


//            trainApiService
//                .requestTrainReservation(jsonArray.requestBody())
//                .handle(
//                    context = context,
//                    onFail = { message: String, httpCode: Int ->
//                        println("message : $message, httpCode : $httpCode")
//                        println("실패뜸?")
//                    }
//                ) { response: RequestTrainReservationResponse? ->
//                    println("Success: $response")
//                    // 응답 로직 처리
//                    handleRequestTrainReservationResponse(response, scheduleItems[adapterPosition])
//                }


//            val requestBody = jsonArray.toString()
//                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
//
//            val call = trainApiService.requestTrainReservation(requestBody)
//            call.enqueue(object : Callback<RequestTrainReservationResponse> {
//                override fun onResponse(
//                    call: Call<RequestTrainReservationResponse>,
//                    response: Response<RequestTrainReservationResponse>
//                ) {
//                    if (!response.isSuccessful) {
//                        onFailure(call, t = HttpException(
//                            Response.error<RequestTrainReservationResponse>(response.code(), "예약 실패".toResponseBody(null))
//                        ))
//                        return
//                    }
//
//                    val apiResponse = response.body()
//                    if (apiResponse == null ) {
//                        onFailure(call, t = HttpException(
//                            Response.error<RequestTrainReservationResponse>(500, "응답 없음".toResponseBody(null))
//                        )
//                        )
//                        return
//                    }
//                    println("apiResponse : $apiResponse.toString()")
//                    println("result : ${apiResponse.result}")
//                    println("data : ${apiResponse.data}")
//                    handleRequestTrainReservationResponse(apiResponse, scheduleItems[adapterPosition])
//                }
//
//                override fun onFailure(call: Call<RequestTrainReservationResponse>, t: Throwable) {
//                    println("실패 ${t.message}")
//                }
//            })
        }

        fun handleRequestTrainReservationResponse(
            response: RequestTrainReservationResponse?,
            item: RequestTrainScheduleItem,
        ) {
            println("response!!! : $response")
            if (response != null) {
                if (response.result == "failure") {
                    context.showToast("예약 실패")
                    return
                }

                println("예약 성공!")

                val intent = Intent(context, ReservationDetailActivity::class.java).apply {
                    putExtra("TRAIN_NO", item.trainNo)
                    putExtra("CARRIAGE", if (radioBtnPremiumSeatSelect.isChecked) 1 else 2)
//                    putExtra("SEAT", selected)
                    putExtra("DATE", item.date)
                    putExtra("DEPARTSTATION", item.depPlaceName)
                    putExtra("DEPARTTIME", item.depPlandTime)
                    putExtra("ARRIVESTATION", item.arrPlaceName)
                    putExtra("ARRIVETIME", item.arrPlandTime)
                    putExtra("ADULTCOUNT", adultCount)
                    putExtra("CHILDCOUNT", childCount)
                    putExtra("OLDCOUNT", oldCount)
                }

                context.startActivity(intent)
            } else {
                println("response가 null")
            }




        }


        // 좌석 예약하기
        fun sendRequestSeatsTrain(item: RequestTrainScheduleItem, carriage: Int) {
            // 우선 해당 열차 및 호차에 좌석이 있는지 확인 위해 예약된 좌석 요청
            val trainNo = item.trainNo
            val date = item.date
            val departureTime = item.depPlandTime
            val arrivalTime = item.arrPlandTime

            val jsonObject = JSONObject()
            jsonObject.put("trainNo", trainNo)
            jsonObject.put("date", date)
            jsonObject.put("departTime", departureTime)
            jsonObject.put("arriveTime", arrivalTime)

//            trainApiService
//                .requestTrainSeats(jsonObject.requestBody())



            val requestBody = jsonObject.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val call = trainApiService.requestTrainSeats(requestBody)
            call.enqueue(object : Callback<RequestTrainSeatsResponse> {
                override fun onResponse(
                    call: Call<RequestTrainSeatsResponse>,
                    response: Response<RequestTrainSeatsResponse>
                ) {
                    if (!response.isSuccessful) {
                        println("좌석 요청 실패")

                        return
                    }

                    val apiResponse = response.body()


                    handleRequestTrainSeatsResponse(apiResponse, item, carriage)
                }

                override fun onFailure(call: Call<RequestTrainSeatsResponse>, t: Throwable) {
                    handleRequestTrainSeatsError()
                }
            })
        }

        fun handleRequestTrainSeatsResponse(
            requestTrainSeatsResponse: RequestTrainSeatsResponse?,
            item: RequestTrainScheduleItem,
            carriage: Int
        ) {
            // TODO: 좌석 요청에 대한 서버 응답 처리 로직
            println("좌석 요청 성공!")
            requestTrainSeatsResponse?.let {
                val gson = Gson()
                val dataString = gson.toJson(it.data)
                val result = it.result
                val data = it.data
                if (it.data == null) {
                    println("예약된 좌석이 없습니다.")
                    Toast.makeText(context, "데이터 조회가 불가능합니다.", Toast.LENGTH_LONG).show()
//                    return
                }

                println(data)

                // 열차에 예매된 좌석 저장할 리스트
                val seatList = mutableListOf<String>()

                // 좌석 정보 순회
                for (seatItem in data) {
                    val seat = seatItem.seat
                    seatList.add(seat)
                }

                // 나머지 좌석 구하기
                val allPremiumSeats = listOf("1A", "2A", "3A", "1B", "2B", "3B", "1C", "2C", "3C")
                val allStandardSeats =
                    listOf("1A", "2A", "3A", "4A", "1B", "2B", "3B", "4B", "1C", "2C", "3C", "4C")
                val remainingSeats =
                    if (carriage == 2) {
                        allPremiumSeats.filter { !seatList.contains(it) }
                    } else {
                        allStandardSeats.filter { !seatList.contains(it) }
                    }

                println("result : $result")
                println("data : $data")
                println("예약 가능한 좌석: $remainingSeats")

                // seatItems 리스트에 데이터 추가
//                seatItems.clear()
//                seatItems.addAll(data)

                // 예약을 위한 정보 전달
//                val selectedSeatItem = seatItems.find { it.trainNo == item.trainNo.toString() }
//                selectedSeatItem?.let {
                sendRequestReservationTrain(
                    scheduleItems[adapterPosition],
                    carriage,
                    seat = remainingSeats[0]
                )
//                }

//                seatSelectionListener.onSeatAndPersonSelected(selectedSeat, )
            }
        }

        fun handleRequestTrainSeatsError() {
            //TODO: 좌석 요청 실패 시 로직 처리
        }


        fun bind(requestTrainScheduleItem: RequestTrainScheduleItem, position: Int) {

            // 텍스트 적용
            radioBtnPremiumSeatSelect.text = premiumSeatText
            radioBtnStandardSeatSelect.text = standardSeatText

            // 매진 텍스트 적용
            if (requestTrainScheduleItem.vip) {
                radioBtnPremiumSeatSelect.text = "매진"
            }
            if (requestTrainScheduleItem.common) {
                radioBtnStandardSeatSelect.text = "매진"
            }

            // 드롭다운 메뉴 숨기기


            if (lastCheckedPosition != adapterPosition) {
                // 포지션 아이템 변경 및 초기화
                radioBtnPremiumSeatSelect.setBackgroundColor(Color.WHITE)
                radioBtnStandardSeatSelect.setBackgroundColor(Color.WHITE)
                radioBtnPremiumSeatSelect.isChecked = false
                radioBtnStandardSeatSelect.isChecked = false
                trainScheduleDropdownMenu.visibility = View.GONE
            } else {
                val isPremium = selectedRadio == 0

                seatGradeSelectRadioGroup.check(
                    if (isPremium) {
                        R.id.radioBtnPremiumSeatSelect
                    } else {
                        R.id.radioBtnStandardSeatSelect
                    }
                )

                radioBtnPremiumSeatSelect.apply {
                    setBackgroundColor(
                        if (isPremium) {
                            selectedColor
                        } else {
                            Color.WHITE
                        }
                    )
                }//.isChecked = isPremium
                radioBtnStandardSeatSelect.apply {
                    setBackgroundColor(
                        if (!isPremium) {
                            selectedColor
                        } else {
                            Color.WHITE
                        }
                    )
                }//.isChecked = !isPremium
                trainScheduleDropdownMenu.visibility = View.VISIBLE
            }

            if (lastCheckedPosition == adapterPosition) {
                println("같은 포지션 선택중")
            }

            radioBtnPremiumSeatSelect.setOnClickListener {
                println("특실 선택")
                notifyItemChanged(lastCheckedPosition)
                lastCheckedPosition = adapterPosition
                notifyItemChanged(lastCheckedPosition)
                selectedRadio = 0;
            }

            radioBtnStandardSeatSelect.setOnClickListener {
                println("일반실 선택")
                notifyItemChanged(lastCheckedPosition)
                lastCheckedPosition = adapterPosition
                notifyItemChanged(lastCheckedPosition)
                selectedRadio = 1;
            }


            // 시간 형식 변경
            val originalFormat = SimpleDateFormat("HHmmss", Locale.getDefault())
            val targetFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val hourMinFormat = SimpleDateFormat("HHmm", Locale.getDefault())
            val hourFormat = SimpleDateFormat("HH", Locale.getDefault())
            val minFormat = SimpleDateFormat("mm", Locale.getDefault())
            val departureTime = originalFormat.parse(requestTrainScheduleItem.depPlandTime)
            val arrivalTime = originalFormat.parse(requestTrainScheduleItem.arrPlandTime)
            val formattedDepartureTime = targetFormat.format(departureTime)
            val formattedArrivalTime = targetFormat.format(arrivalTime)

            val calendar = Calendar.getInstance()
            calendar.time = departureTime

            val departureHour = calendar.get(Calendar.HOUR_OF_DAY)
            val departureMin = calendar.get(Calendar.MINUTE)

            calendar.time = arrivalTime

            val arrivalHour = calendar.get(Calendar.HOUR_OF_DAY)
            val arrivalMin = calendar.get(Calendar.MINUTE)

            var hourDifference = arrivalHour - departureHour
            var minDifference = arrivalMin - departureMin

            // 도착 시간의 분(min)이 출발 시간의 분(min) 보다 작을 경우
            if (minDifference < 0) {
                hourDifference -= 1
                minDifference += 60
            }

            // 다음 날  도착일 경우
            if (hourDifference < 0) {
                hourDifference += 24
            }


            val timeDifference = String.format("%02d시간 %02d분 소요", hourDifference, minDifference)

            tvEstimatedTime.text = timeDifference

            tvTrainNo.text = requestTrainScheduleItem.trainNo.toString()
            tvDepartureStation.text = requestTrainScheduleItem.depPlaceName
            tvDepartureTime.text = formattedDepartureTime
            tvArrivalStation.text = requestTrainScheduleItem.arrPlaceName
            tvArrivalTime.text = formattedArrivalTime


            // 라디오 버튼에 따른 로직 구분
            // 매진인 경우 버튼 예약 버튼 비활성화
            seatGradeSelectRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                btnTrainScheduleReservation.isEnabled = true
                btnTrainScheduleReservation.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.primary
                    )
                )
                when (checkedId) {
                    R.id.radioBtnPremiumSeatSelect -> {
                        btnTrainScheduleReservation.text = "특실 예약하기"
                        if (requestTrainScheduleItem.vip == true) {
                            radioBtnPremiumSeatSelect.text = "매진"
                            btnTrainScheduleReservation.isEnabled = false
                            btnTrainScheduleReservation.setBackgroundColor(Color.GRAY)
                        }

                        val jsonData = """
                            {
                                "seats": ["1A", "2A", "3A", "1B", "2B", "3B", "1C", "2C", "3C"]
                            }
                        """.trimIndent()

                        btnTrainScheduleReservation.setOnClickListener {
                            sendRequestSeatsTrain(scheduleItems[adapterPosition], carriage = 2)
                        }


                    }

                    R.id.radioBtnStandardSeatSelect -> {
                        btnTrainScheduleReservation.text = "일반실 예약하기"
                        if (requestTrainScheduleItem.common == true) {
                            radioBtnStandardSeatSelect.text = "매진"
                            btnTrainScheduleReservation.isEnabled = false
                            btnTrainScheduleReservation.setBackgroundColor(Color.GRAY)
                        }

                        btnTrainScheduleReservation.setOnClickListener {
                            sendRequestSeatsTrain(scheduleItems[adapterPosition], carriage = 1)
                        }
                    }
                }
            }
        }
    }

//    interface SeatSelectionListener {
//        fun onSeatAndPersonSelected(seat: String, personCount: Int)
//    }
}