package com.example.m_board.ui.sections.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_board.util.CustomSharedValues.setSizeLimitation
import com.example.m_board.util.ScreenState
import com.google.firebase.auth.FirebaseAuth

object HomeScreen {
    @Composable
    fun Screen(
        modifier: Modifier,
        homeViewModel: HomeViewModel
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 16.dp,
                        alignment = Alignment.Start
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        Box(
                            modifier = Modifier
                                .setSizeLimitation()
                                .clip(shape = CircleShape)
                                .background(color = MaterialTheme.colorScheme.primaryContainer)
                                .clickable(onClick = { TODO() }),
                            contentAlignment = Alignment.Center,
                            content = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = null
                                )
                            }
                        )
                        Text(
                            text = "Message Board",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            },
            content = { paddingValues ->
                val screenState = homeViewModel.screenState.collectAsState()
                val context = LocalContext.current
                LaunchedEffect(
                    key1 = screenState.value,
                    block = {
                        val ss = screenState.value
                        when (ss) {
                            is ScreenState.PreCall, is ScreenState.Loading, is ScreenState.Loaded -> {}
                            is ScreenState.ApiError -> ss.manageToastActions(context = context)
                        }
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues),
                    content = {
                        val list = homeViewModel.messageList.collectAsState().value
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        val listState = rememberLazyListState()
                        LaunchedEffect(
                            key1 = list.size,
                            block = { listState.animateScrollToItem(0) }
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(weight = 1f),
                            state = listState,
                            reverseLayout = true,
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
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = if (isUsersText) Alignment.CenterEnd else Alignment.CenterStart,
                                            content = {
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
                                                        if (!isUsersText) {
                                                            Text(
                                                                modifier = Modifier.align(alignment = Alignment.Start),
                                                                text = it.userName
                                                                    ?: "[Unnamed User]",
                                                                fontWeight = FontWeight.SemiBold,
                                                                fontSize = 12.sp,
                                                                lineHeight = 12.sp,
                                                                color = textColor
                                                            )
                                                        }
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
                            }
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .imePadding()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    shape = RoundedCornerShape(size = 32.dp)
                                )
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            content = {
                                val text = homeViewModel.message.collectAsState().value
                                TextField(
                                    modifier = Modifier
                                        .weight(weight = 1f)
                                        .setSizeLimitation()
                                        .padding(horizontal = 8.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        errorContainerColor = Color.Transparent,

                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        errorIndicatorColor = Color.Transparent
                                    ),
                                    placeholder = { Text("Type your message...") },
                                    value = text,
                                    onValueChange = { txt -> homeViewModel.message.value = txt },
//                                    enabled = !screenState.value.isLoading
                                )
                                IconButton(
                                    modifier = Modifier.setSizeLimitation(),
                                    enabled = !screenState.value.isLoading,
                                    onClick = {
                                        text.takeUnless { it.isBlank() }
                                            ?.let { homeViewModel.sendMessage(message = it) }
                                    },
                                    content = {
                                        if (screenState.value.isLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(size = 24.dp),
                                                strokeWidth = 2.dp,
                                                color = MaterialTheme.colorScheme.onSurface

                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.Send,
                                                contentDescription = null
                                            )
                                        }
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