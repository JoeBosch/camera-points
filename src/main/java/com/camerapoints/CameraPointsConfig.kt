package com.camerapoints

import net.runelite.client.config.Config
import net.runelite.client.config.ConfigGroup
import net.runelite.client.config.ConfigItem


@ConfigGroup(CameraPointsConfig.CONFIG_GROUP)
interface CameraPointsConfig : Config {

    @ConfigItem(
        keyName = "disableWhileTyping",
        name = "Disable switching while typing",
        description = "Disables the hotkeys while typing in the chat box."
    )
    fun disableWhileTyping(): Boolean {
        return true
    }

    companion object {
        const val CONFIG_GROUP = "camerapoints"
    }
}