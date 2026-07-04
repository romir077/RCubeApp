package com.rcube.app.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.rcube.app.data.repository.RcubeRepository

/**
 * Manual dependency container (Google's recommended DI-without-a-framework approach).
 * A single [RcubeRepository] is shared app-wide so every screen sees the same state.
 */
class AppContainer {
    val repository: RcubeRepository = RcubeRepository()
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
