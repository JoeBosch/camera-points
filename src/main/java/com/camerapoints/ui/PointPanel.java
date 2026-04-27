package com.camerapoints.ui;

import com.camerapoints.CameraPointsPlugin;
import com.camerapoints.models.CameraPoint;
import com.camerapoints.models.CameraPointGroup;
import net.runelite.client.config.Keybind;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.util.ImageUtil;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PointPanel extends JPanel
{
    private static final int ZOOM_LIMIT_MIN = -272;
    private static final int ZOOM_LIMIT_MAX = 1400;

	private final CameraPointsPlugin plugin;
	private final CameraPointGroup group;
	private final CameraPoint cameraPoint;
	private final Runnable reloadFunction;

	private final ImageIcon fromGameIcon = new ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin.class, "from_game_icon.png"));
	private final ImageIcon fromGameHoverIcon = new ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin.class, "from_game_icon_blue.png"));
	private final ImageIcon fromGamePressedIcon = new ImageIcon(ImageUtil.alphaOffset(fromGameHoverIcon.getImage(), -50));
	private final ImageIcon deleteIcon = new ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin.class, "delete_icon.png"));
	private final ImageIcon deleteHoverIcon = new ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin.class, "delete_icon_red.png"));
	private final ImageIcon deletePressedIcon = new ImageIcon(ImageUtil.alphaOffset(deleteHoverIcon.getImage(), -50));
    private final ImageIcon zoomIcon = new ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin.class, "zoom_icon.png"));
    private final ImageIcon zoomPressedIcon = new ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin.class, "zoom_icon_blue.png"));

	private final JCheckBox enabledBox = new JCheckBox();
	private final FlatTextField nameInput = new FlatTextField();
	private final JLabel saveLabel = new JLabel("Save");
	private final JLabel cancelLabel = new JLabel("Cancel");
	private final JLabel renameLabel = new JLabel("Rename");
	private final JButton keybindButton = new JButton();
	private final JComboBox<CameraPoint.Direction> directionDropdown = new JComboBox<>(CameraPoint.Direction.values());
	private final JToggleButton applyZoomButton = new JToggleButton();
	private final JLabel updateZoomLabel = new JLabel(fromGameIcon);
	private final JLabel deleteLabel = new JLabel(deleteIcon);
    private final JSpinner zoomSpinner = new JSpinner(new SpinnerNumberModel(ZOOM_LIMIT_MIN, ZOOM_LIMIT_MIN, ZOOM_LIMIT_MAX, 1));

	public PointPanel(CameraPointsPlugin plugin, CameraPointGroup group, CameraPoint cameraPoint, Runnable reloadFunction)
	{
		this.plugin = plugin;
		this.group = group;
		this.cameraPoint = cameraPoint;
		this.reloadFunction = reloadFunction;

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JPanel namePanel = new JPanel(new BorderLayout());
		namePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		namePanel.setBorder(new CompoundBorder(
			new MatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
			new LineBorder(ColorScheme.DARKER_GRAY_COLOR)));

		JPanel nameActions = new JPanel(new BorderLayout(4, 0));
		nameActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		nameActions.setBorder(new EmptyBorder(0, 4, 0, 8));

		configureNameActions(nameActions);

		nameInput.setText(cameraPoint.getName());
		nameInput.setBorder(null);
		nameInput.setEditable(false);
		nameInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		nameInput.setPreferredSize(new Dimension(0, 24));
		nameInput.getTextField().setForeground(Color.WHITE);
		nameInput.getTextField().setBorder(new EmptyBorder(0, 8, 0, 0));
		nameInput.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					saveName();
				}
				else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					cancelRename();
				}
			}
		});
		nameInput.getTextField().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (!Helper.checkClick(e))
				{
					return;
				}
				startRename();
			}
		});

		enabledBox.setSelected(cameraPoint.isEnabled());
		enabledBox.addItemListener(e -> {
			cameraPoint.setEnabled(enabledBox.isSelected());
			plugin.updateConfig();
		});

		namePanel.add(nameInput, BorderLayout.CENTER);
		namePanel.add(nameActions, BorderLayout.EAST);

		JPanel bottomPanel = new JPanel(new BorderLayout(0, 6));
		bottomPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		bottomPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

		keybindButton.setToolTipText("Load camera point hotkey");
		keybindButton.setText(cameraPoint.getKeybind().toString());
		keybindButton.setFont(FontManager.getDefaultFont().deriveFont(12f));
		keybindButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
				keybindButton.setText(Keybind.NOT_SET.toString());
				cameraPoint.setKeybind(Keybind.NOT_SET);
				plugin.updateConfig();
			}
		});
		keybindButton.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					requestFocusInWindow();
					return;
				}
				if (Helper.isModifierKey(e.getKeyCode()))
				{
					return;
				}
				Keybind hotkey = new Keybind(e);
				keybindButton.setText(hotkey.toString());
				cameraPoint.setKeybind(hotkey);
				plugin.updateConfig();
				requestFocusInWindow();
			}
		});

		if (cameraPoint.getDirection() == null)
		{
			cameraPoint.setDirection(CameraPoint.Direction.NORTH);
		}
		directionDropdown.setSelectedItem(cameraPoint.getDirection());
		directionDropdown.setToolTipText("Direction to face when loading this point");
		directionDropdown.addActionListener(e -> {
			CameraPoint.Direction selectedDirection = (CameraPoint.Direction) directionDropdown.getSelectedItem();
			if (selectedDirection == null)
			{
				return;
			}
			cameraPoint.setDirection(selectedDirection);
			plugin.updateConfig();
		});

		applyZoomButton.setSelected(cameraPoint.isApplyZoom());
		applyZoomButton.setToolTipText("Apply saved zoom when loading this point");
		applyZoomButton.setIcon(zoomIcon);
		applyZoomButton.setSelectedIcon(zoomPressedIcon);
		applyZoomButton.setPressedIcon(zoomPressedIcon);
		applyZoomButton.setBorderPainted(false);
		applyZoomButton.setContentAreaFilled(false);
		applyZoomButton.setFocusPainted(false);
		applyZoomButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		applyZoomButton.setPreferredSize(new Dimension(18, 18));
		applyZoomButton.addItemListener(e -> {
			cameraPoint.setApplyZoom(applyZoomButton.isSelected());
			plugin.updateConfig();
		});

		JPanel buttonsPanel = new JPanel(new BorderLayout(4, 0));
		buttonsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		updateZoomLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		updateZoomLabel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		updateZoomLabel.setToolTipText("Update zoom from current game zoom");
		updateZoomLabel.setPreferredSize(new Dimension(18, 18));
		updateZoomLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (!Helper.checkClick(e))
				{
					return;
				}
				updateZoomLabel.setIcon(fromGamePressedIcon);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (!Helper.checkClick(e))
				{
					return;
				}
				int result = JOptionPane.showConfirmDialog(PointPanel.this,
					"Are you sure you want to update zoom from the game?",
					"Are you sure?",
					JOptionPane.OK_CANCEL_OPTION);
				if (result == 0)
				{
					plugin.updateCameraPointZoom(cameraPoint);
					applyZoomButton.setSelected(cameraPoint.isApplyZoom());
					plugin.updateConfig();
				}
				updateZoomLabel.setIcon(fromGameHoverIcon);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				updateZoomLabel.setIcon(fromGameHoverIcon);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				updateZoomLabel.setIcon(fromGameIcon);
			}
		});

		deleteLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		deleteLabel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		deleteLabel.setToolTipText("Delete camera point");
		deleteLabel.setPreferredSize(new Dimension(18, 18));
		deleteLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (!Helper.checkClick(e))
				{
					return;
				}
				deleteLabel.setIcon(deletePressedIcon);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (!Helper.checkClick(e))
				{
					return;
				}
				int result = JOptionPane.showConfirmDialog(PointPanel.this,
					"Are you sure you want to delete this camera point?",
					"Are you sure?",
					JOptionPane.OK_CANCEL_OPTION);
				if (result == 0)
				{
					plugin.getCameraPointGroupManager().removePointFromGroup(group, cameraPoint);
					plugin.updateConfig();
					reloadFunction.run();
				}
				deleteLabel.setIcon(deleteHoverIcon);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				deleteLabel.setIcon(deleteHoverIcon);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				deleteLabel.setIcon(deleteIcon);
			}
		});

		buttonsPanel.add(updateZoomLabel, BorderLayout.WEST);
		buttonsPanel.add(deleteLabel, BorderLayout.EAST);

		directionDropdown.setPreferredSize(new Dimension(100, 22));
		keybindButton.setPreferredSize(new Dimension(78, 22));

		JPanel topMetaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
		topMetaRow.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		topMetaRow.add(enabledBox);
		topMetaRow.add(directionDropdown);

		JPanel bottomControlsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
		bottomControlsRow.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		bottomControlsRow.add(keybindButton);
		bottomControlsRow.add(applyZoomButton);
		bottomControlsRow.add(updateZoomLabel);
		bottomControlsRow.add(deleteLabel);

        zoomSpinner.setToolTipText("Zoom value");
        zoomSpinner.setValue(cameraPoint.getZoom());
        zoomSpinner.setEnabled(cameraPoint.isApplyZoom());
        zoomSpinner.addChangeListener(e ->
        {
            cameraPoint.setZoom((int)zoomSpinner.getValue());
            plugin.updateConfig();
        });

        JPanel centerRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        centerRow.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        centerRow.add(zoomSpinner);

		bottomPanel.add(topMetaRow, BorderLayout.NORTH);
        bottomPanel.add(centerRow, BorderLayout.CENTER);
		bottomPanel.add(bottomControlsRow, BorderLayout.SOUTH);

		add(namePanel, BorderLayout.NORTH);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	private void configureNameActions(JPanel nameActions)
	{
		saveLabel.setVisible(false);
		saveLabel.setFont(FontManager.getRunescapeSmallFont());
		saveLabel.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
		saveLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (!Helper.checkClick(e))
				{
					return;
				}
				saveLabel.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.brighter());
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (!Helper.checkClick(e))
				{
					return;
				}
				saveName();
				saveLabel.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.darker());
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				saveLabel.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.darker());
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				saveLabel.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
			}
		});

		cancelLabel.setVisible(false);
		cancelLabel.setFont(FontManager.getRunescapeSmallFont());
		cancelLabel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
		cancelLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (!Helper.checkClick(e))
				{
					return;
				}
				cancelLabel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR.brighter());
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (!Helper.checkClick(e))
				{
					return;
				}
				cancelRename();
				cancelLabel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR.darker());
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				cancelLabel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR.darker());
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				cancelLabel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
			}
		});

		renameLabel.setFont(FontManager.getRunescapeSmallFont());
		renameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
		renameLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (!Helper.checkClick(e))
				{
					return;
				}
				renameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (!Helper.checkClick(e))
				{
					return;
				}
				startRename();
				renameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker().darker());
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				renameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker().darker());
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				renameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
			}
		});

		nameActions.add(saveLabel, BorderLayout.EAST);
		nameActions.add(cancelLabel, BorderLayout.WEST);
		nameActions.add(renameLabel, BorderLayout.CENTER);
	}

	private void startRename()
	{
		nameInput.setEditable(true);
		saveLabel.setVisible(true);
		cancelLabel.setVisible(true);
		renameLabel.setVisible(false);
		nameInput.getTextField().requestFocusInWindow();
		nameInput.getTextField().selectAll();
	}

	private void saveName()
	{
		nameInput.setEditable(false);
		cameraPoint.setName(nameInput.getText());
		plugin.updateConfig();
		saveLabel.setVisible(false);
		cancelLabel.setVisible(false);
		renameLabel.setVisible(true);
		requestFocusInWindow();
	}

	private void cancelRename()
	{
		nameInput.setEditable(false);
		nameInput.setText(cameraPoint.getName());
		saveLabel.setVisible(false);
		cancelLabel.setVisible(false);
		renameLabel.setVisible(true);
		requestFocusInWindow();
	}
}
