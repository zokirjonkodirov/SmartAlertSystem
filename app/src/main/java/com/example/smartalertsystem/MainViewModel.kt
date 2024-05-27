package com.example.smartalertsystem

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    private val mqttService = MqttService()

    fun setupMqtt(context: Context) {
        mqttService.connect(context)

        viewModelScope.launch {
            delay(5000)
            mqttService.setReceiveListener { topic, message ->
                when (topic) {
                    "home/temperature" -> _state.update {
                        it.copy(
                            temperature = message,
                            problem = Problem.No
                        )
                    }

                    "home/gasppm" -> _state.update {
                        it.copy(gasPpm = message)
                    }

                    "home/gas" -> _state.update { it.copy(problem = Problem.Gas) }
                    "home/flood" -> _state.update { it.copy(problem = Problem.Flood) }
                    "home/fire" -> _state.update { it.copy(problem = Problem.Fire) }
                }
            }
            mqttService.subscribe("home/temperature", 1)
            mqttService.subscribe("home/gasppm", 1)
            mqttService.subscribe("home/gas", 0)
            mqttService.subscribe("home/flood", 0)
            mqttService.subscribe("home/fire", 0)
        }
    }

    fun notifyBuzzer() {
        mqttService.publish("home/buzzer", "notify")
    }
}