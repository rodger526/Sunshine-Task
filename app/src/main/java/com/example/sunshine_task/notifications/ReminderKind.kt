package com.example.sunshine_task.notifications

/** Momentos en los que se avisa al usuario, contados hacia atrás desde el vencimiento. */
enum class ReminderKind(val hoursBefore: Long) {
    H24(24),
    H12(12),
    H1(1),
    VENCIDA(0)
}