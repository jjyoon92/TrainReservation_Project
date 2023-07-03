package com.sdt.trproject.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.annotations.SerializedName
import com.sdt.trproject.R
import com.sdt.trproject.services.RequestTrainReservationListItem
import com.sdt.trproject.services.TrainApiService
import dagger.hilt.android.qualifiers.ActivityContext
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class TrainReservationTicketAdapter @Inject constructor(
    @ActivityContext private val context: Context,
    private var trainApiService: TrainApiService

) : RecyclerView.Adapter<TrainReservationTicketAdapter.ViewHolder>(){

    private val reservationTicketItems: MutableList<RequestTrainReservationListItem> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<RequestTrainReservationListItem>) {
        reservationTicketItems.clear()
        reservationTicketItems.addAll(data)
        println("ReservationList data : $data")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrainReservationTicketAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_reservation_ticket, parent, false
        )
        return ViewHolder(itemView)    }

    override fun onBindViewHolder(holder: TrainReservationTicketAdapter.ViewHolder, position: Int) {
        val item = reservationTicketItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return reservationTicketItems.size
    }

    inner class ViewHolder(
        itemView: View,
    ): RecyclerView.ViewHolder(itemView) {
        private val reservationCancelItemLl: LinearLayout = itemView.findViewById(R.id.reservationCancelItemLl)
        private val tvReservationDepartDate: TextView = itemView.findViewById(R.id.tvReservationDepartDate)
        private val tvReservationSeatTicketCount: TextView = itemView.findViewById(R.id.tvReservationSeatTicketCount)
        private val tvReservationTrainNo: TextView = itemView.findViewById(R.id.tvReservationTrainNo)
        private val tvReservationRouteInfo: TextView = itemView.findViewById(R.id.tvReservationRouteInfo)
        private val tvReservationExpiredDate: TextView = itemView.findViewById(R.id.tvReservationExpiredDate)
        private val btnReservationConfirm: Button = itemView.findViewById(R.id.btnReservationConfirm)
        @SuppressLint("SetTextI18n")
        fun bind(item: RequestTrainReservationListItem) {
            val reservationId: String = item.reservationId
            val date: String = item.date
            val arriveTime: String = item.arriveTime
            val arriveStation: String = item.arriveStation
            val departTime: String = item.departTime
            val departStation: String = item.departStation
            val trainNo: String = item.trainNo
            val ticketCnt: Int = item.ticketCnt
            val createdDate: String = item.createdDate
            val expiredDate: String = item.expiredDate
            val paymentId: String? = item.paymentId

            // 출발 일자 포맷 변경
            val originalDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val parsedOriginalDate = LocalDate.parse(date, originalDateFormat)
            val targetDateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
            val formattedDate = parsedOriginalDate.format(targetDateFormat)
            val dayOfWeek = when (parsedOriginalDate.dayOfWeek.toString()) {
                "SUNDAY" -> "일"
                "MONDAY" -> "월"
                "TUESDAY" -> "화"
                "WEDNESDAY" -> "수"
                "THURSDAY" -> "목"
                "FRIDAY" -> "금"
                "SATURDAY" -> "토"
                else -> ""
            }

            // 시간 포맷 변경
            val originalTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val targetTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val parsedOriginalDepartTime = originalTimeFormat.parse(departTime)
            val parsedOriginalArriveTime = originalTimeFormat.parse(arriveTime)
            val formattedDepartTime = parsedOriginalDepartTime?.let { targetTimeFormat.format(it) }
            val formattedArriveTime = parsedOriginalArriveTime?.let { targetTimeFormat.format(it) }

            // 결제 기한 포맷 변경
            println("expired date : $expiredDate")
            val originalExpiredDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val targetExpiredDateFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.getDefault())
            val parsedOriginalExpiredDate = originalExpiredDateFormat.parse(expiredDate)
            val formattedExpiredDate = parsedOriginalExpiredDate?.let { targetExpiredDateFormat.format(it) }
            
            
            // TextView에 표시
            tvReservationDepartDate.text = "${item.date}($dayOfWeek)"
            tvReservationSeatTicketCount.text = "${ticketCnt}매"
            tvReservationTrainNo.text = "[SRT ${trainNo}]"
            tvReservationRouteInfo.text = "${departStation}(${formattedDepartTime}) -> ${arriveStation}(${formattedArriveTime})"

            println("paymentId 확인 : ${item.paymentId}")
            if(paymentId == null) {
                tvReservationExpiredDate.text = expiredDate
                btnReservationConfirm.text = "결제 대기 중"

                btnReservationConfirm.setOnClickListener {

                }
            } else {
                tvReservationExpiredDate.visibility = View.GONE
                btnReservationConfirm.text = "자세히 보기"
            }
        }
    }
}