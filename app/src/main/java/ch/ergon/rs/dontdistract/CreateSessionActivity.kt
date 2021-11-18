package ch.ergon.rs.dontdistract

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.EditText
import android.widget.LinearLayout
import ch.ergon.rs.dontdistract.model.SessionDate
import ch.ergon.rs.dontdistract.model.WorkingSession
import ch.ergon.rs.dontdistract.service.HandleSessionsService

class CreateSessionActivity : AppCompatActivity() {
    private var currentSelectedDate: SessionDate? = null

    private lateinit var mService: HandleSessionsService
    private var mBound: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_session)
        var dateView = findViewById<CalendarView>(R.id.dateView)


        dateView.setOnDateChangeListener(OnDateChangeListener { view, year, month, dayOfMonth ->
            currentSelectedDate = SessionDate(year, month + 1, dayOfMonth)
        })
    }

    fun createSession(view: View) {

        var sessionNameView = findViewById<EditText>(R.id.sessionNameView)
        var stundenView = findViewById<EditText>(R.id.stundenView)
        var minutenView = findViewById<EditText>(R.id.minutenView)

        val sessionName = sessionNameView.text
        var minuten = stundenView.text.toString().toInt() * 60
        minuten += minutenView.text.toString().toInt()

        //if (minuten)


        TODO("Schauen, dass die Felder nicht leer sind.")
        //val sessionDate = SessionDate()
//
        //val workingSession = WorkingSession()
//
        //mService.saveNewSession()
    }

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as HandleSessionsService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, HandleSessionsService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }
}