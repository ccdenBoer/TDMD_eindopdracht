package com.example.mobile_development_2_2.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.mobile_development_2_2.gui.GoalPointManager
import kotlinx.coroutines.delay
import java.util.*

class GoalTimer {
    companion object{
        var started: MutableState<Boolean> = mutableStateOf(false)
        var secondsPassed: MutableState<Double> = mutableStateOf(0.0)
        var myTimer = Timer()

        var timerThread = Thread(Runnable {
            secondsPassed.value = 0.0
            myTimer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if(!GoalPointManager.getGoalPointManager(null).hasFinished()){
                        secondsPassed.value += 0.1
                    }


                }
            }, 100, 100)

        })

        fun start(){
            secondsPassed.value = 0.0
            if(!started.value){
                myTimer = Timer()
                timerThread = Thread(Runnable {
                    secondsPassed.value = 0.0
                    myTimer.scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            if(!GoalPointManager.getGoalPointManager(null).hasFinished()){
                                secondsPassed.value += 0.1
                            }


                        }
                    }, 100, 100)

                })
                timerThread.start()
                started.value = true
            }
        }
        fun getSecondPassed() : MutableState<Double> {
            return secondsPassed
        }
    }
}