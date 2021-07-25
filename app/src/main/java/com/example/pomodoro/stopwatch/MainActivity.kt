package com.example.pomodoro.stopwatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodoro.databinding.ActivityMainBinding
import com.example.pomodoro.foregroundservice.*

//import com.example.stopwatch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), StopwatchListener, LifecycleObserver {

    private lateinit var binding: ActivityMainBinding

    private val stopwatchAdapter = StopwatchAdapter(this)
    private var stopwatches = mutableListOf<Stopwatch>()
    private var nextId = 0

    private var startTime = 0L
    private var idd:Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startTime = System.currentTimeMillis()

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }

        binding.addNewStopwatchButton.setOnClickListener {

            stopwatches.add(Stopwatch((stopwatches.size-1)+1,
                binding.enterTimer.text.toString().toLong()*1000,
                binding.enterTimer.text.toString().toLong()*1000,
                false))

            stopwatchAdapter.submitList(stopwatches.toList())
        }
    }

    override fun start(id: Int) {
//        changeStopwatch(id, null, true)
        val newTimer = mutableListOf<Stopwatch>()
        stopwatches.forEach {
            if (it.id == id) {
                newTimer.add(Stopwatch(it.id, it.enterMs, it.currentMs, true))
            } else {
                newTimer.add(Stopwatch(it.id, it.enterMs, it.currentMs, false))
            }
        }
        stopwatchAdapter.submitList(newTimer)
        stopwatches.clear()
        stopwatches.addAll(newTimer)
        Log.i("TAG", "${stopwatches.joinToString("")}")

    }

    override fun stop(id: Int, enterMs: Long, currentMs: Long) {
        changeStopwatch(id, enterMs, currentMs)
    }

    override fun reset(id: Int, enterMs: Long, currentMs: Long ) {
        changeStopwatch(id, enterMs, enterMs)
    }

    override fun delete(id: Int) {
//        stopwatches.remove(stopwatches.find { it.id == id })
//        stopwatchAdapter.submitList(stopwatches.toList())
        val newTimers = mutableListOf<Stopwatch>()
        stopwatches.forEach {
            if (it.id < id) {
                newTimers.add(it)
            }
            if (it.id > id) {
                newTimers.add(Stopwatch(it.id - 1, it.enterMs, it.currentMs, it.isStarted))
            }
        }
        stopwatchAdapter.submitList(newTimers)
        stopwatches.clear()
        stopwatches.addAll(newTimers)
        Log.i("TAG", "${stopwatches.joinToString("")}")
    }

    override fun toast() {
      Toast.makeText(this,"Stop Timer. Push restart",Toast.LENGTH_SHORT).show()
    }

    override fun id(id: Int?){
        idd=id
        Log.i("TAG", "$id $idd")
    }

    private fun changeStopwatch(id: Int, enterMs: Long?, currentMs: Long?) {
        val newTimers = mutableListOf<Stopwatch>()
        stopwatches.forEach {
            if (it.id == id) {
                newTimers.add(Stopwatch(it.id, it.enterMs, currentMs ?: it.currentMs,false))
            } else {
                newTimers.add(it)
            }
        }
        stopwatchAdapter.submitList(newTimers)
        stopwatches.clear()
        stopwatches.addAll(newTimers)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {

        if(idd !=null && stopwatches[idd!!].isStarted) {
            Log.i("TAG", "onAppBackgrounded()")
            val startIntent = Intent(this, ForegroundService::class.java)
            startIntent.putExtra(COMMAND_ID, COMMAND_START)
            startIntent.putExtra(
                STARTED_TIMER_TIME_MS,
                stopwatches[idd!!].currentMs + System.currentTimeMillis()
            )
            startService(startIntent)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Log.i("TAG", "onAppForegrounded()")
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    private companion object {

        private const val INTERVAL = 10L
    }
}