package com.example.m_board.ui.sections.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.m_board.util.CustomSharedValues.setSizeLimitation

object LoginScreen {
    @Composable
    fun Screen(modifier: Modifier) {
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
                        Button(
                            modifier = Modifier.setSizeLimitation(),
                            onClick = {
                                TODO()
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