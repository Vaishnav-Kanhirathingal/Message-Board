package com.example.m_board.ui.sections.loader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.m_board.util.CustomSharedValues
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

object LoaderScreen {
    @Composable
    fun Screen(
        modifier: Modifier,
        toHomeScreen: () -> Unit,
        toLoginScreen: () -> Unit
    ) {
        LaunchedEffect(
            key1 = Unit,
            block = {
//                delay(3000)
                if (Firebase.auth.currentUser != null) toHomeScreen()
                else toLoginScreen()
            }
        )
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
            content = {
                CircularProgressIndicator(
                    modifier = Modifier.size(size = CustomSharedValues.minimumTouchSize),
                    strokeWidth = 3.dp
                )
            }
        )
    }
}