package com.example.m_board.ui.sections.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_board.util.ScreenState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {
    private val TAG = this::class.simpleName

    companion object {
        const val BOARD_REFERENCE = "BOARD"
    }

    val message: MutableStateFlow<String> = MutableStateFlow("")

    private val _messageList: MutableStateFlow<List<Message>> = MutableStateFlow(listOf())
    val messageList: StateFlow<List<Message>> get() = _messageList

    private val _screenState: MutableStateFlow<ScreenState<Unit>> =
        MutableStateFlow(ScreenState.PreCall())
    val screenState: StateFlow<ScreenState<Unit>> get() = _screenState

    fun sendMessage(message: String) {
        _screenState.value = ScreenState.Loading()
        viewModelScope.launch {
            try {
                Log.d(TAG,"api call starting")
                Firebase
                    .database
                    .getReference(BOARD_REFERENCE)
                    .push()
                    .setValue(
                        Message(
                            time = System.currentTimeMillis(),
                            message = message,
                            userName = null
                        )
                    ).await()
                ScreenState.Loaded(result = Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                ScreenState.ApiError.fromException(e = e)
            }.let { _screenState.value = it }
        }
    }

    init {
        liveListener()
    }

    fun liveListener() {
        Firebase.database
            .getReference(BOARD_REFERENCE)
            .orderByChild(Message::time.name)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        _messageList.value = snapshot.children.mapNotNull { child ->
                            child.getValue(Message::class.java)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        error.toException().printStackTrace()
                    }
                }
            )
    }
}

data class Message(
    val time: Long,
    val message: String,
    val userName: String?
)

fun Long.toHourMinute(): String {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("hh:mm"))
}