package com.example.examplemusicplayer.ui


interface Event

sealed class MediaEvents: Event {
    data object EmptyLibrary : MediaEvents()
}