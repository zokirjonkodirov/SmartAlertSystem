package com.example.smartalertsystem

import android.content.Context
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

class MqttService {

    private lateinit var mqttClient: MqttAndroidClient

    fun connect(
        applicationContext: Context,
    ) {
        val clientId = MqttClient.generateClientId()

        mqttClient = MqttAndroidClient(
            applicationContext,
            "tcp://ec2-16-171-233-102.eu-north-1.compute.amazonaws.com:1883",
            clientId
        )

        val connOptions = MqttConnectOptions()

        try {
            mqttClient.connect(connOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    println("Message --> Connected Successfully into server")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    println("Message --> Connection error --> ${exception.message}")
                }
            })
        } catch (e: MqttException) {
            println("Message --> Unexpected connection error ${e.message}")
        }
    }

    fun setReceiveListener(result: (String, String) -> Unit) {
        println("Message --> Callback is being set")
        println("Message --> Is Connected ${mqttClient.isConnected}")

        mqttClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                println("Message --> Connection lost --> ${cause.message}")
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                val data = String(message.payload, charset("UTF-8"))
                result(topic, data)
                println("Message --> $topic --> $message")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                println("Message --> Delivery complete")
            }
        })
    }

    fun subscribe(topic: String, qos: Int) {
        try {
            mqttClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    println("Message --> Successfully subscribed")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    println("Message --> Failed subscribe --> ${exception.message}")
                }
            })
        } catch (e: MqttException) {
            println("Message --> Subscribe error ${e.message}")
        }
    }

    fun publish(topic: String, msg: String) {
        try {
            val mqttMessage = MqttMessage(msg.toByteArray(charset("UTF-8")))
            mqttMessage.isRetained = false
            mqttClient.publish(topic, mqttMessage)
        } catch (e: Exception) {
            // Check exception
            println("Message --> Publish error ${e.message}")
        }
    }

    fun unsubscribe(topic: String) {
        try {
            mqttClient.unsubscribe(topic, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // Successful unsubscribe
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Failed unsubscribe
                }
            })
        } catch (e: MqttException) {
            // Check exception
        }
    }

    fun disconnect() {
        try {
            mqttClient.disconnect(null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    println("Message --> Successful disconnection")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    println("Message --> Failed disconnection --> ${exception.message}")
                }
            })
        } catch (e: MqttException) {
            println("Message --> Disconnect error ${e.message}")
        }
    }
}
