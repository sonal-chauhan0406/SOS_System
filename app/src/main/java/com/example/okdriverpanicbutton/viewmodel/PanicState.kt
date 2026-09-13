package com.example.okdriverpanicbutton.viewmodel

import com.example.okdriverpanicbutton.data.MockUser

/**
 * Sealed class representing all possible states in the panic flow.
 */
sealed class PanicState {

    /** Default resting state. */
    data class Idle(
        val noRespondersMessage: Boolean = false,
        val noRegisteredMembersError: Boolean = false
    ) : PanicState()

    /** Countdown before search begins. */
    data class Countdown(val secondsRemaining: Int, val totalSeconds: Int) : PanicState()

    /** Actively searching for the nearest online user. */
    data object Searching : PanicState()

    /** A request has been sent to a specific user. */
    data class RequestSent(
        val user: MockUser,
        val distance: Double
    ) : PanicState()

    /** The user accepted the help request. */
    data class Accepted(
        val user: MockUser,
        val eta: String,
        val location: String
    ) : PanicState()

    /** The user declined — transient state before auto-advancing. */
    data class Declined(val user: MockUser) : PanicState()
}

/** Types of haptic feedback triggered during state changes. */
enum class HapticType {
    HEAVY,
    MEDIUM,
    LIGHT,
    SUCCESS,
    ERROR
}
