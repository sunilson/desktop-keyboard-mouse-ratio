import androidx.compose.runtime.*
import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelEvent
import com.github.kwhat.jnativehook.mouse.NativeMouseWheelListener
import kotlinx.coroutines.*
import kotlin.math.abs

@Composable
fun MouseKeyboardTracker(
    onMouseClicked: () -> Unit,
    onMouseMoved: () -> Unit,
    onMouseWheelMoved: () -> Unit,
    onKeyClicked: (Int) -> Unit
) {
    LaunchedEffect(Unit) {
        val mouseListener = MouseListener { onMouseClicked() }
        val mouseWheelListener = MouseWheelListener(this) { onMouseWheelMoved() }
        val mouseMovementListener = MouseMovementListener(this) { onMouseMoved() }
        val keyBoardListener = KeyBoardListener { onKeyClicked(it) }

        try {
            GlobalScreen.registerNativeHook()
            GlobalScreen.addNativeMouseListener(mouseListener)
            GlobalScreen.addNativeMouseWheelListener(mouseWheelListener)
            GlobalScreen.addNativeMouseMotionListener(mouseMovementListener)
            GlobalScreen.addNativeKeyListener(keyBoardListener)
            awaitCancellation()
        } finally {
            GlobalScreen.unregisterNativeHook()
            GlobalScreen.removeNativeMouseListener(mouseListener)
            GlobalScreen.removeNativeMouseWheelListener(mouseWheelListener)
            GlobalScreen.removeNativeMouseMotionListener(mouseListener)
            GlobalScreen.removeNativeKeyListener(keyBoardListener)
        }
    }
}

private class KeyBoardListener(private val onInteraction: (Int) -> Unit) : NativeKeyListener {
    override fun nativeKeyPressed(nativeEvent: NativeKeyEvent) {
        onInteraction(nativeEvent.keyCode)
        super.nativeKeyPressed(nativeEvent)
    }
}

private class MouseWheelListener(
    private val coroutineScope: CoroutineScope,
    private val onInteraction: () -> Unit
) : NativeMouseWheelListener {

    private var debounceJob: Job? = null

    override fun nativeMouseWheelMoved(nativeEvent: NativeMouseWheelEvent?) {
        super.nativeMouseWheelMoved(nativeEvent)

        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            debounceJob = launch {
                delay(SCROLL_DEBOUNCE)
                onInteraction()
            }
        }
    }

    companion object {
        private const val SCROLL_DEBOUNCE = 500L
    }
}

private class MouseListener(private val onInteraction: () -> Unit) : NativeMouseInputListener {
    override fun nativeMouseClicked(nativeEvent: NativeMouseEvent?) {
        onInteraction()
        super.nativeMouseClicked(nativeEvent)
    }
}

private class MouseMovementListener(
    private val coroutineScope: CoroutineScope,
    private val onInteraction: () -> Unit,
) : NativeMouseInputListener {

    private var distance: Int = 0
    private var previousX: Int = 0
    private var debounceJob: Job? = null
    override fun nativeMouseMoved(nativeEvent: NativeMouseEvent?) {
        super.nativeMouseMoved(nativeEvent)

        distance += abs(previousX - (nativeEvent?.x ?: 0))
        previousX = nativeEvent?.x ?: 0

        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            debounceJob = launch {
                delay(MOVEMENT_DEBOUNCE)
                if (distance > MOVEMENT_THRESHOLD) {
                    onInteraction()
                }
                distance = 0
            }
        }
    }

    companion object {
        private const val MOVEMENT_THRESHOLD = 10
        private const val MOVEMENT_DEBOUNCE = 500L
    }
}
