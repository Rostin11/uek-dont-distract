package ch.ergon.rs.dontdistract

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.hardware.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.view.View
import android.widget.Button
import android.widget.TextView
import ch.ergon.rs.dontdistract.model.SessionDate
import ch.ergon.rs.dontdistract.service.HandleSessionsService
import java.lang.Exception

class WorkSessionActivity : AppCompatActivity() {
    lateinit var countDownText: TextView;
    lateinit var sessionNameField: TextView;
    lateinit var sessionDateField: TextView;
    lateinit var countDownButton: Button;
    lateinit var deleteButton: Button;
    var listIndex: Int = -1

    lateinit var countDownTimer: CountDownTimer
    var milliSecondsLeft = 60000
    var timerRunning: Boolean = false

    private lateinit var mService: HandleSessionsService
    private var mBound: Boolean = false

    //Sensor
    var mSensorManager : SensorManager? = null
    var mSensorAccelerometer: Sensor? = null

    private val mSensorEventListener: SensorEventListener = object : SensorEventListener {


        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            println(event.values.toString())
        }

    }


    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as HandleSessionsService.LocalBinder
            mService = binder.getService()
            mBound = true
            deleteButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    timerRunning = false
                    mService.deleteSessionByListIndex(listIndex)
                    var intent = Intent(this@WorkSessionActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            })
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onResume() {
        super.onResume()
        mSensorManager?.registerListener(
            mSensorEventListener,
            mSensorAccelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onStart() {
        super.onStart()
        Intent(this, HandleSessionsService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_session)

        listIndex = intent.extras?.get(MainActivity.LIST_INDEX).toString().toInt()
        val sessionName = intent.extras?.get(MainActivity.NAME).toString()
        val remainingMilliSeconds =
            intent.extras?.get(MainActivity.REMAINING_SECONDS).toString().toInt()
        val endDate = intent.extras?.get(MainActivity.END_DATE) as SessionDate

        milliSecondsLeft = remainingMilliSeconds

        countDownText = findViewById(R.id.countDownTextView)
        sessionNameField = findViewById(R.id.sessionNameField)
        sessionDateField = findViewById(R.id.sessionDateField)
        countDownText = findViewById(R.id.countDownTextView)
        countDownButton = findViewById(R.id.countDownButton)
        deleteButton = findViewById(R.id.deleteButton)

        sessionNameField.text = sessionName
        sessionDateField.text = "Endet am ${endDate.day}/${endDate.month}/${endDate.year}"

        updateTimer()

        if (milliSecondsLeft == 0) hideStartButton()

         mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
         mSensorAccelerometer= mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

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
                milliSecondsLeft = 0
                stopTimer()
                updateTimer()
                hideStartButton()
            }
        }.start()
        countDownButton.setText("PAUSE")
        timerRunning = true
    }

    fun stopTimer() {
        countDownTimer.cancel()
        countDownButton.setText("START")
        timerRunning = false
        saveTheTime()
    }

    fun updateTimer() {
        val millisecondsToShow: Int

        if (milliSecondsLeft % 60000 == 0) {
            millisecondsToShow = milliSecondsLeft
        } else {
            millisecondsToShow = milliSecondsLeft + 60000
        }
        milliSecondsLeft
        val hours: Int = millisecondsToShow / 3600000
        val minutes: Int = (millisecondsToShow % 3600000) / 60000

        val timeLeftText: String

        if (minutes < 10) {
            timeLeftText = "$hours:0$minutes"
        } else {
            timeLeftText = "$hours:$minutes"
        }

        countDownText.setText(timeLeftText)

    }

    fun hideStartButton() {
        countDownButton.visibility = View.INVISIBLE
    }

    fun saveTheTime() {
        mService.changeRemainingMinutesByListIndex(listIndex, milliSecondsLeft)
    }

    override fun onPause() {
        super.onPause()
        if (timerRunning) {
            stopTimer()
        }
        mSensorManager?.unregisterListener(mSensorEventListener)
    }
}