package io.github.artenes.ostatic.db

import android.content.Context

class PreferencesRepository(context: Context) {

    private val preferences = context.getSharedPreferences("OSTATIC_PREFERENCES", Context.MODE_PRIVATE)

    fun saveCurrentSession(sessionId: String, currentIndex: Int) {
        with(preferences.edit()) {
            putString("CURRENT_SESSION_ID", sessionId)
            putInt("CURRENT_SESSION_INDEX", currentIndex)
            apply()
        }
    }

    fun getLastSessionData(): Pair<String, Int> {
        val sessionId = preferences.getString("CURRENT_SESSION_ID", "")
        val currentIndex = preferences.getInt("CURRENT_SESSION_INDEX", 0)
        return sessionId to currentIndex
    }

}