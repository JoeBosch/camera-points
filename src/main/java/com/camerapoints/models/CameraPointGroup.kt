package com.camerapoints.models

import net.runelite.client.config.Keybind

data class CameraPointGroup(
    var id: Int,
    var name: String,
    var enabled: Boolean,
    var nextKeybind: Keybind,
    var previousKeybind: Keybind,
    var points: MutableList<CameraPoint>,
)
