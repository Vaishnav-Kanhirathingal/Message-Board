package com.example.m_board.ui.sections.login

import androidx.credentials.Credential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.m_board.util.ScreenState
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {
    private val _screenState: MutableStateFlow<ScreenState<Unit>> =
        MutableStateFlow(ScreenState.PreCall())
    val screenState: StateFlow<ScreenState<Unit>> get() = _screenState

    fun login(credential: Credential) {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading()
            try {

                val googleIdToken =
                    GoogleIdTokenCredential
                        .createFrom(data =credential.data)
                        .idToken



                FirebaseAuth.getInstance()
                    .signInWithCredential(
                        GoogleAuthProvider.getCredential(
                            googleIdToken,
                            null
                        )
                    )
                    .await()
                ScreenState.Loaded(result = Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                ScreenState.ApiError.fromException<Unit>(e = e)
            }.let { _screenState.value = it }
        }
    }
}