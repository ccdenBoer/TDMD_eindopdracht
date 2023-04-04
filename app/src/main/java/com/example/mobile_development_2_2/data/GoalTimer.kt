package com.example.mobile_development_2_2.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay
import java.util.*

class GoalTimer {
    companion object{
        var started: MutableState<Boolean> = mutableStateOf(false)
        var secondsPassed: MutableState<Double> = mutableStateOf(0.0)

        val timerThread = Thread(Runnable {
            secondsPassed.value = 0.0
            val myTimer = Timer()
            myTimer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    secondsPassed.value += 0.1

                }
            }, 100, 100)

        })

        fun start(){
            secondsPassed.value = 0.0
            if(!started.value){
                timerThread.start()
                started.value = true
            }
        }

        fun stop(){

        }
    }
}