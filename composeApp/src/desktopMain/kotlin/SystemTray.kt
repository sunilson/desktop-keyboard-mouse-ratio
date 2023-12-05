import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.TrayIcon
import java.awt.image.BufferedImage
import javax.swing.JLabel

@Composable
fun SystemTray(keyboardState: MouseKeyboardState, onResetAction: () -> Unit) {
    LaunchedEffect(Unit) {
        val popup = PopupMenu().apply {
            add(MenuItem("").apply { this.addActionListener { } })
            add(MenuItem("Mouse Clicks: ${keyboardState.mouseClicks}").apply { this.addActionListener { } })
            add(MenuItem("Mouse movements: ${keyboardState.mouseMovements}").apply { this.addActionListener { } })
            add(MenuItem("MouseWheel movements: ${keyboardState.mouseWheelMovements}").apply { this.addActionListener { } })
            add(MenuItem("Keyboard Presses: ${keyboardState.keyEvents}").apply { this.addActionListener { } })
            add(MenuItem("Reset").apply { this.addActionListener { onResetAction() } })
        }
        val tayIcon = TrayIcon(
            useResource(resourcePath = "keyboard.png") { loadImageBitmap(it).toAwtImage() },
            "Atime",
            popup
        )
        val tray = java.awt.SystemTray.getSystemTray()
        try {
            tray.add(tayIcon)
        } catch (error: Throwable) {
            println(error.stackTrace.toString())
        }
    }

    LaunchedEffect(keyboardState) {
        val string = "${keyboardState.keyboardRatio.toInt().toString().ifBlank { "0" }}%"
        val timeImage = BufferedImage(320, 160, BufferedImage.TYPE_INT_ARGB)
        timeImage.createGraphics().apply {
            val logoImage = useResource("keyboard.png") { loadImageBitmap(it).toAwtImage() }
            drawImage(logoImage, 0, 20, 100, 120, null)

            // Draw work duration text as image
            font = JLabel().font.deriveFont(100f)
            val fontMetrics = getFontMetrics(font)
            drawString(
                string.substring(string.indices),
                120,
                ((160 - fontMetrics.height) / 2) + fontMetrics.ascent
            )
            dispose()
        }

        val tray = java.awt.SystemTray.getSystemTray()
        tray.trayIcons.firstOrNull()?.run {
            image = timeImage
            popupMenu.getItem(0).label = "Keyboard/mouse ratio: $string"
            popupMenu.getItem(1).label = "Mouse Clicks: ${keyboardState.mouseClicks}"
            popupMenu.getItem(2).label = "Mouse Movements: ${keyboardState.mouseMovements}"
            popupMenu.getItem(3).label = "MouseWheel Movements: ${keyboardState.mouseWheelMovements}"
            popupMenu.getItem(4).label = "Keyboard Presses: ${keyboardState.keyEvents}"
        }
    }
}
