package com.camerapoints

import com.camerapoints.models.CameraPoint
import com.camerapoints.models.CameraPointGroup
import com.camerapoints.ui.CameraPointsPluginPanel
import com.google.gson.Gson
import com.google.inject.Provides
import lombok.extern.slf4j.Slf4j
import net.runelite.api.Client
import net.runelite.api.ScriptID
import net.runelite.api.gameval.VarClientID
import net.runelite.client.callback.ClientThread
import net.runelite.client.config.ConfigManager
import net.runelite.client.input.KeyManager
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.ui.ClientToolbar
import net.runelite.client.ui.NavigationButton
import net.runelite.client.util.ImageUtil
import java.awt.KeyboardFocusManager
import java.awt.event.KeyEvent
import javax.inject.Inject

const val CURRENT_VERISON = "3.0.0"
const val SCRIPTID_CAM_FORCE_ANGLE = 143

@Slf4j
@PluginDescriptor(
    name = "Camera Points"
)
class CameraPointsPlugin: Plugin() {

    @Inject
    private lateinit var gson: Gson

    @Inject
    private lateinit var client: Client
    @Inject
    private lateinit var clientThread: ClientThread
    @Inject
    private lateinit var clientToolbar: ClientToolbar

    @Inject
    private lateinit var cameraPointsConfig: CameraPointsConfig
    @Inject
    private lateinit var configManager: ConfigManager

    @Inject
    lateinit var cameraPointGroupManager: CameraPointGroupManager

    @Inject
    private lateinit var keyManager: KeyManager
    @Inject
    private lateinit var keyHandler: KeyHandler

    private var pluginPanel: CameraPointsPluginPanel? = null
    private lateinit var navigationButton: NavigationButton

    override fun startUp() {
        keyManager.registerKeyListener(keyHandler)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyHandler.keyEventDispatcher)
        loadConfig()
        pluginPanel = CameraPointsPluginPanel(this)

        navigationButton = NavigationButton.builder()
            .tooltip("Camera Points")
            .icon(ImageUtil.loadImageResource(javaClass, "panel_icon.png"))
            .priority(5)
            .panel(pluginPanel)
            .build()
        clientToolbar.addNavigation(navigationButton)
    }

    override fun shutDown() {
        keyManager.unregisterKeyListener(keyHandler)
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyHandler.keyEventDispatcher)
        clientToolbar.removeNavigation(navigationButton)
        pluginPanel = null
    }

    fun handleCameraPointChange(keyEvent: KeyEvent?): Boolean {
        for (cameraPointGroup in cameraPointGroupManager.getEnabledGroups()) {
            if (cameraPointGroup.nextKeybind.matches(keyEvent)) {
                val nextCamera = cameraPointGroupManager.cycleNext(cameraPointGroup)
                if (nextCamera != null) {
                    setCamera(nextCamera)
                    return true
                }
            }
            if (cameraPointGroup.previousKeybind.matches(keyEvent)) {
                val prevCamera = cameraPointGroupManager.cyclePrevious(cameraPointGroup)
                if (prevCamera != null) {
                    setCamera(prevCamera)
                    return true
                }
            }
            for (cameraPoint in cameraPointGroupManager.getEnabledPointsForGroup(cameraPointGroup)) {
                if (cameraPoint.keybind.matches(keyEvent)) {
                    setCamera(cameraPoint)
                    cameraPointGroupManager.updateCycleIndexToPoint(cameraPointGroup, cameraPoint)
                    return true
                }
            }
        }
        return false
    }

    fun setCamera(cameraPoint: CameraPoint) {
        clientThread.invoke(Runnable {
            client.runScript(SCRIPTID_CAM_FORCE_ANGLE, cameraPoint.pitch, cameraPoint.yaw)
            client.runScript(ScriptID.CAMERA_DO_ZOOM, cameraPoint.zoom, cameraPoint.zoom)
        })
    }

    fun saveCameraPosition(cameraPoint: CameraPoint) {
        cameraPoint.pitch = client.cameraPitch
        cameraPoint.yaw = client.cameraYaw
        cameraPoint.zoom = client.getVarcIntValue(VarClientID.CAMERA_ZOOM_BIG)
    }

    fun loadConfig() {
        val version = configManager.getConfiguration(CameraPointsConfig.CONFIG_GROUP, "version")
        if (version != CURRENT_VERISON) {
            return
        }
        val groupsJson = configManager.getConfiguration(CameraPointsConfig.CONFIG_GROUP, "groups")
        val groups = gson.fromJson(groupsJson, Array<CameraPointGroup>::class.java).toList()
        cameraPointGroupManager.setGroups(groups)
    }

    fun updateConfig() {
        configManager.setConfiguration(CameraPointsConfig.CONFIG_GROUP, "version", CURRENT_VERISON)
        configManager.setConfiguration(CameraPointsConfig.CONFIG_GROUP, "groups", gson.toJson(cameraPointGroupManager.getAllGroups()))
    }


    @Provides
    fun getConfig(configManager: ConfigManager): CameraPointsConfig {
        return configManager.getConfig(CameraPointsConfig::class.java)
    }
}