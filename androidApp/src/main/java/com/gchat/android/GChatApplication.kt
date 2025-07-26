package com.gchat.android

import android.app.Application
import com.gchat.shared.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@GChatApplication)
            modules(viewModelModule)
        }
    }
}