package com.idn.myalarmapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.idn.myalarmapp.databinding.ActivityMainBinding
import com.idn.myalarmapp.fragment.TimePickerFragment
import com.idn.myalarmapp.room.Alarm
import com.idn.myalarmapp.room.AlarmDB
import kotlinx.android.synthetic.main.activity_repeating_alarm.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class RepeatingAlarmActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener, View.OnClickListener {
    private var binding : ActivityMainBinding? = null

    private lateinit var alarmReceiver: AlarmReceiver

    private val db by lazy {
        AlarmDB(context = this)
    }

    companion object {
        private const val TIME_PICKER_REPEAT_TAG = "TimePickerRepeat"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_repeating_alarm)

        btn_set_time_repeating.setOnClickListener(this)
        btn_cancel_set_repeating_alarm.setOnClickListener(this)

        btn_add_set_repeating_alarm.setOnClickListener(this)

        alarmReceiver = AlarmReceiver()
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.btn_set_time_repeating -> {
                val timePickerFragment = TimePickerFragment()
                timePickerFragment.show(supportFragmentManager, TIME_PICKER_REPEAT_TAG)
            }
            R.id.btn_cancel_set_repeating_alarm -> {
                onBackPressed()
            }
            R.id.btn_add_set_repeating_alarm -> {
                val repeatTime = tv_repeating_time.text.toString()
                val repeatMessage = et_note_repeating.text.toString()
                val requestCode = Random.nextInt(0, Int.MAX_VALUE)

                alarmReceiver.setRepeatingAlarm(this, AlarmReceiver.TYPE_REPEATING, repeatTime, repeatMessage,requestCode)

                CoroutineScope(Dispatchers.IO).launch {
                    db.alarmDao().addAlarm(
                        Alarm(0, repeatTime, "Repeating Alarm", repeatMessage, AlarmReceiver.TYPE_REPEATING,requestCode)
                    )
                    finish()
                }
            }
        }
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
        calendar.set(Calendar.MINUTE,minute)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        when (tag) {
            TIME_PICKER_REPEAT_TAG -> {
                tv_repeating_time.text = timeFormat.format(calendar.time)
            }
            else -> {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}