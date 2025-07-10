package com.example.m_board.ui.sections.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.m_board.util.ScreenState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds

class HomeViewModel : ViewModel() {
    private val TAG = this::class.simpleName

    companion object {
        const val BOARD_PATH = "BOARD"
    }

    val message: MutableStateFlow<String> = MutableStateFlow("")

    private val _refreshCounter: MutableStateFlow<ScreenState<Int>> =
        MutableStateFlow(ScreenState.PreCall())
    val refreshCounter: StateFlow<ScreenState<Int>> get() = _refreshCounter

    private val _screenState: MutableStateFlow<ScreenState<Unit>> =
        MutableStateFlow(ScreenState.PreCall())
    val screenState: StateFlow<ScreenState<Unit>> get() = _screenState

    /** no caching added because firebase already has that built in */
    fun sendMessage(message: String) {
        _screenState.value = ScreenState.Loading()
        viewModelScope.launch {
            try {
                Log.d(TAG, "api call starting")

                val user = FirebaseAuth.getInstance().currentUser
                val msg = Message(
                    time = System.currentTimeMillis(),
                    message = message,
                    userName = user?.displayName,
                    userId = user?.uid
                )
                withTimeout(
                    timeout = 5.seconds,
                    block = {
                        Firebase.firestore
                            .collection(BOARD_PATH)
                            .add(msg.toMap())
                            .await()
                    }
                )
                this@HomeViewModel.message.value = ""
                ScreenState.Loaded(result = Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                ScreenState.ApiError.fromException(e = e)
            }.let { _screenState.value = it }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pager = refreshCounter.flatMapLatest {
        Pager(
            config = PagingConfig(
                pageSize = MessagePagingSource.PAGE_SIZE,
                maxSize = 200
            ),
            pagingSourceFactory = { MessagePagingSource() }
        ).flow
    }.cachedIn(scope = viewModelScope)

    init {
        addBoardListener()
    }

    fun addBoardListener() {
        Firebase.firestore
            .collection(BOARD_PATH)
            .orderBy(Message::time.name,Query.Direction.DESCENDING)
            .limit(MessagePagingSource.PAGE_SIZE.toLong())
            .addSnapshotListener { s, e ->
                if ((e == null) && (s != null)) {
                    _refreshCounter.value = ScreenState.Loaded(
                        (_refreshCounter.value as? ScreenState.Loaded)?.result?.plus(1) ?: 0
                    )
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        // TODO: clear listener
//        Firebase.database.getReference(BOARD_PATH).removeEventListener(valueEventListener)
    }
}

data class Message(
    val time: Long = 0,
    val message: String = "",
    val userName: String? = null,
    val userId: String? = null,
    val key: String? = null
) {
    fun toMap(): Map<String, Any?> { // TODO: remove?
        return mapOf(
            this::time.name to this.time,
            this::message.name to this.message,
            this::userName.name to this.userName,
            this::userId.name to this.userId
        )
    }
}

fun Long.toHourMinute(): String {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("hh:mm a"))
}