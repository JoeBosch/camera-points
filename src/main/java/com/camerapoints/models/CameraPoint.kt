package com.camerapoints.models

import net.runelite.client.config.Keybind

data class CameraPoint(
    var id: Int,
    var name: String,
    var pitch: Int,
    var yaw: Int,
    var zoom: Int,
    var keybind: Keybind,
    var enabled: Boolean,
)