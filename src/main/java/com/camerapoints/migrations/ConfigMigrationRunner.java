package com.camerapoints.migrations;

import com.camerapoints.CameraPointsConfig;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ConfigMigrationRunner
{
	private static final String MIGRATION_LIST_RESOURCE = "config-migrations.list";

	private final ConfigManager configManager;
	private final String targetVersion;

	public ConfigMigrationRunner(ConfigManager configManager, String targetVersion)
	{
		this.configManager = configManager;
		this.targetVersion = targetVersion;
	}

	public void runMigrations()
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
			migration.apply(configManager);
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
		List<String> classNames = readMigrationList();
		if (classNames.isEmpty())
		{
			return Collections.emptyList();
		}

		List<ConfigMigration> migrations = new ArrayList<>();
		for (String className : classNames)
		{
			ConfigMigration migration = instantiateMigration(className);
			if (migration != null)
			{
				migrations.add(migration);
			}
		}
		return migrations;
	}

	private List<String> readMigrationList()
	{
		InputStream stream = getClass().getClassLoader().getResourceAsStream(MIGRATION_LIST_RESOURCE);
		if (stream == null)
		{
			log.debug("No migration list resource found: {}", MIGRATION_LIST_RESOURCE);
			return Collections.emptyList();
		}

		List<String> classNames = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)))
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				String trimmed = line.trim();
				if (trimmed.isEmpty() || trimmed.startsWith("#"))
				{
					continue;
				}
				classNames.add(trimmed);
			}
		}
		catch (IOException e)
		{
			log.error("Failed to read migration list resource: {}", MIGRATION_LIST_RESOURCE, e);
		}
		return classNames;
	}

	private ConfigMigration instantiateMigration(String className)
	{
		try
		{
			Class<?> migrationClass = Class.forName(className);
			Object migrationInstance = migrationClass.getDeclaredConstructor().newInstance();
			if (!(migrationInstance instanceof ConfigMigration))
			{
				log.warn("Migration class {} does not implement ConfigMigration", className);
				return null;
			}
			return (ConfigMigration) migrationInstance;
		}
		catch (ReflectiveOperationException e)
		{
			log.error("Could not load migration class {}", className, e);
			return null;
		}
	}
}
