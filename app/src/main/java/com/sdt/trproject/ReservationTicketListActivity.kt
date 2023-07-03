package com.sdt.trproject

import android.opengl.Visibility
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.sdt.trproject.adapters.TrainReservationTicketAdapter
import com.sdt.trproject.services.RequestTrainReservationListItem
import com.sdt.trproject.services.RequestTrainReservationListResponse
import com.sdt.trproject.services.TrainApiService
import com.sdt.trproject.utils.RetrofitModule
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReservationTicketListActivity: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var adapter: TrainReservationTicketAdapter

    @Inject
    lateinit var trainApiService: TrainApiService

    private lateinit var tvEmptyReservationTicketList: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_ticket_list)

        recyclerView = findViewById(R.id.reservationTicketRecyclerView)
        tvEmptyReservationTicketList = findViewById(R.id.tvEmptyReservationTicketList)


        adapter = TrainReservationTicketAdapter(this, trainApiService)
        recyclerView.adapter = adapter

        // 로그인한 유저의 승차권 목록 가져오기
        val call = trainApiService.requestTrainReservationList()

        RetrofitModule.executeCall(
            call,
            onFailure = { message, httpCode ->
                println("TrainReservationList 요청 실패 : Message = $message, HttpCode = $httpCode")
            },
            onSuccess = { response ->
                println("TrainReservationList 요청 성공 : Response = $response")
                handleRequestTrainReservationListResponse(response)
            }
        )
    }

    private fun handleRequestTrainReservationListResponse(response: RequestTrainReservationListResponse?) {
        val responseData: List<RequestTrainReservationListItem>? = response?.data

        if ( responseData != null) {
            tvEmptyReservationTicketList.visibility = View.GONE
            adapter.setData(responseData)
        } else {
            tvEmptyReservationTicketList.visibility = View.VISIBLE
        }

    }
}