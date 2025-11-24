package io.github.vinnih.app.utils

fun Long.formatTime(): String {
    if (this < 0) return "--:--"
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val remainingSeconds = totalSeconds % 60

    return String.format("%02d:%02d", minutes, remainingSeconds)
}
