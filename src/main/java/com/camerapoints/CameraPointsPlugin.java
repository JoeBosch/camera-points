package com.camerapoints;

import com.camerapoints.models.CameraPoint;
import com.camerapoints.models.CameraPointGroup;
import com.camerapoints.migrations.ConfigMigrationRunner;
import com.camerapoints.ui.CameraPointsPluginPanel;
import com.google.gson.Gson;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.gameval.VarClientID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "Camera Points"
)
public class CameraPointsPlugin extends Plugin
{
	public static final String CURRENT_VERSION = "3.0.1";
	public static final int SCRIPTID_CAM_FORCE_ANGLE = 143;
	public static final int SCRIPTID_COMPASS_ANGLE = 1050;

	@Inject
	private Gson gson;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private CameraPointsConfig cameraPointsConfig;

	@Inject
	private ConfigManager configManager;

	@Inject
	CameraPointGroupManager cameraPointGroupManager;

	@Inject
	private KeyManager keyManager;

	@Inject
	private KeyHandler keyHandler;

	private CameraPointsPluginPanel pluginPanel;
	private NavigationButton navigationButton;

	@Override
	protected void startUp()
	{
		keyManager.registerKeyListener(keyHandler);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyHandler.getKeyEventDispatcher());
		loadConfig();
		pluginPanel = new CameraPointsPluginPanel(this);

		navigationButton = NavigationButton.builder()
			.tooltip("Camera Points")
			.icon(ImageUtil.loadImageResource(getClass(), "panel_icon.png"))
			.priority(5)
			.panel(pluginPanel)
			.build();
		clientToolbar.addNavigation(navigationButton);
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(keyHandler);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyHandler.getKeyEventDispatcher());
		clientToolbar.removeNavigation(navigationButton);
		pluginPanel = null;
	}

	public boolean handleCameraPointChange(KeyEvent keyEvent)
	{
		for (CameraPointGroup cameraPointGroup : cameraPointGroupManager.getEnabledGroups())
		{
			if (cameraPointGroup.getNextKeybind().matches(keyEvent))
			{
				CameraPoint nextCamera = cameraPointGroupManager.cycleNext(cameraPointGroup);
				if (nextCamera != null)
				{
					setCamera(nextCamera);
					return true;
				}
			}
			if (cameraPointGroup.getPreviousKeybind().matches(keyEvent))
			{
				CameraPoint prevCamera = cameraPointGroupManager.cyclePrevious(cameraPointGroup);
				if (prevCamera != null)
				{
					setCamera(prevCamera);
					return true;
				}
			}
			for (CameraPoint cameraPoint : cameraPointGroupManager.getEnabledPointsForGroup(cameraPointGroup))
			{
				if (cameraPoint.getKeybind().matches(keyEvent))
				{
					setCamera(cameraPoint);
					cameraPointGroupManager.updateCycleIndexToPoint(cameraPointGroup, cameraPoint);
					return true;
				}
			}
		}
		return false;
	}

	public void setCamera(CameraPoint cameraPoint)
	{
		clientThread.invoke(() -> {
			var direction = cameraPoint.getDirection() == null ? CameraPoint.Direction.NONE : cameraPoint.getDirection();
			if (direction != CameraPoint.Direction.NONE)
			{
				client.runScript(SCRIPTID_COMPASS_ANGLE, direction.getScriptValue());
			}
			if (cameraPoint.isApplyZoom())
			{
				client.runScript(ScriptID.CAMERA_DO_ZOOM, cameraPoint.getZoom(), cameraPoint.getZoom());
			}
		});
	}

	public void saveCameraPosition(CameraPoint cameraPoint)
	{
		cameraPoint.setDirection(CameraPoint.Direction.fromCameraYaw(client.getCameraYaw()));
		cameraPoint.setZoom(getCurrentZoom());
		cameraPoint.setApplyZoom(true);
	}

	public int getCurrentZoom()
	{
		return client.getVarcIntValue(VarClientID.CAMERA_ZOOM_BIG);
	}

	public CameraPoint addPointToGroup(CameraPointGroup group)
	{
		CameraPoint point = cameraPointGroupManager.addPointToGroup(group);
		point.setZoom(getCurrentZoom());
		point.setApplyZoom(true);
		return point;
	}

	public void updateCameraPointZoom(CameraPoint cameraPoint)
	{
		cameraPoint.setZoom(getCurrentZoom());
		cameraPoint.setApplyZoom(true);
	}

	public void loadConfig()
	{
		new ConfigMigrationRunner(configManager, CURRENT_VERSION).runMigrations();

		String groupsJson = configManager.getConfiguration(CameraPointsConfig.CONFIG_GROUP, "groups");
		CameraPointGroup[] groups = gson.fromJson(groupsJson, CameraPointGroup[].class);
		List<CameraPointGroup> groupList = groups == null ? java.util.Collections.emptyList() : Arrays.asList(groups);
		cameraPointGroupManager.setGroups(groupList);
	}

	public void updateConfig()
	{
		configManager.setConfiguration(CameraPointsConfig.CONFIG_GROUP, "version", CURRENT_VERSION);
		configManager.setConfiguration(CameraPointsConfig.CONFIG_GROUP, "groups", gson.toJson(cameraPointGroupManager.getAllGroups()));
	}

	public CameraPointGroupManager getCameraPointGroupManager()
	{
		return cameraPointGroupManager;
	}

	@Provides
	CameraPointsConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CameraPointsConfig.class);
	}
}
