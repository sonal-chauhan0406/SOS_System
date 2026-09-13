package com.example.okdriverpanicbutton.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class ContactRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("emergency_contacts", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CONTACTS = "contacts_json"
        internal const val KEY_POLICE_NUMBER = "police_number"
        internal const val KEY_AMBULANCE_NUMBER = "ambulance_number"
        internal const val KEY_FIRE_NUMBER = "fire_number"
        internal const val KEY_EMERGENCY_CALLS_ENABLED = "emergency_calls_enabled"
        internal const val KEY_TIMER_DURATION = "timer_duration"
        internal const val KEY_SOS_MESSAGE = "sos_message"
    }

    fun saveContact(contact: EmergencyContact) {
        val contacts = getContacts().toMutableList()
        contacts.add(contact)
        saveContactsList(contacts)
    }


    fun getContacts(): List<EmergencyContact> {
        val jsonString = prefs.getString(KEY_CONTACTS, null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(jsonString)
            (0 until jsonArray.length()).map { i ->
                val obj = jsonArray.getJSONObject(i)
                EmergencyContact(
                    name = obj.getString("name"),
                    phoneNumber = obj.getString("phoneNumber"),
                    isAvailable = obj.optBoolean("isAvailable", true)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }


    fun deleteContact(index: Int) {
        val contacts = getContacts().toMutableList()
        if (index in contacts.indices) {
            contacts.removeAt(index)
            saveContactsList(contacts)
        }
    }

    fun updateContactAvailability(index: Int, isAvailable: Boolean) {
        val contacts = getContacts().toMutableList()
        if (index in contacts.indices) {
            val contact = contacts[index]
            contacts[index] = contact.copy(isAvailable = isAvailable)
            saveContactsList(contacts)
        }
    }

    private fun saveContactsList(contacts: List<EmergencyContact>) {
        val jsonArray = JSONArray()
        contacts.forEach { contact ->
            val obj = JSONObject().apply {
                put("name", contact.name)
                put("phoneNumber", contact.phoneNumber)
                put("isAvailable", contact.isAvailable)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_CONTACTS, jsonArray.toString()).apply()
    }



    fun getEmergencyNumber(type: ServiceType): String? {
        return prefs.getString(type.prefKey, null)
    }

    fun saveEmergencyNumber(type: ServiceType, number: String) {
        prefs.edit().putString(type.prefKey, number).apply()
    }

    fun deleteEmergencyNumber(type: ServiceType) {
        prefs.edit().remove(type.prefKey).apply()
    }

    fun isEmergencyCallsEnabled(): Boolean {
        return prefs.getBoolean(KEY_EMERGENCY_CALLS_ENABLED, true)
    }

    fun setEmergencyCallsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_EMERGENCY_CALLS_ENABLED, enabled).apply()
    }

    fun getTimerDuration(): Int {
        return prefs.getInt(KEY_TIMER_DURATION, 5)
    }

    fun setTimerDuration(seconds: Int) {
        prefs.edit().putInt(KEY_TIMER_DURATION, seconds).apply()
    }

    fun getSosMessage(): String {
        return prefs.getString(KEY_SOS_MESSAGE, "HELP ME!! IT'S AN EMERGENCY") ?: "HELP ME!! IT'S AN EMERGENCY"
    }

    fun setSosMessage(message: String) {
        prefs.edit().putString(KEY_SOS_MESSAGE, message).apply()
    }
}

enum class ServiceType(val prefKey: String, val label: String) {
    POLICE(ContactRepository.KEY_POLICE_NUMBER, "Police"),
    AMBULANCE(ContactRepository.KEY_AMBULANCE_NUMBER, "Ambulance"),
    FIRE(ContactRepository.KEY_FIRE_NUMBER, "Fire")
}
