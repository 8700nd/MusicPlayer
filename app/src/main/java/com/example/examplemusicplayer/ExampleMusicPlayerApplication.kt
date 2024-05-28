package com.example.examplemusicplayer

import android.app.Application
import com.example.examplemusicplayer.di.media.mediaModule
import com.example.examplemusicplayer.di.utils.utilsModule
import com.example.examplemusicplayer.di.viewmodel.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class ExampleMusicPlayerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@ExampleMusicPlayerApplication)
            modules(
                listOf(
                    viewModelModule,
                    mediaModule,
                    utilsModule
                )
            )
        }
    }
}