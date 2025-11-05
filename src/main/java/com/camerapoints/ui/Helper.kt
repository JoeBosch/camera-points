package com.camerapoints.ui

import java.awt.event.MouseEvent
import javax.swing.JComponent

class Helper {
    companion object {
        fun checkClick(event: MouseEvent): Boolean {
            if (event.button != MouseEvent.BUTTON1)
                return false
            if (event.source !is JComponent)
                return false
            val point = event.point
            val size = (event.source as JComponent).size
            return point.x >= 0 && point.x <= size.width && point.y >= 0 && point.y <= size.height

        }
    }
}