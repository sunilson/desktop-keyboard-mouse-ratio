import androidx.compose.runtime.*
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay

fun main() = application {
    val preferences = remember { LocalPreferences() }
    var state by remember {
        mutableStateOf(
            MouseKeyboardState(
                keyEvents = preferences.get("keyEvents", 0),
                mouseClicks = preferences.get("mouseClicks", 0),
                mouseMovements = preferences.get("mouseMovements", 0),
                mouseWheelMovements = preferences.get("mouseWheelMovements", 0)
            )
        )
    }

    LaunchedEffect(state) {
        preferences.put("keyEvents", state.keyEvents)
        preferences.put("mouseClicks", state.mouseClicks)
        preferences.put("mouseMovements", state.mouseMovements)
        preferences.put("mouseWheelMovements", state.mouseWheelMovements)
    }

    MouseKeyboardTracker(
        onKeyClicked = { state = state.copy(keyEvents = state.keyEvents + 1) },
        onMouseClicked = { state = state.copy(mouseClicks = state.mouseClicks + 1) },
        onMouseMoved = { state = state.copy(mouseMovements = state.mouseMovements + 1) },
        onMouseWheelMoved = { state = state.copy(mouseWheelMovements = state.mouseWheelMovements + 1) }
    )

    SystemTray(
        state,
        onResetAction = {
            preferences.clear()
            state = MouseKeyboardState()
        }
    )

    // Keep alive
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
        }
    }
}