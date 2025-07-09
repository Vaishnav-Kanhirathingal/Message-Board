package com.example.m_board.ui.sections.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.m_board.BuildConfig
import com.example.m_board.R
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
                        space = 8.dp,
                        alignment = Alignment.CenterVertically
                    ),
                    content = {
                        Text(
                            text = "Google login",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val context = LocalContext.current
                        val scope = rememberCoroutineScope()
                        Box(
                            modifier = Modifier
                                .setSizeLimitation()
                                .clip(shape = RoundedCornerShape(size = 16.dp))
                                .background(
                                    color =
                                        if (screenState.value.isLoading) ButtonDefaults.buttonColors().disabledContainerColor
                                        else ButtonDefaults.buttonColors().containerColor
                                )
                                .clickable(
                                    enabled = !screenState.value.isLoading,
                                    onClick = {
                                        val googleIdOption = GetGoogleIdOption.Builder()
                                            .setServerClientId(BuildConfig.SERVER_CLIENT_ID) // TODO: local.prop?
                                            .setFilterByAuthorizedAccounts(
                                                filterByAuthorizedAccounts = false
                                            )
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
                                                    context,
                                                    "Something went wrong",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                )
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                            contentAlignment = Alignment.Center,
                            content = {
                                if (screenState.value.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(size = 24.dp),
                                        color = ButtonDefaults.buttonColors().disabledContentColor
                                    )
                                }
                                Row(
                                    modifier = Modifier.alpha(alpha = if (screenState.value.isLoading) 0f else 1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(
                                        space = 8.dp,
                                        alignment = Alignment.Start
                                    ),
                                    content = {
                                        Icon(
                                            modifier = Modifier.size(size = 24.dp),
                                            painter = painterResource(id = R.drawable.icons8_google),
                                            contentDescription = null,
                                            tint = ButtonDefaults.buttonColors().contentColor,
                                        )
                                        Text(
                                            fontSize = 16.sp,
                                            color = ButtonDefaults.buttonColors().contentColor,
                                            fontWeight = FontWeight.Medium,
                                            text = "Login"
                                        )
                                    }
                                )
                            }
                        )
                    }
                )
            }
        )
    }
}