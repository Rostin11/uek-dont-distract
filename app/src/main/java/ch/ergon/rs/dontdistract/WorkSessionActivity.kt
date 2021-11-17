package ch.ergon.rs.dontdistract

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView

class WorkSessionActivity : AppCompatActivity() {
    lateinit var countDownText: TextView;
    lateinit var countDownButton: Button;

    lateinit var countDownTimer: CountDownTimer
     var secondsLeft = 0
     var timerRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_session)

        countDownText = findViewById(R.id.countDownTextView)
        countDownButton = findViewById(R.id.countDownButton)

    }

    fun startStop(view: View) {
        if(timerRunning) {
            startTimer()
        } else {
            stopTimer()
        }
    }

    fun startTimer(){
        countDownTimer  = object : CountDownTimer((secondsLeft * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                secondsLeft = (millisUntilFinished / 1000).toInt()
                updateTimer()
            }

            override fun onFinish() {}
        }
        countDownButton.setText("PAUSE")
        timerRunning = true
    }

    fun stopTimer(){
        countDownTimer.cancel()
        countDownButton.setText("START")
        timerRunning = false
    }

    fun updateTimer(){
        val minutes: Int = secondsLeft / 60
        val seconds: Int = secondsLeft % 60

        val timeLeftText: String

        if (seconds<10){
            timeLeftText = "$minutes:0$seconds"
        } else {
            timeLeftText = "$minutes:$seconds"
        }

        countDownText.setText(timeLeftText)

    }
}