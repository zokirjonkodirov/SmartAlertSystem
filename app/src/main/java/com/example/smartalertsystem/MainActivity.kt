package com.example.smartalertsystem

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants.IterateForever
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.smartalertsystem.ui.theme.SmartAlertSystemTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartAlertSystemTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MqttScreen(innerPadding = innerPadding)
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MqttScreen(
    viewModel: MainViewModel = viewModel(),
    innerPadding: PaddingValues
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val mqttService = remember { MqttService() }

    val backgroundColor by animateColorAsState(
        targetValue = when (state.problem) {
            Problem.Gas, Problem.Fire, Problem.Flood -> Color(0xFFFFCDD2)
            Problem.No -> Color.White
        },
        label = "background",
    )

    LaunchedEffect(Unit) {
        viewModel.setupMqtt(context)
    }

    Scaffold(
        content = {
            Box(
                modifier = Modifier
                    .safeDrawingPadding()
                    .fillMaxSize()
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = state.problem,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                            animationSpec = tween(
                                300
                            )
                        )
                    }, label = "background"
                ) { currentState ->
                    if (currentState == Problem.No) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                        ) {
                            Text(
                                text = "Temperature:",
                                color = Color.DarkGray,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 28.sp,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            AnimatedContent(
                                targetState = state.temperature,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                                        animationSpec = tween(300)
                                    )
                                },
                                label = "temperature"
                            ) { temperature ->
                                if (temperature == "default") {
                                    CircularProgressIndicator(
                                        color = Color.Blue,
                                        modifier = Modifier.size(48.dp)
                                    )
                                } else {
                                    Text(
                                        text = "$temperature °",
                                        color = Color.Blue,
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontSize = 48.sp,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Gas Concentration:",
                                color = Color.DarkGray,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 28.sp,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            AnimatedContent(
                                targetState = state.gasPpm,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                                        animationSpec = tween(300)
                                    )
                                },
                                label = "gas"
                            ) { gas ->
                                if (gas == "default") {
                                    CircularProgressIndicator(
                                        color = Color.Blue,
                                        modifier = Modifier.size(48.dp)
                                    )
                                } else {
                                    Text(
                                        text = "$gas ppm",
                                        color = Color.Blue,
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontSize = 48.sp,
                                    )
                                }
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                        ) {
                            val lottieFile = remember {
                                when (state.problem) {
                                    Problem.Gas -> R.raw.gas
                                    Problem.Fire -> R.raw.fire
                                    Problem.Flood -> R.raw.flood
                                    Problem.No -> R.raw.fire
                                }
                            }
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.RawRes(
                                    lottieFile
                                )
                            )
                            val progress by animateLottieCompositionAsState(
                                composition = composition,
                                iterations = IterateForever
                            )

                            LottieAnimation(
                                composition = composition,
                                progress = { progress },
                                modifier = Modifier.size(200.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Alert!",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 28.sp,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.problem.text,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 38.sp,
                            )
                        }
                    }
                }

                Button(onClick = {viewModel.notifyBuzzer()}, modifier = Modifier.align(Alignment.BottomCenter)) {
                    Text(text = "Notify sensor")
                }
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            mqttService.disconnect()
        }
    }
}
