package com.example.smartalertsystem

data class MainState(
    val temperature: String = "default",
    val gasPpm: String = "default",
    val problem: Problem = Problem.No
)

enum class Problem(val text: String) {
    Gas(text = "Gas leakage detected"),
    Fire(text = "Fire detected"),
    Flood(text = "Flood detected"),
    No(text = "")
}