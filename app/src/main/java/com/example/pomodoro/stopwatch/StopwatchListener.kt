package com.example.pomodoro.stopwatch

interface StopwatchListener {

    fun start(id: Int)

    fun stop(id: Int, enterMs: Long, currentMs: Long)

    fun reset(id: Int, enterMs: Long, currentMs: Long)

    fun delete(id: Int)

    fun toast()

    fun id(id: Int?)
}