package com.rcube.app.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.rcube.app.BuildConfig
import com.rcube.app.data.local.SessionStore
import com.rcube.app.data.remote.RcubeApi
import com.rcube.app.data.repository.RcubeRepository

/**
 * Manual dependency container (Google's recommended DI-without-a-framework approach).
 * A single [RcubeRepository] is shared app-wide so every screen sees the same state.
 *
 * If RCUBE_API_URL is set in local.properties the app talks to the real Apps Script
 * backend; otherwise it runs in offline demo mode with seeded data.
 */
class AppContainer(context: Context) {
    private val api: RcubeApi? =
        if (BuildConfig.RCUBE_API_URL.isNotBlank()) {
            RcubeApi(BuildConfig.RCUBE_API_URL, BuildConfig.RCUBE_API_KEY)
        } else {
            null
        }

    private val sessionStore = SessionStore(context.applicationContext)

    val repository: RcubeRepository = RcubeRepository(api, sessionStore)
}

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer not provided")
}

/** Concise ViewModel creation with the shared repository injected. */
@Composable
inline fun <reified VM : ViewModel> rcubeViewModel(
    crossinline create: (RcubeRepository) -> VM,
): VM {
    val repository = LocalAppContainer.current.repository
    return viewModel(factory = viewModelFactory { initializer { create(repository) } })
}
