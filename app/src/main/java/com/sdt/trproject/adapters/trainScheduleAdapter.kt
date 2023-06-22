package com.sdt.trproject.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.sdt.trproject.BuildConfig
import com.sdt.trproject.R
import com.sdt.trproject.ReservationDetailActivity
import com.sdt.trproject.services.RequestTrainReservationResponse
import com.sdt.trproject.services.RequestTrainSeatsItem
import com.sdt.trproject.services.RequestTrainSeatsResponse
import com.sdt.trproject.services.TrainApiService
import com.sdt.trproject.services.SearchTrainScheduleItem
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class trainScheduleAdapter(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val activity: Activity,
    private val departureStation: String?,
    private val arrivalStation: String?,
    private val departureDate: String?,
    private val departureTime: Int?,
    private val returnDate: String?,
    private val returnTime: Int?,
    private val adultCount: Int?,
    private val kidCount: Int?,
    private val oldCount: Int?,
) : RecyclerView.Adapter<trainScheduleAdapter.ViewHolder>() {
    private val scheduleItems: MutableList<SearchTrainScheduleItem> = mutableListOf()
    private val seatItems: MutableList<RequestTrainSeatsItem> = mutableListOf()
    private var lastCheckedPosition = -1
    private var selectedRadio = -1;
    private var currentPosition = -1
    private var remainingSeats: List<String>? = null



    companion object RetrofitBuilder {
        private var trainApiService: TrainApiService

        init {
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_ADDR)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            trainApiService = retrofit.create(TrainApiService::class.java)
        }

        const val TRAIN_NO = "TRAIN_NO"
        const val CARRIAGE = "CARRIAGE"
        const val SEAT = "SEAT"
        const val DATE = "DATE"
        const val DEPARTSTATION = "DEPARTSTATION"
        const val ARRIVESTATION = "ARRIVESTATION"
        const val DEPARTTIME = "DEPARTTIME"
        const val ARRIVETIME = "ARRIVETIME"
    }


    fun setData(data: List<SearchTrainScheduleItem>) {
        scheduleItems.clear()
        scheduleItems.addAll(data)
        notifyDataSetChanged() // 리스트의 크기와 아이템이 둘 다 변경되는 경우 ( 대체 가능? )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): trainScheduleAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.train_schedule_list_item, parent, false)
        return ViewHolder(view, context, departureStation, arrivalStation, departureDate, adultCount, kidCount, oldCount)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = scheduleItems[position]
        holder.bind(item, position)

    }

    override fun getItemCount(): Int {
        return scheduleItems.size
    }

    inner class ViewHolder(itemView: View, private val context: Context, departureStation: String?, arrivalStation: String?, departureDate: String?, adultCount: Int?, kidCount: Int?, oldCount: Int?) :
        RecyclerView.ViewHolder(itemView) {

        private val departureStation: String? = departureStation
        private val arrivalStation: String? = arrivalStation
        private val departureDate: String? = departureDate
        private val adultCount: Int? = adultCount
        private val kidCount: Int? = kidCount
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

        fun sendRequestReservationTrain(item: RequestTrainSeatsItem, seat: String) {
            val trainNo = item.trainNo
            val carriage = item.carriage
            val seat = seat
            val departureStation = departureStation
            val departureTime = item.departureTime.replace(":", "")
            val arrivalStation = arrivalStation
            val arrivalTime = item.arrivalTime.replace(":", "")
            val date = item.date.replace("-", "")

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

            val requestBody = jsonArray.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            val call = trainApiService.requestTrainReservation(requestBody)
            call.enqueue(object : Callback<RequestTrainReservationResponse> {
                override fun onResponse(
                    call: Call<RequestTrainReservationResponse>,
                    response: Response<RequestTrainReservationResponse>
                ) {
                    if (!response.isSuccessful) {
                        println("예약 실패")

                        return
                    }

                    val apiResponse = response.body()
                    handleRequestTrainReservationResponse(apiResponse, item)

                }

                override fun onFailure(call: Call<RequestTrainReservationResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })

        }

        fun handleRequestTrainReservationResponse(
            requestTrainReservationResponse: RequestTrainReservationResponse?,
            item: RequestTrainSeatsItem,
        ) {
            println("예약 성공!")

            val intent = Intent(context, ReservationDetailActivity::class.java).apply {
                putExtra("TRAIN_NO", item.trainNo)
                putExtra("CARRIAGE", if (radioBtnPremiumSeatSelect.isChecked) 1 else 2)
//                    putExtra("SEAT", selected)
                putExtra("DATE", item.date)
                putExtra("DEPARTSTATION", item.departureStation)
                putExtra("DEPARTTIME", item.departureTime)
                putExtra("ARRIVESTATION", item.arrivalStation)
                putExtra("ARRIVETIME", item.arrivalTime)
                putExtra("ADULTCOUNT", adultCount)
                putExtra("CHILDCOUNT", kidCount)
                putExtra("OLDCOUNT", oldCount)
            }

            context.startActivity(intent)
        }


        // 좌석 예약하기
        fun sendRequestSeatsTrain(item: SearchTrainScheduleItem, carriage: Int) {
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
            item: SearchTrainScheduleItem,
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
                    if ( carriage == 2 ) {
                        allPremiumSeats.filter { !seatList.contains(it) }
                    } else {
                        allStandardSeats.filter { !seatList.contains(it) }
                    }

                println("result : $result")
                println("data : $data")
                println("예약 가능한 좌석: $remainingSeats")

                // seatItems 리스트에 데이터 추가
                seatItems.clear()
                seatItems.addAll(data)

                // 예약을 위한 정보 전달
                val selectedSeatItem = seatItems.find { it.trainNo == item.trainNo.toString() }
                selectedSeatItem?.let {
                    sendRequestReservationTrain(it, seat = remainingSeats[0])
                }

//                seatSelectionListener.onSeatAndPersonSelected(selectedSeat, )
            }
        }

        fun handleRequestTrainSeatsError() {
            //TODO: 좌석 요청 실패 시 로직 처리
        }


        fun bind(searchTrainScheduleItem: SearchTrainScheduleItem, position: Int) {

            // 텍스트 적용
            radioBtnPremiumSeatSelect.text = premiumSeatText
            radioBtnStandardSeatSelect.text = standardSeatText

            // 매진 텍스트 적용
            if (searchTrainScheduleItem.vip) {
                radioBtnPremiumSeatSelect.text = "매진"
            }
            if (searchTrainScheduleItem.common) {
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
            val departureTime = originalFormat.parse(searchTrainScheduleItem.depPlandTime)
            val arrivalTime = originalFormat.parse(searchTrainScheduleItem.arrPlandTime)
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

            tvTrainNo.text = searchTrainScheduleItem.trainNo.toString()
            tvDepartureStation.text = searchTrainScheduleItem.depPlaceName
            tvDepartureTime.text = formattedDepartureTime
            tvArrivalStation.text = searchTrainScheduleItem.arrPlaceName
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
                        if (searchTrainScheduleItem.vip == true) {
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
                        if (searchTrainScheduleItem.common == true) {
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