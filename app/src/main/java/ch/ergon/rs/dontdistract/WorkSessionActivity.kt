package ch.ergon.rs.dontdistract

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import ch.ergon.rs.dontdistract.model.SessionDate

class WorkSessionActivity : AppCompatActivity() {
    lateinit var countDownText: TextView;
    lateinit var countDownButton: Button;

    lateinit var countDownTimer: CountDownTimer
    var milliSecondsLeft = 60000
    var timerRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_session)

        val listIndex = intent.extras?.get(MainActivity.LIST_INDEX).toString().toInt()
        val sessionName = intent.extras?.get(MainActivity.NAME).toString()
        val remainingMinutes = intent.extras?.get(MainActivity.REMAINING_SECONDS).toString().toInt()
        val endDate = intent.extras?.get(MainActivity.END_DATE) as SessionDate

        milliSecondsLeft = remainingMinutes * 60000

        countDownText = findViewById(R.id.countDownTextView)
        countDownButton = findViewById(R.id.countDownButton)

    }

    fun startStop(view: View) {
        if (!timerRunning) {
            startTimer()
        } else {
            stopTimer()
        }
    }

    fun startTimer() {
        countDownTimer = object : CountDownTimer((milliSecondsLeft).toLong(), 60000) {
            override fun onTick(millisUntilFinished: Long) {
                milliSecondsLeft = millisUntilFinished.toInt()
                updateTimer()
            }

            override fun onFinish() {
                println("finish")
                milliSecondsLeft = 70000
                stopTimer()
            }
        }.start()
        countDownButton.setText("PAUSE")
        timerRunning = true
    }

    fun stopTimer() {
        countDownTimer.cancel()
        countDownButton.setText("START")
        timerRunning = false
    }

    fun updateTimer() {
        val hours: Int = milliSecondsLeft / 3600000
        val minutes: Int = (milliSecondsLeft % 3600000) / 60000

        val timeLeftText: String

        if (minutes < 10) {
            timeLeftText = "$hours:0$minutes"
        } else {
            timeLeftText = "$hours:$minutes"
        }

        countDownText.setText(timeLeftText)

    }
}