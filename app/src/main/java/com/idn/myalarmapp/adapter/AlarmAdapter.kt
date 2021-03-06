package com.idn.myalarmapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.idn.myalarmapp.AlarmReceiver
import com.idn.myalarmapp.R
import com.idn.myalarmapp.room.Alarm
import kotlinx.android.synthetic.main.item_row_reminder_alarm.view.*

class AlarmAdapter() : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {
    var alarms = emptyList<Alarm>()

    inner class AlarmViewHolder(val view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val rowItem = LayoutInflater.from(parent.context).inflate(R.layout.item_row_reminder_alarm,parent,false)
        return AlarmViewHolder(rowItem)
    }

    fun setData(list : List<Alarm>) {
        val alarmDiffUtil = AlarmDiffUtil(alarms,list)
        val alarmDiffUtilResult = DiffUtil.calculateDiff(alarmDiffUtil)
        this.alarms = list
        alarmDiffUtilResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        val alarmImage = when (alarm.type) {
            AlarmReceiver.TYPE_ONE_TIME -> R.drawable.ic_one_time
            AlarmReceiver.TYPE_REPEATING -> R.drawable.ic_repeating
            else -> R.drawable.ic_one_time
        }

        holder.view.item_time_alarm.text = alarm.time
        holder.view.item_date_alarm.text = alarm.date
        holder.view.item_note_alarm.text = alarm.note

        holder.view.item_img_one_time.loadImageDrawable(holder.view.context, alarmImage)
    }

    override fun getItemCount() : Int = alarms.size

    private fun ImageView.loadImageDrawable(context : Context, drawable : Int) {
        Glide.with(context).load(drawable).into(this)
    }
}