package com.idn.myalarmapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.idn.myalarmapp.databinding.ActivityMainBinding
import com.idn.myalarmapp.fragment.DatePickerFragment
import com.idn.myalarmapp.fragment.TimePickerFragment
import com.idn.myalarmapp.room.Alarm
import com.idn.myalarmapp.room.AlarmDB
import kotlinx.android.synthetic.main.activity_one_time_alarm.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class OneTimeAlarmActivity : AppCompatActivity(), DatePickerFragment.DialogDateListener, TimePickerFragment.DialogTimeListener, View.OnClickListener {
    private var binding: ActivityMainBinding? = null

    private lateinit var alarmReceiver: AlarmReceiver

    private val db by lazy {
        AlarmDB(context = this)
    }

    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_ONCE_TAG = "TimePickerOnce"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_one_time_alarm)

        btn_set_date_one_time.setOnClickListener(this)
        btn_set_time_one_time.setOnClickListener(this)
        btn_cancel_set_one_time_alarm.setOnClickListener(this)

        btn_add_set_one_time_alarm.setOnClickListener(this)

        alarmReceiver = AlarmReceiver()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_set_date_one_time -> {
                val datePickerFragment = DatePickerFragment()
                datePickerFragment.show(supportFragmentManager, DATE_PICKER_TAG)
            }
            R.id.btn_set_time_one_time -> {
                val timePickerFragment = TimePickerFragment()
                timePickerFragment.show(supportFragmentManager, TIME_PICKER_ONCE_TAG)
            }
            R.id.btn_cancel_set_one_time_alarm -> {
                onBackPressed()
            }
            R.id.btn_add_set_one_time_alarm -> {
                val onceDate = tv_once_date.text.toString()
                val onceTime = tv_once_time.text.toString()
                val onceMessage = et_note_one_time.text.toString()
                val requestCode = Random.nextInt(0, Int.MAX_VALUE)

                alarmReceiver.setOneTimeAlarm(this,AlarmReceiver.TYPE_ONE_TIME, onceDate, onceTime, onceMessage, requestCode)

                CoroutineScope(Dispatchers.IO).launch {
                    db.alarmDao().addAlarm(
                        Alarm(0, onceTime, onceDate, onceMessage, AlarmReceiver.TYPE_ONE_TIME,requestCode)
                    )
                    finish()
                }
            }
        }
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year,month,dayOfMonth)

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        tv_once_date.text = dateFormat.format(calendar.time)
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
        calendar.set(Calendar.MINUTE,minute)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        when (tag) {
            TIME_PICKER_ONCE_TAG -> {
                tv_once_time.text = timeFormat.format(calendar.time)
            }
            else -> {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}