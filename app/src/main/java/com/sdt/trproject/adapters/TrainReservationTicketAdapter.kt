package com.sdt.trproject.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sdt.trproject.R
import com.sdt.trproject.services.RequestTrainReservationListItem
import com.sdt.trproject.services.TrainApiService
import dagger.hilt.android.qualifiers.ActivityContext
import org.w3c.dom.Text
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
        notifyDataSetChanged()
    }

//    fun initData(
//        recyclerView: RecyclerView,
//        data: List<RequestTrainReservationListItem>
//    ) {
//        this.recyclerView = recyclerView
//        this.data = data
//    }

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
        fun bind(item: RequestTrainReservationListItem) {
            val tvReservationDepartDate: TextView = itemView.findViewById(R.id.tvReservationDepartDate)
            val tvReservationSeatTicketCount: TextView = itemView.findViewById(R.id.tvReservationSeatTicketCount)
            val tvReservationTrainNo: TextView = itemView.findViewById(R.id.tvReservationTrainNo)
            val tvReservationRouteInfo: TextView = itemView.findViewById(R.id.tvReservationRouteInfo)
            val tvReservationExpiredDate: TextView = itemView.findViewById(R.id.tvReservationExpiredDate)
            tvReservationDepartDate.text = item.date
        }
    }

}