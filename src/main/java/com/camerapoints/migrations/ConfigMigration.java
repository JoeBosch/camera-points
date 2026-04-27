package com.camerapoints.migrations;

import net.runelite.client.config.ConfigManager;

public interface ConfigMigration
{
	String fromVersion();

	String toVersion();

	void apply(ConfigManager configManager);
}
