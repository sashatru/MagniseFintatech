package com.example.eventapp.application

import android.app.Application
import androidx.lifecycle.LifecycleObserver

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import androidx.lifecycle.ProcessLifecycleOwner
import com.magnise.fintatech.BuildConfig
import com.magnise.fintatech.di.appModule


class FintatechApp : Application(), LifecycleObserver {
    override fun onCreate() {
        super.onCreate()
        initTimber()
        initKoin()
    }

    private fun initTimber() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initKoin(){
        startKoin {
            androidContext(this@FintatechApp)
            modules(appModule)
        }

    }

}