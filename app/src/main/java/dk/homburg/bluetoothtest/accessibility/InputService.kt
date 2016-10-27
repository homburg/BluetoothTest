package dk.homburg.bluetoothtest.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import timber.log.Timber

class InputService : AccessibilityService() {
    override fun onServiceConnected() {
        doOnServiceConnected()
    }

    fun doOnServiceConnected() {
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        info.flags = AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
        info.packageNames = null
        info.notificationTimeout = 100
        // info.setCap = AccessibilityServiceInfo.CAPABILITY_CAN_REQUEST_FILTER_KEY_EVENTS

        serviceInfo = info
    }

    override fun onInterrupt() {
        Timber.d("Interrupt!")
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        Timber.d("Key event %s", event)
        return handleKeyEvent(event)
    }

    private val handledKeyCodes: Set<Int> = setOf(KeyEvent.KEYCODE_BUTTON_A, KeyEvent.KEYCODE_BUTTON_B, KeyEvent.KEYCODE_BUTTON_C, KeyEvent.KEYCODE_D, KeyEvent.KEYCODE_BUTTON_X, KeyEvent.KEYCODE_BUTTON_Y)

    private val gestureCallback = object : GestureResultCallback() {
        override fun onCancelled(gestureDescription: GestureDescription?) {
            Timber.d("Gesture cancelled!")
            super.onCancelled(gestureDescription)
        }

        override fun onCompleted(gestureDescription: GestureDescription?) {
            Timber.d("Gesture completed")
            super.onCompleted(gestureDescription)
        }
    }

    private fun handleKeyEvent(event: KeyEvent): Boolean {
        if (handledKeyCodes.contains(event.keyCode)) {
            if (event.action == KeyEvent.ACTION_DOWN) {
                dispatchTouch()
            }
            return true
        } else {
            return false
        }
    }

    private fun dispatchTouch() {
        val gesture = GestureDescription.Builder().run {
            val path = Path().apply {
                moveTo(300F, 300F)
            }
            val strokeDescription = GestureDescription.StrokeDescription(path, 0, 100)
            addStroke(strokeDescription)
            build()
        }
        dispatchGesture(gesture, gestureCallback, null)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Timber.d("Accessibility event %s", event)
    }
}