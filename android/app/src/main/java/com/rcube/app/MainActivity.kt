package com.rcube.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.rcube.app.core.designsystem.theme.RcubeTheme
import com.rcube.app.di.LocalAppContainer
import com.rcube.app.navigation.RcubeApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as RcubeApplication).container

        setContent {
            RcubeTheme {
                CompositionLocalProvider(LocalAppContainer provides container) {
                    RcubeApp()
                }
            }
        }
    }
}
