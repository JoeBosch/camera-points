package com.camerapoints;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(CameraPointsConfig.CONFIG_GROUP)
public interface CameraPointsConfig extends Config
{
	String CONFIG_GROUP = "camerapoints";

	@ConfigItem(
		keyName = "disableWhileTyping",
		name = "Disable switching while typing",
		description = "Disables the hotkeys while typing in the chat box."
	)
	default boolean disableWhileTyping()
	{
		return true;
	}
}
