package com.example.okdriverpanicbutton.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

enum class HistoryStatus {
    ACCEPTED,
    DECLINED,
    TIMED_OUT
}

data class HistoryRecord(
    val id: String,
    val userName: String,
    val timestamp: Long,
    val distanceKm: Double,
    val message: String,
    val status: HistoryStatus
)

class HistoryRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("sos_history", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_HISTORY = "history_json"
    }

    fun saveRecord(record: HistoryRecord) {
        val records = getHistory().toMutableList()
        records.add(0, record) // add to front
        saveHistoryList(records)
    }

    fun getHistory(): List<HistoryRecord> {
        val jsonString = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(jsonString)
            (0 until jsonArray.length()).map { i ->
                val obj = jsonArray.getJSONObject(i)
                HistoryRecord(
                    id = obj.getString("id"),
                    userName = obj.getString("userName"),
                    timestamp = obj.getLong("timestamp"),
                    distanceKm = obj.getDouble("distanceKm"),
                    message = obj.optString("message", "HELP ME!! IT'S AN EMERGENCY"),
                    status = HistoryStatus.valueOf(obj.getString("status"))
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveHistoryList(records: List<HistoryRecord>) {
        val jsonArray = JSONArray()
        records.forEach { record ->
            val obj = JSONObject().apply {
                put("id", record.id)
                put("userName", record.userName)
                put("timestamp", record.timestamp)
                put("distanceKm", record.distanceKm)
                put("message", record.message)
                put("status", record.status.name)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
    }
}
