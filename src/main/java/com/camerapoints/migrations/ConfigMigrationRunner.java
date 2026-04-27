package com.camerapoints.migrations;

import com.camerapoints.CameraPointsConfig;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ConfigMigrationRunner
{
	private final ConfigManager configManager;
	private final Gson gson;

	@Inject
	public ConfigMigrationRunner(ConfigManager configManager, Gson gson)
	{
		this.configManager = configManager;
		this.gson = gson;
	}

	public void runMigrations(String targetVersion)
	{
		String configVersion = configManager.getConfiguration(CameraPointsConfig.CONFIG_GROUP, "version");
		if (targetVersion.equals(configVersion))
		{
			return;
		}

		List<ConfigMigration> migrations = loadMigrations();
		if (migrations.isEmpty())
		{
			log.warn("No config migrations configured for {} -> {}", configVersion, targetVersion);
			return;
		}

		String runningVersion = configVersion;
		boolean appliedAny = false;
		for (ConfigMigration migration : migrations)
		{
			if (!Objects.equals(migration.fromVersion(), runningVersion))
			{
				continue;
			}
			if (!applyMigration(migration))
			{
				return;
			}
			runningVersion = configManager.getConfiguration(CameraPointsConfig.CONFIG_GROUP, "version");
			appliedAny = true;
		}

		if (!targetVersion.equals(runningVersion))
		{
			if (!appliedAny)
			{
				log.warn("No config migration path found from {} to {}", configVersion, targetVersion);
			}
			else
			{
				log.warn("Incomplete config migration path from {} to {}. Last migrated version: {}", configVersion, targetVersion, runningVersion);
			}
			return;
		}

	}

	private boolean applyMigration(ConfigMigration migration)
	{
		log.info("Applying camera points config migration {} -> {}", migration.fromVersion(), migration.toVersion());
		try
		{
			migration.apply(configManager, gson);
			return true;
		}
		catch (RuntimeException ex)
		{
			log.error(
				"Failed camera points config migration {} -> {}. Config snapshot: version={}, groups={}",
				migration.fromVersion(),
				migration.toVersion(),
				configManager.getConfiguration(CameraPointsConfig.CONFIG_GROUP, "version"),
				configManager.getConfiguration(CameraPointsConfig.CONFIG_GROUP, "groups"),
				ex
			);
			return false;
		}
	}

	private List<ConfigMigration> loadMigrations()
	{
		return Collections.unmodifiableList(List.of(
			new GroupMigrate(),
			new GroupMigrateRecovery()
		));
	}
}
