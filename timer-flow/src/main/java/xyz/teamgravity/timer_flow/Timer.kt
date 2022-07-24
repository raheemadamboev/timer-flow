package xyz.teamgravity.timer_flow

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class Timer {

    companion object {
        private const val DEFAULT_TIMER_DURATION = 10_000L
        private const val DEFAULT_TIMER_CHECKPOINT = 3_000L
    }

    var timerDuration: Long = DEFAULT_TIMER_DURATION
    var timerCheckpoint: Long = DEFAULT_TIMER_CHECKPOINT

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var sendCheckpoint = true

    private val _time = MutableStateFlow(0L)
    val time: StateFlow<Long> = _time.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _state = MutableStateFlow<TimerState>(TimerState.Idle)
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private val _event = Channel<TimerEvent>()
    val event: Flow<TimerEvent> = _event.receiveAsFlow()

    fun start() {
        startTimer()
    }

    suspend fun pause() {
        _state.emit(TimerState.Paused)
    }

    suspend fun resume() {
        _state.emit(TimerState.Ticking)
    }

    suspend fun reset() {
        stop()
        _state.emit(TimerState.Idle)
    }

    suspend fun stop() {
        _time.emit(0L)
        _duration.emit(0L)
        _state.emit(TimerState.Stopped)

        job?.cancel()
        job = null
    }

    private fun startTimer() {
        sendCheckpoint = true
        job?.cancel()

        job = scope.launch {
            _time.emit(timerDuration)
            _duration.emit(timerDuration)
            _state.emit(TimerState.Ticking)
            _event.send(TimerEvent.Started)

            while (_time.value > 0L) {
                delay(200L)

                if (_state.value == TimerState.Ticking) {
                    _time.emit(_time.value - 200L)

                    if (_time.value <= timerCheckpoint) {
                        sendCheckpoint()
                    }
                }
            }

            finish()
        }
    }

    private suspend fun finish() {
        stop()
        _state.emit(TimerState.Finished)
        _event.send(TimerEvent.Finished)
    }

    private suspend fun sendCheckpoint() {
        if (sendCheckpoint) {
            sendCheckpoint = false
            _event.send(TimerEvent.Checkpoint)
        }
    }

    sealed class TimerState {
        object Idle : TimerState() // timer ready to start
        object Ticking : TimerState() // timer is ticking
        object Paused : TimerState() // timer in paused state
        object Finished : TimerState() // timer finished
        object Stopped : TimerState() // timer stopped programmatically
    }

    sealed class TimerEvent {
        object Started : TimerEvent()
        object Checkpoint : TimerEvent()
        object Finished : TimerEvent()
    }
}