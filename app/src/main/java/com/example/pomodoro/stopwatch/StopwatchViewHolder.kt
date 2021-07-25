package com.example.pomodoro.stopwatch

import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
//import com.example.customview.MainActivity
import com.example.pomodoro.R
import com.example.pomodoro.databinding.StopwatchItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener,
    private val resources: Resources,
    private var current: Long = 0L

) : RecyclerView.ViewHolder(binding.root) {

    private var timer: CountDownTimer? = null

    fun bind(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()





        if (stopwatch.isStarted) {
            startTimer(stopwatch)

            binding.custom.setPeriod(stopwatch.enterMs)
            current=stopwatch.enterMs-stopwatch.currentMs
            GlobalScope.launch {
                while (current < stopwatch.enterMs ) {

                        current += INTERVAL
                        binding.custom.setCurrent(current)
                    delay(INTERVAL)
                }
            }


        } else {
            stopTimer(stopwatch)


        }



        initButtonsListeners(stopwatch)
    }

    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.startPauseButton.setOnClickListener {
            if (stopwatch.isStarted) {
                listener.stop(stopwatch.id, stopwatch.enterMs,stopwatch.currentMs)
            } else {
                listener.start(stopwatch.id)
            }
        }

        binding.restartButton.setOnClickListener {
            listener.reset(stopwatch.id, stopwatch.enterMs,stopwatch.currentMs)
//            binding.startPauseButton.text = "STOP"
            stopwatch.isStarted=false

        }


            binding.deleteButton.setOnClickListener {

                if(stopwatch.id==0)
                {listener.id(null)}



                listener.delete(stopwatch.id, stopwatch.enterMs,stopwatch.currentMs)

            }
    }

    private fun startTimer(stopwatch: Stopwatch) {
        binding.startPauseButton.text = "STOP"
        listener.id(stopwatch.id)
        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()

        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer(stopwatch: Stopwatch) {
        binding.startPauseButton.text = "START"

        timer?.cancel()

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {
        return object : CountDownTimer(stopwatch.currentMs, UNIT_TEN_MS) {

            override fun onTick(millisUntilFinished: Long) {

                binding.stopwatchTimer.text = millisUntilFinished.displayTime()

                stopwatch.currentMs = millisUntilFinished


            }

            override fun onFinish() {
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
                listener.toast()
                stopTimer(stopwatch)


            }
        }
    }

    private fun Long.displayTime(): String {
        if (this <= 100L) {
            return START_TIME
        }
        val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60
        val ms = this % 1000 / 10

        return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}:${displaySlot(ms)}"
    }

    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0) {
            "$count"
        } else {
            "0$count"
        }
    }

    private companion object {

        private const val START_TIME = "00:00:00:00"
        private const val UNIT_TEN_MS = 10L
//        private const val PERIOD = 1000L * 60L * 60L * 24L // Day


        private var INTERVAL = 100L
        private const val PERIOD = 1000L * 30 // 30 sec
        private const val REPEAT = 1 // 10 times
    }
}