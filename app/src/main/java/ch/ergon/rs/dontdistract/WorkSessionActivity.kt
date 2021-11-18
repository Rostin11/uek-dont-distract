package ch.ergon.rs.dontdistract

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import ch.ergon.rs.dontdistract.model.SessionDate
import java.time.LocalDate
import java.util.*

class WorkSessionActivity : AppCompatActivity() {
    lateinit var countDownText: TextView;
    lateinit var countDownButton: Button;

    lateinit var countDownTimer: CountDownTimer
    var milliSecondsLeft = 70000
    var timerRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_session)

        val listIndex = intent.extras?.get(MainActivity.LIST_INDEX).toString().toInt()
        val sessionName = intent.extras?.get(MainActivity.NAME).toString()
        val remainingSeconds = intent.extras?.get(MainActivity.REMAINING_SECONDS).toString().toInt()
        val endDate = intent.extras?.get(MainActivity.END_DATE) as SessionDate

        milliSecondsLeft = remainingSeconds * 1000

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
        countDownTimer = object : CountDownTimer((milliSecondsLeft).toLong(), 1000) {
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
        val minutes: Int = milliSecondsLeft / 60000
        val seconds: Int = (milliSecondsLeft % 60000) / 1000

        val timeLeftText: String

        if (seconds < 10) {
            timeLeftText = "$minutes:0$seconds"
        } else {
            timeLeftText = "$minutes:$seconds"
        }

        countDownText.setText(timeLeftText)

    }
}