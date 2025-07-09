package com.example.m_board

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.m_board.ui.sections.home.HomeScreen
import com.example.m_board.ui.sections.loader.LoaderScreen
import com.example.m_board.ui.sections.login.LoginScreen
import com.example.m_board.ui.theme.MBoardTheme
import com.google.firebase.FirebaseApp
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContent { MBoardTheme { NavHost(modifier = Modifier.fillMaxSize()) } }
    }

    @Composable
    fun NavHost(modifier: Modifier) {
        val navController = rememberNavController()
        androidx.navigation.compose.NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = Destinations.Loader,
            builder = {
                val screenModifier = Modifier.fillMaxSize()
                composable<Destinations.Loader>(
                    content = {
                        LoaderScreen.Screen(
                            modifier = screenModifier,
                            toLoginScreen = {
                                navController.navigate(
                                    route = Destinations.Login,
                                    builder = {
                                        this.popUpTo<Destinations.Loader>(
                                            popUpToBuilder = {
                                                this.inclusive = true
                                            }
                                        )
                                    }
                                )
                            },
                            toHomeScreen = {
                                navController.navigate(
                                    route = Destinations.Home,
                                    builder = {
                                        this.popUpTo<Destinations.Loader>(
                                            popUpToBuilder = {
                                                this.inclusive = true
                                            }
                                        )
                                    }
                                )
                            }
                        )
                    }
                )
                composable<Destinations.Login>(
                    content = {
                        LoginScreen.Screen(
                            modifier = screenModifier,
                            loginViewModel = viewModel(viewModelStoreOwner = it),
                            toHomeScreen = {
                                navController.navigate(
                                    route = Destinations.Home,
                                    builder = {
                                        this.popUpTo(
                                            route = Destinations.Login,
                                            popUpToBuilder = { this.inclusive = true }
                                        )
                                    }
                                )
                            }
                        )
                    }
                )
                composable<Destinations.Home>(
                    content = {
                        HomeScreen.Screen(
                            modifier = screenModifier,
                            homeViewModel = viewModel(viewModelStoreOwner = it)
                        )
                    }
                )
            }
        )
    }
}

sealed class Destinations {
    @Serializable
    data object Login : Destinations()

    @Serializable
    data object Home : Destinations()

    @Serializable
    data object Loader : Destinations()
}