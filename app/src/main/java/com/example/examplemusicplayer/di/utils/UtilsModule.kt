package com.example.examplemusicplayer.di.utils

import com.example.examplemusicplayer.utils.ScanMediaUtil
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val utilsModule = module {
    single { ScanMediaUtil(androidApplication().applicationContext) }
}