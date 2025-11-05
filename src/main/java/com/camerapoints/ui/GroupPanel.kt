package com.camerapoints.ui

import com.camerapoints.CameraPointsPlugin
import com.camerapoints.models.CameraPointGroup
import net.runelite.client.config.Keybind
import net.runelite.client.ui.ColorScheme
import net.runelite.client.ui.FontManager
import net.runelite.client.ui.components.FlatTextField
import net.runelite.client.util.ImageUtil
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder
import javax.swing.border.MatteBorder

class GroupPanel(val plugin: CameraPointsPlugin, private val group: CameraPointGroup, private val reloadFunction: () -> Unit) : JPanel() {

    val ADD_ICON = ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin::class.java, "add_icon.png"))
    val ADD_HOVER_ICON = ImageIcon(ImageUtil.alphaOffset(ADD_ICON.image, -100))
    val ADD_PRESSED_ICON = ImageIcon(ImageUtil.alphaOffset(ADD_ICON.image, -50))

    val DELETE_ICON = ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin::class.java, "delete_icon.png"))
    val DELETE_HOVER_ICON =
        ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin::class.java, "delete_icon_red.png"))
    val DELETE_PRESSED_ICON = ImageIcon(ImageUtil.alphaOffset(DELETE_HOVER_ICON.image, -50))

    val enabledBox: JCheckBox = JCheckBox()
    val nameInput: FlatTextField = FlatTextField()
    val saveLabel: JLabel = JLabel("Save")
    val cancelLabel: JLabel = JLabel("Cancel")
    val renameLabel: JLabel = JLabel("Rename")
    val addPointLabel: JLabel = JLabel(ADD_ICON)
    val prevKeybind: JButton = JButton()
    val nextKeybind: JButton = JButton()
    val deleteLabel: JLabel = JLabel(DELETE_ICON)
    private val pointsContainer: JPanel

    init {
        layout = BorderLayout()
        border = EmptyBorder(5, 5, 5, 5)
        background = ColorScheme.DARKER_GRAY_COLOR

        // Header Panel
        val headerPanel = JPanel(BorderLayout())
        headerPanel.background = ColorScheme.DARKER_GRAY_COLOR
        headerPanel.border = EmptyBorder(10, 10, 10, 10)

        // Row 1: Checkbox, Name, Plus
        enabledBox.toolTipText = "Enable or disable this camera point group"
        enabledBox.isSelected = group.enabled
        enabledBox.addItemListener {
            group.enabled = enabledBox.isSelected
            plugin.updateConfig()
        }
        headerPanel.add(enabledBox, BorderLayout.WEST)

        val namePanel = JPanel(BorderLayout())
        namePanel.background = ColorScheme.DARKER_GRAY_COLOR
        namePanel.border = CompoundBorder(
            MatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
            LineBorder(ColorScheme.DARKER_GRAY_COLOR)
        )

        val nameActions = JPanel(BorderLayout(4, 0))
        nameActions.background = ColorScheme.DARKER_GRAY_COLOR

        saveLabel.isVisible = false
        saveLabel.background = ColorScheme.DARKER_GRAY_COLOR
        saveLabel.font = FontManager.getRunescapeSmallFont()
        saveLabel.foreground = ColorScheme.PROGRESS_COMPLETE_COLOR
        saveLabel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(mouseEvent: MouseEvent) {
                if (!Helper.checkClick(mouseEvent))
                    return
                saveLabel.foreground = ColorScheme.PROGRESS_COMPLETE_COLOR.brighter()
            }
            override fun mouseReleased(mouseEvent: MouseEvent) {
                if (!Helper.checkClick(mouseEvent))
                    return
                nameInput.setEditable(false)
                group.name = nameInput.text
                plugin.updateConfig()
                saveLabel.isVisible = false
                cancelLabel.isVisible = false
                renameLabel.isVisible = true
                requestFocusInWindow()
                saveLabel.foreground = ColorScheme.PROGRESS_COMPLETE_COLOR.darker()
            }
            override fun mouseEntered(mouseEvent: MouseEvent) {
                saveLabel.foreground = ColorScheme.PROGRESS_COMPLETE_COLOR.darker()
            }
            override fun mouseExited(mouseEvent: MouseEvent) {
                saveLabel.foreground = ColorScheme.PROGRESS_COMPLETE_COLOR
            }
        })
        nameActions.add(saveLabel, BorderLayout.EAST)

        cancelLabel.isVisible = false
        cancelLabel.background = ColorScheme.DARKER_GRAY_COLOR
        cancelLabel.font = FontManager.getRunescapeSmallFont()
        cancelLabel.foreground = ColorScheme.PROGRESS_ERROR_COLOR
        cancelLabel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(mouseEvent: MouseEvent) {
                if (!Helper.checkClick(mouseEvent))
                    return
                cancelLabel.foreground = ColorScheme.PROGRESS_ERROR_COLOR.brighter()
            }
            override fun mouseReleased(mouseEvent: MouseEvent) {
                if (!Helper.checkClick(mouseEvent))
                    return
                nameInput.setEditable(false)
                nameInput.text = group.name
                saveLabel.isVisible = false
                cancelLabel.isVisible = false
                renameLabel.isVisible = true
                requestFocusInWindow()
                cancelLabel.foreground = ColorScheme.PROGRESS_ERROR_COLOR.darker()
            }
            override fun mouseEntered(mouseEvent: MouseEvent) {
                cancelLabel.foreground = ColorScheme.PROGRESS_ERROR_COLOR.darker()
            }
            override fun mouseExited(mouseEvent: MouseEvent) {
                cancelLabel.foreground = ColorScheme.PROGRESS_ERROR_COLOR
            }
        })
        nameActions.add(cancelLabel, BorderLayout.WEST)

        renameLabel.background = ColorScheme.DARKER_GRAY_COLOR
        renameLabel.font = FontManager.getRunescapeSmallFont()
        renameLabel.foreground = ColorScheme.LIGHT_GRAY_COLOR.darker()
        renameLabel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(mouseEvent: MouseEvent) {
                if (!Helper.checkClick(mouseEvent))
                    return
                renameLabel.foreground = ColorScheme.LIGHT_GRAY_COLOR
            }
            override fun mouseReleased(mouseEvent: MouseEvent) {
                if (!Helper.checkClick(mouseEvent))
                    return
                nameInput.setEditable(true)
                saveLabel.isVisible = true
                cancelLabel.isVisible = true
                renameLabel.isVisible = false
                nameInput.textField.requestFocusInWindow()
                nameInput.textField.selectAll()
                renameLabel.foreground = ColorScheme.LIGHT_GRAY_COLOR.darker().darker()
            }
            override fun mouseEntered(mouseEvent: MouseEvent) {
                renameLabel.foreground = ColorScheme.LIGHT_GRAY_COLOR.darker().darker()
            }
            override fun mouseExited(mouseEvent: MouseEvent) {
                renameLabel.foreground = ColorScheme.LIGHT_GRAY_COLOR.darker()
            }
        })
        nameActions.add(renameLabel, BorderLayout.CENTER)

        namePanel.add(nameActions, BorderLayout.EAST)

        nameInput.text = group.name
        nameInput.setEditable(false)
        nameInput.textField.foreground = Color.WHITE
        nameInput.textField.font = FontManager.getRunescapeBoldFont()
        nameInput.textField.border = EmptyBorder(0, 8, 0, 0)
        nameInput.addKeyListener(object: KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    nameInput.setEditable(false)
                    group.name = nameInput.text
                    plugin.updateConfig()
                    saveLabel.isVisible = false
                    cancelLabel.isVisible = false
                    renameLabel.isVisible = true
                    requestFocusInWindow()
                } else if (e.keyCode == KeyEvent.VK_ESCAPE) {
                    nameInput.setEditable(false)
                    nameInput.text = group.name
                    saveLabel.isVisible = false
                    cancelLabel.isVisible = false
                    renameLabel.isVisible = true
                    requestFocusInWindow()
                }
            }
        })
        nameInput.textField.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (!Helper.checkClick(e) || e.clickCount < 2 || !renameLabel.isVisible)
                    return
                nameInput.setEditable(true)
                saveLabel.isVisible = true
                cancelLabel.isVisible = true
                renameLabel.isVisible = false
                nameInput.textField.requestFocusInWindow()
                nameInput.textField.selectAll()
            }
        })
        namePanel.add(nameInput, BorderLayout.CENTER)

        headerPanel.add(namePanel, BorderLayout.CENTER)

        val actionsPanel = JPanel(GridBagLayout())
        actionsPanel.background = ColorScheme.DARKER_GRAY_COLOR

        val keybindConstraints = GridBagConstraints()
        keybindConstraints.fill = GridBagConstraints.HORIZONTAL
        keybindConstraints.weightx = 0.4
        keybindConstraints.insets = Insets(0, 2, 0, 2)

        val itemConstraints = GridBagConstraints()
        itemConstraints.fill = GridBagConstraints.HORIZONTAL
        itemConstraints.weightx = 0.2
        itemConstraints.insets = Insets(0, 2, 0, 2)

        // Row 2: Hotkey buttons and X
        prevKeybind.preferredSize = Dimension(73, 22)
        prevKeybind.minimumSize = Dimension(73, 0)
        prevKeybind.maximumSize = Dimension(73, Int.MAX_VALUE)
        prevKeybind.text = group.previousKeybind.toString()
        prevKeybind.toolTipText = "Load previous camera point in group"
        prevKeybind.font = FontManager.getDefaultFont().deriveFont(12f)
        prevKeybind.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(mouseEvent: MouseEvent) {
                prevKeybind.text = Keybind.NOT_SET.toString()
                group.previousKeybind = Keybind.NOT_SET
                plugin.updateConfig()
            }
        })
        prevKeybind.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ESCAPE) {
                    requestFocusInWindow()
                    return
                }
                val hotkey = Keybind(e)
                prevKeybind.text = hotkey.toString()
                group.previousKeybind = hotkey
                plugin.updateConfig()
                requestFocusInWindow()
            }
        })
        actionsPanel.add(prevKeybind, keybindConstraints)

        nextKeybind.preferredSize = Dimension(73, 22)
        nextKeybind.minimumSize = Dimension(73, 0)
        nextKeybind.maximumSize = Dimension(73, Int.MAX_VALUE)
        nextKeybind.text = group.nextKeybind.toString()
        nextKeybind.toolTipText = "Load next camera point in group"
        nextKeybind.font = FontManager.getDefaultFont().deriveFont(12f)
        nextKeybind.addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(mouseEvent: MouseEvent) {
                nextKeybind.text = Keybind.NOT_SET.toString()
                group.nextKeybind = Keybind.NOT_SET
                plugin.updateConfig()
            }
        })
        nextKeybind.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ESCAPE) {
                    requestFocusInWindow()
                    return
                }
                val hotkey = Keybind(e)
                nextKeybind.text = hotkey.toString()
                group.nextKeybind = hotkey
                plugin.updateConfig()
                requestFocusInWindow()
            }
        })
        actionsPanel.add(nextKeybind, keybindConstraints)

        addPointLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        addPointLabel.toolTipText = "Add a camera point to this group"
        addPointLabel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (!Helper.checkClick(e))
                    return
                addPointLabel.icon = ADD_PRESSED_ICON
            }
            override fun mouseReleased(e: MouseEvent) {
                if (!Helper.checkClick(e))
                    return
                plugin.cameraPointGroupManager.addPointToGroup(group)
                plugin.updateConfig()
                reload()
                addPointLabel.icon = ADD_HOVER_ICON
            }
            override fun mouseEntered(e: MouseEvent?) {
                addPointLabel.icon = ADD_HOVER_ICON
            }
            override fun mouseExited(e: MouseEvent?) {
                addPointLabel.icon = ADD_ICON
            }
        })
        actionsPanel.add(addPointLabel, itemConstraints)

        deleteLabel.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        deleteLabel.toolTipText = "Delete this camera point group"
        deleteLabel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (!Helper.checkClick(e))
                    return
                deleteLabel.icon = DELETE_PRESSED_ICON
            }

            override fun mouseReleased(e: MouseEvent) {
                if (!Helper.checkClick(e))
                    return
                val result = JOptionPane.showConfirmDialog(this@GroupPanel, "Are you sure you want to delete this camera point group?", "Are you sure?", JOptionPane.OK_CANCEL_OPTION)
                if (result == 0) {
                    plugin.cameraPointGroupManager.removeGroup(group)
                    plugin.updateConfig()
                    reloadFunction()
                }
                deleteLabel.icon = DELETE_HOVER_ICON
            }

            override fun mouseEntered(e: MouseEvent) {
                deleteLabel.icon = DELETE_HOVER_ICON
            }

            override fun mouseExited(e: MouseEvent) {
                deleteLabel.icon = DELETE_ICON
            }
        })
        actionsPanel.add(deleteLabel, itemConstraints)

        headerPanel.add(actionsPanel, BorderLayout.SOUTH)

        add(headerPanel, BorderLayout.NORTH)

        // Points Container
        pointsContainer = JPanel()
        pointsContainer.layout = BoxLayout(pointsContainer, BoxLayout.Y_AXIS)
        add(pointsContainer, BorderLayout.CENTER)


        // Load initial points
        reload()
    }

    fun reload() {
        pointsContainer.removeAll()

        for (point in plugin.cameraPointGroupManager.getAllPointsForGroup(group)) {
            val pointPanel = PointPanel(plugin, group, point, reloadFunction)
            pointsContainer.add(pointPanel)
            pointsContainer.add(Box.createVerticalStrut(5))
        }

        revalidate()
        repaint()
    }
}