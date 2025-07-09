package com.example.m_board.ui.sections.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_board.util.CustomSharedValues.setSizeLimitation

object HomeScreen {
    @Composable
    fun Screen(
        modifier: Modifier,
        homeViewModel: HomeViewModel
    ) {
        Scaffold(
            modifier = modifier,
            content = { paddingValues ->
                val screenState = homeViewModel.screenState.collectAsState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues),
                    content = {
                        val list = homeViewModel.messageList.collectAsState().value
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(weight = 1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            content = {
                                items(
                                    items = list,
                                    itemContent = {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                                    shape = RoundedCornerShape(size = 16.dp)
                                                )
                                                .padding(
                                                    horizontal = 8.dp,
                                                    vertical = 4.dp
                                                ),
                                            content = {
                                                Text(
                                                    modifier = Modifier.align(alignment = Alignment.Start),
                                                    text = it.userName ?: "[Unnamed User]",
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = it.message,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 16.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = it.time.toHourMinute(),
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    shape = RoundedCornerShape(size = 32.dp)
                                )
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                            horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            content = {
                                val text = homeViewModel.message.collectAsState().value
                                BasicTextField(
                                    modifier = Modifier.weight(weight = 1f),
                                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                    value = text,
                                    onValueChange = { txt -> homeViewModel.message.value = txt },
                                    enabled = !screenState.value.isLoading
                                )
                                IconButton(
                                    modifier = Modifier.setSizeLimitation(),
                                    enabled = !screenState.value.isLoading,
                                    onClick = {
                                        text.takeUnless { it.isBlank() }
                                            ?.let { homeViewModel.sendMessage(message = it) }
                                    },
                                    content = {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = null
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