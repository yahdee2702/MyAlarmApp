package com.idn.myalarmapp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.idn.myalarmapp.room.Alarm

class AlarmDiffUtil(private val oldList: List<Alarm>, private val newList : List<Alarm>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldData = oldList[oldItemPosition]
        val newData = newList[newItemPosition]

        return oldData.id == newData.id
                && oldData.time == newData.time
                && oldData.date == newData.date
                && oldData.note == newData.note
                && oldData.type == newData.type
    }

}