package com.rcube.app

import android.app.Application
import com.rcube.app.di.AppContainer

class RcubeApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}
