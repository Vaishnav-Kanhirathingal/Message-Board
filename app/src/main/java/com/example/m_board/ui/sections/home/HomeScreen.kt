package com.example.m_board.ui.sections.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import com.google.firebase.auth.FirebaseAuth

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
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(weight = 1f),
                            verticalArrangement = Arrangement.spacedBy(
                                space = 4.dp,
                                alignment = Alignment.Bottom
                            ),
                            content = {
                                items(
                                    items = list,
                                    itemContent = {
                                        val isUsersText =
                                            ((it.userId == userId) && (userId != null))
                                        Column(
                                            modifier = Modifier
                                                .widthIn(min = 250.dp)
                                                .padding(
                                                    start = if (isUsersText) 64.dp else 16.dp,
                                                    end = if (isUsersText) 16.dp else 64.dp
                                                )
                                                .background(
                                                    color =
                                                        if (isUsersText) MaterialTheme.colorScheme.primaryContainer
                                                        else MaterialTheme.colorScheme.surfaceContainer,
                                                    shape = RoundedCornerShape(size = 16.dp)
                                                )
                                                .padding(
                                                    start = 16.dp,
                                                    end = 16.dp,
                                                    top = 12.dp,
                                                    bottom = 8.dp
                                                ),
                                            content = {
                                                val textColor =
                                                    if (isUsersText) MaterialTheme.colorScheme.onPrimaryContainer
                                                    else MaterialTheme.colorScheme.onSurface

                                                Text(
                                                    modifier = Modifier.align(alignment = Alignment.Start),
                                                    text = it.userName ?: "[Unnamed User]",
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 12.sp,
                                                    lineHeight = 12.sp,
                                                    color = textColor
                                                )
                                                Text(
                                                    text = it.message,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 18.sp,
                                                    lineHeight = 18.sp,
                                                    color = textColor
                                                )
                                                Text(
                                                    modifier = Modifier.align(alignment = Alignment.End),
                                                    text = it.time.toHourMinute(),
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 12.sp,
                                                    lineHeight = 12.sp,
                                                    color = textColor
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