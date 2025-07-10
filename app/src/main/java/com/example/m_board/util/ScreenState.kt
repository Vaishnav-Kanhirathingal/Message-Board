package com.example.m_board.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.TimeoutCancellationException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class ScreenState<T> {
    class PreCall<T> : ScreenState<T>()
    class Loading<T> : ScreenState<T>()
    class Loaded<T>(val result: T) : ScreenState<T>()

    sealed class ApiError<T>(
        val generalToastMessage: String
    ) : ScreenState<T>() {
        companion object {
            fun <T> fromException(e: Exception): ApiError<T> {
                return when (e) {
                    is SocketTimeoutException, is UnknownHostException, is TimeoutCancellationException -> NetworkError()
                    else -> SomethingWentWrong()
                }
            }
        }

        private val TAG = this::class.simpleName

        private var errorHasBeenDisplayed: Boolean = false

        fun manageToastActions(context: Context) {
            if (!this.errorHasBeenDisplayed) {
                this.errorHasBeenDisplayed = true
                Toast.makeText(
                    context,
                    this.generalToastMessage,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Log.d(TAG, "avoided displaying error multiple times")
            }
        }

        class NetworkError<T> : ApiError<T>(
            generalToastMessage = "No internet connection. Please check your network."
        )

        class SomethingWentWrong<T>(alternateToastMessage: String? = null) : ApiError<T>(
            generalToastMessage = alternateToastMessage ?: "Something went wrong. Please try again."
        )
    }

    val isLoading get() = (this is Loading)
}