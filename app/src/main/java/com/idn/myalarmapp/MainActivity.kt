package com.idn.myalarmapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.idn.myalarmapp.adapter.AlarmAdapter
import com.idn.myalarmapp.databinding.ActivityMainBinding
import com.idn.myalarmapp.room.AlarmDB
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var alarmAdapter: AlarmAdapter

    private lateinit var alarmReceiver: AlarmReceiver
    val db by lazy {
        AlarmDB(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        alarmReceiver = AlarmReceiver()

        initTimeToday()
        iniDateToday()
        initAlarmType()

        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        db.alarmDao().getAlarm().observe(this@MainActivity) {
            alarmAdapter.setData(it)
            Log.d("MainActivity", "dbresponse: $it")
        }
    }

    private fun initRecyclerView() {
        alarmAdapter = AlarmAdapter()

        rv_reminder_alarm.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = alarmAdapter

            swipeToDelete(this)
        }
    }

    private fun initTimeToday() {
        val timeNow = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formattedTime = timeFormat.format(timeNow.time)

        binding.tvTimeToday.text = formattedTime
    }

    private fun iniDateToday() {
        val dateNow = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("E, dd MMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(dateNow.time)

        binding.tvDateToday.text = formattedDate
    }

    private fun initAlarmType() {
        binding.viewSetOneTimeAlarm.setOnClickListener {
            startActivity(Intent(this, OneTimeAlarmActivity::class.java))
        }
        binding.viewSetRepeatingAlarm.setOnClickListener {
            startActivity(Intent(this, RepeatingAlarmActivity::class.java))
        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = alarmAdapter.alarms[viewHolder.adapterPosition]
                val typeOfAlarm = deletedItem.type
                alarmReceiver.cancelAlarm(this@MainActivity, typeOfAlarm, deletedItem.requestCode)

                // Delete Item
                CoroutineScope(Dispatchers.IO).launch {
                    db.alarmDao().deleteAlarm(deletedItem)
                }

                alarmAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                Toast.makeText(applicationContext,"Successfully deleted Alarm", Toast.LENGTH_SHORT).show()
            }
        }).attachToRecyclerView(recyclerView)
    }
}
