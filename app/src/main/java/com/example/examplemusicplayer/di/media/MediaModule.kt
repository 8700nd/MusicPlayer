package com.example.examplemusicplayer.di.media

import com.example.examplemusicplayer.utils.MediaPlayerController
import org.koin.dsl.module

val mediaModule = module {
    single { MediaPlayerController() }
}
