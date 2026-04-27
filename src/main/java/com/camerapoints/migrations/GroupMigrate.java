package com.camerapoints.migrations;

import com.camerapoints.CameraPointsConfig;
import com.camerapoints.models.CameraPoint;
import com.camerapoints.models.CameraPointGroup;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;

import java.util.Arrays;
import java.util.Collections;

public class GroupMigrate implements ConfigMigration
{
	private final Gson gson = new Gson();

	@Override
	public String fromVersion()
	{
		return null;
	}

	@Override
	public String toVersion()
	{
		return "3.0.1";
	}

	@Override
	public void apply(ConfigManager configManager)
	{
		String oldPointsJson = configManager.getConfiguration(CameraPointsConfig.CONFIG_GROUP, "points");
		if (oldPointsJson != null && !oldPointsJson.isEmpty())
		{
			JsonArray pointsArray = new JsonParser().parse(oldPointsJson).getAsJsonArray();
			for (int i = 0; i < pointsArray.size(); i++)
			{
				JsonElement pointElement = pointsArray.get(i);
				if (!pointElement.isJsonObject())
				{
					continue;
				}
				JsonObject pointObject = pointElement.getAsJsonObject();
				// Force IDs to 0..n-1 so legacy long values cannot overflow int fields.
				pointObject.addProperty("id", i);
			}

			CameraPoint[] oldPoints = gson.fromJson(pointsArray, CameraPoint[].class);
			if (oldPoints != null && oldPoints.length > 0)
			{
				for (CameraPoint oldPoint : oldPoints)
				{
					oldPoint.setEnabled(true);
				}

                CameraPointGroup recoveredGroup = new CameraPointGroup(
                        0,
                        "Old",
                        true,
                        Keybind.NOT_SET,
                        Keybind.NOT_SET,
                        Arrays.asList(oldPoints)
                        );
                configManager.setConfiguration(
                        CameraPointsConfig.CONFIG_GROUP,
                        "groups",
                        gson.toJson(Collections.singletonList(recoveredGroup))
                        );
            }
        }
        configManager.setConfiguration(CameraPointsConfig.CONFIG_GROUP, "version", toVersion());
    }
}
