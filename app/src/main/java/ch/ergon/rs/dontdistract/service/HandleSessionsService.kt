package ch.ergon.rs.dontdistract.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import ch.ergon.rs.dontdistract.model.WorkingSession
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.Exception

class HandleSessionsService : Service() {
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): HandleSessionsService = this@HandleSessionsService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun getSavedSessions(): List<WorkingSession> {
        val sharedPreferences = getSharedPreferences("dontDistract", Context.MODE_PRIVATE)

        val workingSessionsAsString = sharedPreferences.getString("WORKING_SESSIONS", "[]")

        if (workingSessionsAsString != null) {
            return Json.decodeFromString(workingSessionsAsString)
        } else {
            throw Exception("Working Sessions not found in shared Preferences.")
        }
    }

    fun saveNewSession(workingSession: WorkingSession) {
        val sharedPreferences = getSharedPreferences("dontDistract", Context.MODE_PRIVATE)

        val savedSessions = getSavedSessions().toMutableList()

        savedSessions.add(workingSession)

        val savedSessionAsString = Json.encodeToString(savedSessions)

        val editor = sharedPreferences.edit()
        editor.putString("WORKING_SESSIONS", savedSessionAsString)
        editor.apply()
    }

    fun deleteSessionByListIndex(listIndex: Int) {
        val sharedPreferences = getSharedPreferences("dontDistract", Context.MODE_PRIVATE)

        val savedSessions = getSavedSessions().toMutableList()

        savedSessions.removeAt(listIndex)

        val savedSessionAsString = Json.encodeToString(savedSessions)

        val editor = sharedPreferences.edit()
        editor.putString("WORKING_SESSIONS", savedSessionAsString)
        editor.apply()
    }
}
