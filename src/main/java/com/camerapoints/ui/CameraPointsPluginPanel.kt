package com.camerapoints.ui

import com.camerapoints.CameraPointsPlugin
import net.runelite.client.ui.ColorScheme
import net.runelite.client.ui.PluginPanel
import net.runelite.client.util.ImageUtil
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Cursor
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.EmptyBorder

class CameraPointsPluginPanel(val plugin: CameraPointsPlugin) : PluginPanel(false) {

    val ADD_ICON = ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin::class.java, "add_icon.png"))
    val ADD_HOVER_ICON = ImageIcon(ImageUtil.alphaOffset(ADD_ICON.image, -100))
    val ADD_PRESSED_ICON = ImageIcon(ImageUtil.alphaOffset(ADD_ICON.image, -50))

    val titleLabel: JLabel = JLabel("Camera Points")
    val addGroupLabel: JLabel = JLabel(ADD_ICON)
    private val groupsContainer: JPanel

    init {
        layout = BorderLayout()
        background = ColorScheme.DARK_GRAY_COLOR

        val contentPanel = JPanel(BorderLayout())
        contentPanel.border = EmptyBorder(6, 6, 6, 6)
        contentPanel.background = ColorScheme.DARK_GRAY_COLOR

        // Header Panel
        val headerPanel = JPanel(BorderLayout())
        headerPanel.border = EmptyBorder(1, 0, 10, 0)

        titleLabel.foreground = Color.WHITE
        headerPanel.add(titleLabel, BorderLayout.WEST)

        addGroupLabel.toolTipText = "Add a new camera point group"
        addGroupLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        addGroupLabel.addMouseListener(object: MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (!Helper.checkClick(e))
                    return
                addGroupLabel.icon = ADD_PRESSED_ICON
            }
            override fun mouseReleased(e: MouseEvent) {
                if (!Helper.checkClick(e))
                    return
                plugin.cameraPointGroupManager.addGroup()
                plugin.updateConfig()
                reload()
                addGroupLabel.icon = ADD_HOVER_ICON
            }
            override fun mouseEntered(e: MouseEvent) {
                addGroupLabel.icon = ADD_HOVER_ICON
            }
            override fun mouseExited(e: MouseEvent) {
                addGroupLabel.icon = ADD_ICON
            }
        })
        headerPanel.add(addGroupLabel, BorderLayout.EAST)

        contentPanel.add(headerPanel, BorderLayout.NORTH)

        // Groups Container with Scroll
        groupsContainer = JPanel()
        groupsContainer.layout = BoxLayout(groupsContainer, BoxLayout.Y_AXIS)
        groupsContainer.border = EmptyBorder(0, 0, 0, 3)

        val groupsWrapper = JPanel(BorderLayout())
        groupsWrapper.add(groupsContainer, BorderLayout.NORTH)

        val scrollPane = JScrollPane(groupsWrapper)
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        scrollPane.border = BorderFactory.createEmptyBorder()

        contentPanel.add(scrollPane, BorderLayout.CENTER)

        add(contentPanel, BorderLayout.CENTER)

        reload()
    }

    fun reload() {
        groupsContainer.removeAll()

        for (group in plugin.cameraPointGroupManager.getAllGroups()) {
            val groupPanel = GroupPanel(plugin, group, ::reload)
            groupsContainer.add(groupPanel)
            groupsContainer.add(Box.createVerticalStrut(10))
        }

        revalidate()
        repaint()
    }
}