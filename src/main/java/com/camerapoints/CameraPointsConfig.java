package com.camerapoints;

import com.camerapoints.utility.Helper;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup(Helper.CONFIG_GROUP)
public interface CameraPointsConfig extends Config
{
    @ConfigItem(
            position = 0,
            keyName = "disableWhileTyping",
            name = "Disable hotkeys while typing",
            description = "When enabled, will not load any camera points while typing in a chatbox."
    )
    default boolean disableWhileTyping() {
        return true;
    }

    @ConfigItem(
            position = 1,
            keyName = "nextCameraPointKey",
            name = "Keybind to cycle forward",
            description = "When pressed, change to the next camera point in the list of camera points."
    )
    default Keybind nextCameraPointKey() { return Keybind.NOT_SET; }

    @ConfigItem(
            position = 2,
            keyName = "previousCameraPointKey",
            name = "Keybind to cycle backward",
            description = "When pressed, change to the previous camera point in the list of camera points."
    )
    default Keybind previousCameraPointKey() { return Keybind.NOT_SET; }
}
