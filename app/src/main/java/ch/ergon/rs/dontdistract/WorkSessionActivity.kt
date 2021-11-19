package ch.ergon.rs.dontdistract

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.hardware.*
import android.os.*
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ch.ergon.rs.dontdistract.model.SessionDate
import ch.ergon.rs.dontdistract.service.HandleSessionsService
import kotlin.math.acos
import kotlin.math.roundToInt
import kotlin.math.sqrt


class WorkSessionActivity : AppCompatActivity() {
    private lateinit var countDownText: TextView
    private lateinit var sessionNameField: TextView
    private lateinit var sessionDateField: TextView
    private lateinit var countDownButton: Button
    lateinit var deleteButton: Button
    var listIndex: Int = -1

    lateinit var countDownTimer: CountDownTimer
    var milliSecondsLeft = 0
    var timerRunning: Boolean = false

    private lateinit var mService: HandleSessionsService
    private var mBound: Boolean = false

    //Sensor
    var mSensorManager: SensorManager? = null
    var mSensorAccelerometer: Sensor? = null

    private val mSensorEventListener: SensorEventListener = object : SensorEventListener {


        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent) {
            val g: FloatArray = event.values.clone()

            val normOfG: Float = sqrt((g[0] * g[0] + g[1] * g[1] + g[2] * g[2]))

            g[0] = g[0] / normOfG
            g[1] = g[1] / normOfG
            g[2] = g[2] / normOfG

            val inclination = Math.toDegrees(
                acos(
                    g[2].toDouble()
                )
            ).roundToInt()

            if ((inclination < 25 || inclination > 155) && milliSecondsLeft != 0) {
                // device is flat
                showStartButton()
            } else {
                // device is not flat
                if (timerRunning) {
                    vibrate(700)
                    stopTimer()
                }
                hideStartButton()
            }

        }

    }


    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as HandleSessionsService.LocalBinder
            mService = binder.getService()
            mBound = true
            deleteButton.setOnClickListener {
                timerRunning = false
                mService.deleteSessionByListIndex(listIndex)
                val intent = Intent(this@WorkSessionActivity, MainActivity::class.java)
                startActivity(intent)
            }
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
        milliSecondsLeft = intent.extras?.get(MainActivity.REMAINING_SECONDS).toString().toInt()

        listIndex = intent.extras?.get(MainActivity.LIST_INDEX).toString().toInt()
        val sessionName = intent.extras?.get(MainActivity.NAME).toString()
        val endDate = intent.extras?.get(MainActivity.END_DATE) as SessionDate



        countDownText = findViewById(R.id.countDownTextView)
        sessionNameField = findViewById(R.id.sessionNameField)
        sessionDateField = findViewById(R.id.sessionDateField)
        countDownText = findViewById(R.id.countDownTextView)
        countDownButton = findViewById(R.id.countDownButton)
        deleteButton = findViewById(R.id.deleteButton)

        hideStartButton()


        sessionNameField.text = sessionName
        sessionDateField.text = "Endet am ${endDate.day}/${endDate.month}/${endDate.year}"

        updateTimer()

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensorAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    }

    fun startStop(view: View) {
        if (!timerRunning) {
            startTimer()
        } else {
            stopTimer()
        }
    }

    fun startTimer() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        countDownTimer = object : CountDownTimer((milliSecondsLeft).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                milliSecondsLeft = millisUntilFinished.toInt()
                updateTimer()
            }

            override fun onFinish() {
                milliSecondsLeft = 0
                stopTimer()
                updateTimer()
                hideStartButton()
                playAlarm()
            }
        }.start()
        countDownButton.text = "PAUSE"
        timerRunning = true
    }

    fun stopTimer() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        countDownTimer.cancel()
        countDownButton.text = "START"
        timerRunning = false
        saveTheTime()
    }

    fun updateTimer() {
        val millisecondsToShow: Int = milliSecondsLeft
        val hours: Int = millisecondsToShow / 3600000
        val minutes: Int = millisecondsToShow % 3600000 / 60000
        val seconds: Int = millisecondsToShow % 60000 / 1000

        val timeStringBuilder = StringBuilder()

        timeStringBuilder.append("$hours:")

        if (minutes < 10) {
            timeStringBuilder.append("0$minutes:")
        } else {
            timeStringBuilder.append("$minutes:")
        }
        if (seconds < 10) {
            timeStringBuilder.append("0$seconds")
        } else {
            timeStringBuilder.append("$seconds")
        }

        countDownText.text = timeStringBuilder.toString()

    }

    fun hideStartButton() {
        countDownButton.visibility = View.INVISIBLE
    }

    fun showStartButton() {
        countDownButton.visibility = View.VISIBLE
    }

    fun saveTheTime() {
        mService.changeRemainingMinutesByListIndex(listIndex, milliSecondsLeft)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager?.unregisterListener(mSensorEventListener)
    }

    fun playAlarm() {
        vibrate(5000)
    }

    private fun vibrate(length: Int) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(length.toLong(), 255))
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (timerRunning) {
            stopTimer()
        }
    }
}