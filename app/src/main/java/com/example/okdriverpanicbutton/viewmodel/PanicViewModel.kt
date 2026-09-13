package com.example.okdriverpanicbutton.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.okdriverpanicbutton.data.ContactRepository
import com.example.okdriverpanicbutton.data.HistoryRecord
import com.example.okdriverpanicbutton.data.HistoryRepository
import com.example.okdriverpanicbutton.data.HistoryStatus
import com.example.okdriverpanicbutton.data.MockUser
import com.example.okdriverpanicbutton.data.MockUserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.sqrt

class PanicViewModel(
    private val historyRepository: HistoryRepository,
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val repository = MockUserRepository()

    private val _state = MutableStateFlow<PanicState>(PanicState.Idle())
    val state: StateFlow<PanicState> = _state.asStateFlow()

    private val _hapticEvent = MutableSharedFlow<HapticType>(extraBufferCapacity = 4)
    val hapticEvent: SharedFlow<HapticType> = _hapticEvent.asSharedFlow()

    private val userX = 50.0
    private val userY = 50.0

    private var priorityQueue: MutableList<Pair<MockUser, Double>> = mutableListOf()

    private var currentQueueIndex = 0

    private var countdownJob: Job? = null
    private var timeoutJob: Job? = null

    fun onPanicPressed(durationSeconds: Int = 3) {
        if (_state.value !is PanicState.Idle) return
        
        val contacts = contactRepository.getContacts().filter { it.isAvailable }
        if (contacts.isEmpty()) {
            _hapticEvent.tryEmit(HapticType.ERROR)
            _state.value = PanicState.Idle(noRegisteredMembersError = true)
            return
        }

        _hapticEvent.tryEmit(HapticType.HEAVY)
        _state.value = PanicState.Countdown(durationSeconds, durationSeconds)

        countdownJob = viewModelScope.launch {
            for (sec in (durationSeconds - 1) downTo 0) {
                delay(1000L)
                if (sec > 0) {
                    _state.value = PanicState.Countdown(sec, durationSeconds)
                } else {
                    startSearching()
                }
            }
        }
    }

    fun onCancelCountdown() {
        countdownJob?.cancel()
        countdownJob = null
        _hapticEvent.tryEmit(HapticType.LIGHT)
        _state.value = PanicState.Idle()
    }

    fun onUserResponds(accepted: Boolean) {
        timeoutJob?.cancel()
        val currentState = _state.value
        if (currentState !is PanicState.RequestSent) return

        if (accepted) {
            _hapticEvent.tryEmit(HapticType.SUCCESS)
            val streets = listOf(
                "Near Sector 7, Block C",
                "MG Road, Junction 3",
                "Park Avenue, Gate 2",
                "Ring Road, Flyover Exit",
                "Civil Lines, Tower B",
                "Lake View Crossing",
                "Station Road, Platform 4 Side"
            )
            val etaMinutes = (2..12).random()
            _state.value = PanicState.Accepted(
                user = currentState.user,
                eta = "~$etaMinutes min",
                location = streets.random()
            )
            
            historyRepository.saveRecord(
                HistoryRecord(
                    id = UUID.randomUUID().toString(),
                    userName = currentState.user.name,
                    timestamp = System.currentTimeMillis(),
                    distanceKm = currentState.distance / 10.0,
                    message = contactRepository.getSosMessage(),
                    status = HistoryStatus.ACCEPTED
                )
            )
        } else {
            _hapticEvent.tryEmit(HapticType.ERROR)
            _state.value = PanicState.Declined(currentState.user)
            
            historyRepository.saveRecord(
                HistoryRecord(
                    id = UUID.randomUUID().toString(),
                    userName = currentState.user.name,
                    timestamp = System.currentTimeMillis(),
                    distanceKm = currentState.distance / 10.0,
                    message = contactRepository.getSosMessage(),
                    status = HistoryStatus.DECLINED
                )
            )

            viewModelScope.launch {
                delay(800L)
                advanceToNextUser()
            }
        }
    }

    fun onReset() {
        countdownJob?.cancel()
        countdownJob = null
        timeoutJob?.cancel()
        timeoutJob = null
        currentQueueIndex = 0
        _state.value = PanicState.Idle()
    }

    fun clearErrors() {
        _state.value = PanicState.Idle()
    }

    private fun startSearching() {
        _hapticEvent.tryEmit(HapticType.MEDIUM)
        _state.value = PanicState.Searching

        viewModelScope.launch {
            delay(1500L)

            val contacts = contactRepository.getContacts().filter { it.isAvailable }

            priorityQueue = contacts.mapIndexed { index, contact ->
                val user = MockUser(
                    id = index + 1,
                    name = contact.name,
                    x = userX + (index + 1) * 2.0,
                    y = userY + (index + 1) * 2.0,
                    isOnline = true,
                    avatarEmoji = "👤"
                )
                val dist = euclideanDistance(userX, userY, user.x, user.y)
                user to dist
            }.sortedBy { it.second }.toMutableList()

            currentQueueIndex = 0
            advanceToNextUser()
        }
    }

    private fun advanceToNextUser() {
        if (currentQueueIndex < priorityQueue.size) {
            val (user, distance) = priorityQueue[currentQueueIndex]
            currentQueueIndex++
            _hapticEvent.tryEmit(HapticType.MEDIUM)
            _state.value = PanicState.RequestSent(
                user = user,
                distance = distance
            )

            timeoutJob?.cancel()
            timeoutJob = viewModelScope.launch {
                delay(10000L)
                if (_state.value is PanicState.RequestSent) {
                    historyRepository.saveRecord(
                        HistoryRecord(
                            id = UUID.randomUUID().toString(),
                            userName = user.name,
                            timestamp = System.currentTimeMillis(),
                            distanceKm = distance / 10.0,
                            message = contactRepository.getSosMessage(),
                            status = HistoryStatus.TIMED_OUT
                        )
                    )
                    advanceToNextUser()
                }
            }
        } else {
            _hapticEvent.tryEmit(HapticType.ERROR)
            _state.value = PanicState.Idle(noRespondersMessage = true)
        }
    }

    private fun euclideanDistance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        val dx = x2 - x1
        val dy = y2 - y1
        return sqrt(dx * dx + dy * dy)
    }
}
