package ch.ergon.rs.dontdistract

import android.app.ActionBar
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import ch.ergon.rs.dontdistract.model.WorkingSession
import ch.ergon.rs.dontdistract.service.HandleSessionsService
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mService: HandleSessionsService
    private var mBound: Boolean = false


    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as HandleSessionsService.LocalBinder
            mService = binder.getService()
            mBound = true

            val savedSessions = mService.getSavedSessions()
            generateButtons(savedSessions)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

    fun generateButtons(sessions: List<WorkingSession>) {
        val sessionOverview = findViewById<LinearLayout>(R.id.sessionsOverview)
        val lp = ActionBar.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        sessionOverview.removeAllViews()

        for (i in sessions.indices) {
            val workingSession = sessions[i]
            val myButton = Button(this)
            myButton.text = workingSession.name
            myButton.setOnClickListener {
                val intent = Intent(this@MainActivity, WorkSessionActivity::class.java)


                intent.putExtra(LIST_INDEX, i)
                intent.putExtra(NAME, workingSession.name)
                intent.putExtra(END_DATE, workingSession.endDate)
                intent.putExtra(REMAINING_SECONDS, workingSession.remainingMilliSeconds)
                startActivity(intent)
            }
            sessionOverview.addView(myButton, lp)
        }
    }

    companion object {
        const val LIST_INDEX = "listIndex"
        const val NAME = "name"
        const val END_DATE = "endDate"
        const val REMAINING_SECONDS = "remainingSeconds"
    }

    fun addWorkingSession(view: View) {
        val intent = Intent(this@MainActivity, CreateSessionActivity::class.java)
        startActivity(intent)
    }
}