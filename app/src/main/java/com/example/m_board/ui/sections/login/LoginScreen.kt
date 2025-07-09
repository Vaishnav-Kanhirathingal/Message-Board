package com.example.m_board.ui.sections.login

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.m_board.util.CustomSharedValues.setSizeLimitation
import com.example.m_board.util.ScreenState
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.launch

object LoginScreen {
    @Composable
    fun Screen(
        modifier: Modifier,
        loginViewModel: LoginViewModel,
        toHomeScreen: () -> Unit
    ) {
        val screenState = loginViewModel.screenState.collectAsState()
        val context = LocalContext.current
        LaunchedEffect(
            key1 = screenState.value,
            block = {
                val ss = screenState.value
                when (ss) {
                    is ScreenState.PreCall, is ScreenState.Loading -> {}
                    is ScreenState.Loaded -> toHomeScreen()
                    is ScreenState.ApiError -> ss.manageToastActions(context = context)
                }
            }
        )

        Scaffold(
            modifier = modifier,
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = it),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        space = 4.dp,
                        alignment = Alignment.CenterVertically
                    ),
                    content = {
                        Text(text = "Google login screen")
                        val context = LocalContext.current
                        val scope = rememberCoroutineScope()
                        Button(
                            modifier = Modifier.setSizeLimitation(),
                            onClick = {
                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setServerClientId("426664497402-65qgep1ufh52va3aqt5ighd94tkireb9.apps.googleusercontent.com") // TODO: local.prop?
                                    .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts = false)
                                    .build()
                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()
                                scope.launch {
                                    try {
                                        val result = CredentialManager
                                            .create(context = context)
                                            .getCredential(
                                                request = request,
                                                context = context
                                            )
                                        loginViewModel.login(credential = result.credential)
                                    } catch (e: GetCredentialCancellationException) {
                                        e.printStackTrace()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        Toast.makeText(
                                            context, "Something went wrong", Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            content = {
                                Text(text = "Login")
                            }
                        )
                    }
                )
            }
        )
    }
}