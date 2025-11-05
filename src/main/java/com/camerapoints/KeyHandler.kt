package com.camerapoints

import com.google.common.base.Strings
import net.runelite.api.Client
import net.runelite.api.gameval.InterfaceID
import net.runelite.api.gameval.VarClientID
import net.runelite.api.widgets.Widget
import net.runelite.client.input.KeyListener
import java.awt.KeyEventDispatcher
import java.awt.event.KeyEvent
import javax.inject.Inject

class KeyHandler: KeyListener {

    @Inject
    private lateinit var plugin: CameraPointsPlugin

    @Inject
    private lateinit var client: Client

    @Inject
    private lateinit var config: CameraPointsConfig

    private var typing: Boolean = false

    val keyEventDispatcher: KeyEventDispatcher = object: KeyEventDispatcher {
        override fun dispatchKeyEvent(e: KeyEvent?): Boolean {
            if (!chatboxFocused())
                return false

            if (typing && e?.keyCode == KeyEvent.VK_ESCAPE) {
                typing = false
                return false
            }

            return false
        }

    }

    override fun keyTyped(e: KeyEvent?) {}

    override fun keyPressed(e: KeyEvent?) {
        if ((!typing && !isDialogOpen()) || !config.disableWhileTyping()) {
            if (plugin.handleCameraPointChange(e))
                return
        }

        if (!chatboxFocused())
            return

        if (!typing) {
            when (e?.keyCode) {
                KeyEvent.VK_ENTER, KeyEvent.VK_SLASH, KeyEvent.VK_COLON -> typing = true
            }
            return
        }

        when (e?.keyCode) {
            KeyEvent.VK_ENTER -> typing = false
            KeyEvent.VK_BACK_SPACE -> {
                if (Strings.isNullOrEmpty(client.getVarcStrValue(VarClientID.CHATINPUT)))
                    typing = false
            }
        }
    }

    override fun keyReleased(e: KeyEvent?) {}

    fun chatboxFocused(): Boolean {
        val chatboxParent = client.getWidget(InterfaceID.Chatbox.UNIVERSE)
        if (chatboxParent == null || chatboxParent.onKeyListener == null)
            return false

        val worldMapSearch = client.getWidget(InterfaceID.Worldmap.MAPLIST_DISPLAY)
        if (worldMapSearch != null && client.getVarcIntValue(VarClientID.WORLDMAP_SEARCHING) == 1)
            return false

        val report = client.getWidget(InterfaceID.Reportabuse.UNIVERSE)
        if (report != null)
            return false

        return true
    }

    fun isDialogOpen(): Boolean {
        return isHidden(InterfaceID.Chatbox.MES_LAYER_HIDE) || isHidden(InterfaceID.Chatbox.CHATDISPLAY)
                || !isHidden(InterfaceID.BankpinKeypad.UNIVERSE)
    }

    private fun isHidden(component: Int): Boolean {
        val w = client.getWidget(component)
        return w == null || w.isSelfHidden
    }
}