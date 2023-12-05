data class MouseKeyboardState(
    val mouseClicks: Int = 0,
    val mouseMovements: Int = 0,
    val mouseWheelMovements: Int = 0,
    val keyEvents: Int = 0,
) {
    val keyboardRatio: Float
        get() {
            val sum = mouseClicks + keyEvents + mouseMovements + mouseWheelMovements
            return if (sum == 0) {
                0f
            } else {
                (keyEvents.toFloat() / sum) * 100f
            }
        }
}