package xyz.teamgravity.timer_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class Timer {

    private companion object {
        const val DEFAULT_TIMER_DURATION = 10_000L
        const val DEFAULT_TIMER_CHECKPOINT = 3_000L
        const val DEFAULT_TIMER_INTERVAL = 200L
    }

    /**
     * Controls the duration of the timer, measured in milliseconds.
     *
     * Ensure this value is a positive integer. If not specified, it defaults to [DEFAULT_TIMER_DURATION].
     */
    var timerDuration: Long = DEFAULT_TIMER_DURATION

    /**
     * Defines a checkpoint in milliseconds. An [event] is emitted a single time when the timer reaches this value.
     *
     * It is crucial that this checkpoint occurs before the timer completes. Therefore,
     * `timerCheckpoint` must be a positive integer and satisfy the condition:
     * `timerCheckpoint < timerDuration`.
     *
     * Defaults to [DEFAULT_TIMER_CHECKPOINT].
     */
    var timerCheckpoint: Long = DEFAULT_TIMER_CHECKPOINT

    /**
     * The interval in milliseconds at which timer updates are emitted.
     *
     * This value determines how frequently the timer provides updates via [time], which can be used to refresh a UI displaying the
     * remaining time.
     *
     * Must be a positive integer between 50 and 1000 (inclusive).
     * Defaults to [DEFAULT_TIMER_INTERVAL].
     */
    var timerInterval: Long = DEFAULT_TIMER_INTERVAL

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var job: Job? = null

    private var sendCheckpoint = true

    private val _time = MutableStateFlow(0L)
    val time: StateFlow<Long> = _time.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _state = MutableStateFlow(TimerState.Idle)
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private val _event = Channel<TimerEvent>()
    val event: Flow<TimerEvent> = _event.receiveAsFlow()

    private fun startTimer() {
        sendCheckpoint = true
        job?.cancel()

        job = scope.launch {
            _time.emit(timerDuration)
            _duration.emit(timerDuration)
            _state.emit(TimerState.Ticking)
            _event.trySend(TimerEvent.Started)

            while (time.value > 0L) {
                ensureActive()
                delay(timerInterval)

                if (state.value == TimerState.Ticking) {
                    _time.emit(time.value - timerInterval)

                    if (time.value <= timerCheckpoint) {
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
        _event.trySend(TimerEvent.Finished)
    }

    private fun sendCheckpoint() {
        if (sendCheckpoint) {
            sendCheckpoint = false
            _event.trySend(TimerEvent.Checkpoint)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

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

    ///////////////////////////////////////////////////////////////////////////
    // MISC
    ///////////////////////////////////////////////////////////////////////////

    enum class TimerState {
        Idle, // timer ready to start
        Ticking, // timer is ticking
        Paused, // timer in paused state
        Finished, // timer finished
        Stopped; // timer stopped programmatically
    }

    enum class TimerEvent {
        Started,
        Checkpoint,
        Finished;
    }
}