package com.example.pomodoro.stopwatch

data class Stopwatch(
    val id: Int,
    val enterMs: Long,
    var currentMs: Long,
    var isStarted: Boolean
)