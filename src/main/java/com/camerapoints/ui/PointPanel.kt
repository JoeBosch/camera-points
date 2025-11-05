package com.camerapoints.ui

import com.camerapoints.CameraPointsPlugin
import com.camerapoints.models.CameraPoint
import com.camerapoints.models.CameraPointGroup
import net.runelite.client.config.Keybind
import net.runelite.client.ui.ColorScheme
import net.runelite.client.ui.FontManager
import net.runelite.client.ui.components.FlatTextField
import net.runelite.client.util.ImageUtil
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Cursor
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder
import javax.swing.border.MatteBorder

class PointPanel(
    private val plugin: CameraPointsPlugin,
    private val group: CameraPointGroup,
    private val cameraPoint: CameraPoint,
    private val reloadFunction: () -> Unit
) : JPanel() {

    val FROM_GAME_ICON = ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin::class.java, "from_game_icon.png"))
    val FROM_GAME_HOVER_ICON =
        ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin::class.java, "from_game_icon_blue.png"))
    val FROM_GAME_PRESSED_ICON = ImageIcon(ImageUtil.alphaOffset(FROM_GAME_HOVER_ICON.image, -50))

    val DELETE_ICON = ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin::class.java, "delete_icon.png"))
    val DELETE_HOVER_ICON =
        ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin::class.java, "delete_icon_red.png"))
    val DELETE_PRESSED_ICON = ImageIcon(ImageUtil.alphaOffset(DELETE_HOVER_ICON.image, -50))


    val enabledBox: JCheckBox = JCheckBox()
    val nameInput: FlatTextField = FlatTextField()
    val saveLabel = JLabel("Save")
    val cancelLabel = JLabel("Cancel")
    val renameLabel = JLabel("Rename")
    val keybindButton: JButton = JButton()
    val fromGameLabel: JLabel = JLabel(FROM_GAME_ICON)
    val deleteLabel: JLabel = JLabel(DELETE_ICON)

    init {
        layout = BorderLayout()
        border = BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        background = ColorScheme.DARKER_GRAY_COLOR

        // Row 1: Checkbox and Name
        val namePanel = JPanel(BorderLayout())
        namePanel.background = ColorScheme.DARKER_GRAY_COLOR
        namePanel.border = CompoundBorder(
            MatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
            LineBorder(ColorScheme.DARKER_GRAY_COLOR)
        )

        val nameActions = JPanel(BorderLayout(4, 0))
        nameActions.background = ColorScheme.DARKER_GRAY_COLOR
        nameActions.border = EmptyBorder(0, 4, 0, 8)

        saveLabel.isVisible = false
        saveLabel.font = FontManager.getRunescapeSmallFont()
        saveLabel.foreground = ColorScheme.PROGRESS_COMPLETE_COLOR
        saveLabel.addMouseListener(object: MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (!Helper.checkClick(e))
                    return
                saveLabel.foreground = ColorScheme.PROGRESS_COMPLETE_COLOR.brighter()
            }

            override fun mouseReleased(e: MouseEvent) {
                if (!Helper.checkClick(e))
                    return
                nameInput.setEditable(false)
                cameraPoint.name = nameInput.text
                plugin.updateConfig()
                saveLabel.isVisible = false
                cancelLabel.isVisible = false
                renameLabel.isVisible = true
                requestFocusInWindow()
                saveLabel.foreground = ColorScheme.PROGRESS_COMPLETE_COLOR.darker()
            }

            override fun mouseEntered(e: MouseEvent) {
                saveLabel.foreground = ColorScheme.PROGRESS_COMPLETE_COLOR.darker()
            }

            override fun mouseExited(e: MouseEvent) {
                saveLabel.foreground = ColorScheme.PROGRESS_COMPLETE_COLOR
            }
        })

        cancelLabel.isVisible = false
        cancelLabel.font = FontManager.getRunescapeSmallFont()
        cancelLabel.foreground = ColorScheme.PROGRESS_ERROR_COLOR
        cancelLabel.addMouseListener(object: MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (!Helper.checkClick(e))
                    return
                cancelLabel.foreground = ColorScheme.PROGRESS_ERROR_COLOR.brighter()
            }

            override fun mouseReleased(e: MouseEvent) {
                if (!Helper.checkClick(e))
                    return
                nameInput.setEditable(false)
                nameInput.text = cameraPoint.name
                saveLabel.isVisible = false
                cancelLabel.isVisible = false
                renameLabel.isVisible = true
                requestFocusInWindow()
                cancelLabel.foreground = ColorScheme.PROGRESS_ERROR_COLOR.darker()
            }

            override fun mouseEntered(e: MouseEvent) {
                cancelLabel.foreground = ColorScheme.PROGRESS_ERROR_COLOR.darker()
            }

            override fun mouseExited(e: MouseEvent) {
                cancelLabel.foreground = ColorScheme.PROGRESS_ERROR_COLOR
            }
        })

        renameLabel.font = FontManager.getRunescapeSmallFont()
        renameLabel.foreground = ColorScheme.LIGHT_GRAY_COLOR.darker()
        renameLabel.addMouseListener(object: MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (!Helper.checkClick(e))
                    return
                renameLabel.foreground = ColorScheme.LIGHT_GRAY_COLOR
            }

            override fun mouseReleased(e: MouseEvent) {
                if (!Helper.checkClick(e))
                    return
                nameInput.setEditable(true)
                saveLabel.isVisible = true
                cancelLabel.isVisible = true
                renameLabel.isVisible = false
                nameInput.textField.requestFocusInWindow()
                nameInput.textField.selectAll()
                renameLabel.foreground = ColorScheme.LIGHT_GRAY_COLOR.darker().darker()
            }

            override fun mouseEntered(e: MouseEvent) {
                renameLabel.foreground = ColorScheme.LIGHT_GRAY_COLOR.darker().darker()
            }

            override fun mouseExited(e: MouseEvent) {
                renameLabel.foreground = ColorScheme.LIGHT_GRAY_COLOR.darker()
            }
        })

        nameActions.add(saveLabel, BorderLayout.EAST)
        nameActions.add(cancelLabel, BorderLayout.WEST)
        nameActions.add(renameLabel, BorderLayout.CENTER)

        nameInput.text = cameraPoint.name
        nameInput.border = null
        nameInput.setEditable(false)
        nameInput.background = ColorScheme.DARKER_GRAY_COLOR
        nameInput.preferredSize = Dimension(0, 24)
        nameInput.textField.foreground = Color.WHITE
        nameInput.textField.border = EmptyBorder(0, 8, 0, 0)
        nameInput.addKeyListener(object: KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    nameInput.setEditable(false)
                    cameraPoint.name = nameInput.text
                    plugin.updateConfig()
                    saveLabel.isVisible = false
                    cancelLabel.isVisible = false
                    renameLabel.isVisible = true
                    requestFocusInWindow()
                } else if (e.keyCode == KeyEvent.VK_ESCAPE) {
                    nameInput.setEditable(false)
                    nameInput.text = cameraPoint.name
                    saveLabel.isVisible = false
                    cancelLabel.isVisible = false
                    renameLabel.isVisible = true
                    requestFocusInWindow()
                }
            }
        })
        nameInput.textField.addMouseListener(object: MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (!Helper.checkClick(e))
                    return
                nameInput.setEditable(true)
                saveLabel.isVisible = true
                cancelLabel.isVisible = true
                renameLabel.isVisible = false
                nameInput.textField.requestFocusInWindow()
                nameInput.textField.selectAll()
            }
        })

        enabledBox.isSelected = cameraPoint.enabled
        enabledBox.addItemListener {
            cameraPoint.enabled = enabledBox.isSelected
            plugin.updateConfig()
        }

        namePanel.add(enabledBox, BorderLayout.WEST)
        namePanel.add(nameInput, BorderLayout.CENTER)
        namePanel.add(nameActions, BorderLayout.EAST)

        // Row 2: Button, Up Arrow, X
        val bottomPanel = JPanel(BorderLayout())
        bottomPanel.background = ColorScheme.DARKER_GRAY_COLOR
        bottomPanel.border = EmptyBorder(8, 8, 8, 8)

        keybindButton.toolTipText = "Load camera point hotkey"
        keybindButton.text = cameraPoint.keybind.toString()
        keybindButton.font = FontManager.getDefaultFont().deriveFont(12f)
        keybindButton.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent) {
                keybindButton.text = Keybind.NOT_SET.toString()
                cameraPoint.keybind = Keybind.NOT_SET
                plugin.updateConfig()
            }
        })
        keybindButton.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ESCAPE) {
                    requestFocusInWindow()
                    return
                }
                val hotkey = Keybind(e)
                keybindButton.text = hotkey.toString()
                cameraPoint.keybind = hotkey
                plugin.updateConfig()
                requestFocusInWindow()
            }
        })

        val buttonsPanel = JPanel(BorderLayout(4, 0))
        buttonsPanel.background = ColorScheme.DARKER_GRAY_COLOR

        fromGameLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        fromGameLabel.background = ColorScheme.DARKER_GRAY_COLOR
        fromGameLabel.toolTipText = "Get camera details from game"
        fromGameLabel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (!Helper.checkClick(e))
                    return
                fromGameLabel.icon = FROM_GAME_PRESSED_ICON
            }

            override fun mouseReleased(e: MouseEvent) {
                if (!Helper.checkClick(e))
                    return
                val result = JOptionPane.showConfirmDialog(this@PointPanel, "Are you sure you want to load the camera details from the game?", "Are you sure?", JOptionPane.OK_CANCEL_OPTION)
                if (result == 0) {
                    plugin.saveCameraPosition(cameraPoint)
                    plugin.updateConfig()
                }
                fromGameLabel.icon = FROM_GAME_HOVER_ICON
            }

            override fun mouseEntered(e: MouseEvent?) {
                fromGameLabel.icon = FROM_GAME_HOVER_ICON
            }

            override fun mouseExited(e: MouseEvent?) {
                fromGameLabel.icon = FROM_GAME_ICON
            }
        })

        deleteLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        deleteLabel.background = ColorScheme.DARKER_GRAY_COLOR
        deleteLabel.toolTipText = "Delete camera point"
        deleteLabel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (!Helper.checkClick(e))
                    return
                deleteLabel.icon = DELETE_PRESSED_ICON
            }

            override fun mouseReleased(e: MouseEvent) {
                if (!Helper.checkClick(e))
                    return
                val result = JOptionPane.showConfirmDialog(this@PointPanel, "Are you sure you want to delete this camera point?", "Are you sure?", JOptionPane.OK_CANCEL_OPTION)
                if (result == 0) {
                    plugin.cameraPointGroupManager.removePointFromGroup(group, cameraPoint)
                    plugin.updateConfig()
                    reloadFunction()
                }
                deleteLabel.icon = DELETE_HOVER_ICON
            }

            override fun mouseEntered(e: MouseEvent?) {
                deleteLabel.icon = DELETE_HOVER_ICON
            }

            override fun mouseExited(e: MouseEvent?) {
                deleteLabel.icon = DELETE_ICON
            }
        })

        buttonsPanel.add(fromGameLabel, BorderLayout.WEST)
        buttonsPanel.add(deleteLabel, BorderLayout.EAST)

        bottomPanel.add(keybindButton, BorderLayout.WEST)
        bottomPanel.add(buttonsPanel, BorderLayout.EAST)

        add(namePanel, BorderLayout.NORTH)
        add(bottomPanel, BorderLayout.SOUTH)
    }
}