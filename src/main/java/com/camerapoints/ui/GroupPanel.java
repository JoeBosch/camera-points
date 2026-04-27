package com.camerapoints.ui;

import com.camerapoints.CameraPointsPlugin;
import com.camerapoints.models.CameraPoint;
import com.camerapoints.models.CameraPointGroup;
import net.runelite.client.config.Keybind;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.util.ImageUtil;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GroupPanel extends JPanel
{
	private final CameraPointsPlugin plugin;
	private final CameraPointGroup group;
	private final Runnable reloadFunction;

	private final ImageIcon addIcon = new ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin.class, "add_icon.png"));
	private final ImageIcon addHoverIcon = new ImageIcon(ImageUtil.alphaOffset(addIcon.getImage(), -100));
	private final ImageIcon addPressedIcon = new ImageIcon(ImageUtil.alphaOffset(addIcon.getImage(), -50));
	private final ImageIcon deleteIcon = new ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin.class, "delete_icon.png"));
	private final ImageIcon deleteHoverIcon = new ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin.class, "delete_icon_red.png"));
	private final ImageIcon deletePressedIcon = new ImageIcon(ImageUtil.alphaOffset(deleteHoverIcon.getImage(), -50));

	private final JCheckBox enabledBox = new JCheckBox();
	private final FlatTextField nameInput = new FlatTextField();
	private final JLabel saveLabel = new JLabel("Save");
	private final JLabel cancelLabel = new JLabel("Cancel");
	private final JLabel renameLabel = new JLabel("Rename");
	private final JLabel addPointLabel = new JLabel(addIcon);
	private final JButton prevKeybind = new JButton();
	private final JButton nextKeybind = new JButton();
	private final JLabel deleteLabel = new JLabel(deleteIcon);
	private final JPanel pointsContainer;

	public GroupPanel(CameraPointsPlugin plugin, CameraPointGroup group, Runnable reloadFunction)
	{
		this.plugin = plugin;
		this.group = group;
		this.reloadFunction = reloadFunction;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		enabledBox.setToolTipText("Enable or disable this camera point group");
		enabledBox.setSelected(group.isEnabled());
		enabledBox.addItemListener(e -> {
			group.setEnabled(enabledBox.isSelected());
			plugin.updateConfig();
		});
		headerPanel.add(enabledBox, BorderLayout.WEST);

		JPanel namePanel = new JPanel(new BorderLayout());
		namePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		namePanel.setBorder(new CompoundBorder(
			new MatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
			new LineBorder(ColorScheme.DARKER_GRAY_COLOR)));

		JPanel nameActions = new JPanel(new BorderLayout(4, 0));
		nameActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		configureNameEditActions(nameActions);

		nameInput.setText(group.getName());
		nameInput.setEditable(false);
		nameInput.getTextField().setForeground(Color.WHITE);
		nameInput.getTextField().setFont(FontManager.getRunescapeBoldFont());
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
				if (!Helper.checkClick(e) || e.getClickCount() < 2 || !renameLabel.isVisible())
				{
					return;
				}
				startRename();
			}
		});

		namePanel.add(nameActions, BorderLayout.EAST);
		namePanel.add(nameInput, BorderLayout.CENTER);
		headerPanel.add(namePanel, BorderLayout.CENTER);

		JPanel actionsPanel = new JPanel(new GridBagLayout());
		actionsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		GridBagConstraints keybindConstraints = new GridBagConstraints();
		keybindConstraints.fill = GridBagConstraints.HORIZONTAL;
		keybindConstraints.weightx = 0.4;
		keybindConstraints.insets = new Insets(0, 2, 0, 2);

		GridBagConstraints itemConstraints = new GridBagConstraints();
		itemConstraints.fill = GridBagConstraints.HORIZONTAL;
		itemConstraints.weightx = 0.2;
		itemConstraints.insets = new Insets(0, 2, 0, 2);

		configureKeybindButton(prevKeybind, group.getPreviousKeybind(), "Load previous camera point in group", keybind -> group.setPreviousKeybind(keybind));
		actionsPanel.add(prevKeybind, keybindConstraints);

		configureKeybindButton(nextKeybind, group.getNextKeybind(), "Load next camera point in group", keybind -> group.setNextKeybind(keybind));
		actionsPanel.add(nextKeybind, keybindConstraints);

		addPointLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addPointLabel.setToolTipText("Add a camera point to this group");
		addPointLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (!Helper.checkClick(e))
				{
					return;
				}
				addPointLabel.setIcon(addPressedIcon);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (!Helper.checkClick(e))
				{
					return;
				}
				plugin.addPointToGroup(group);
				plugin.updateConfig();
				reload();
				addPointLabel.setIcon(addHoverIcon);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				addPointLabel.setIcon(addHoverIcon);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				addPointLabel.setIcon(addIcon);
			}
		});
		actionsPanel.add(addPointLabel, itemConstraints);

		deleteLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		deleteLabel.setToolTipText("Delete this camera point group");
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
				int result = JOptionPane.showConfirmDialog(GroupPanel.this,
					"Are you sure you want to delete this camera point group?",
					"Are you sure?",
					JOptionPane.OK_CANCEL_OPTION);
				if (result == 0)
				{
					plugin.getCameraPointGroupManager().removeGroup(group);
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
		actionsPanel.add(deleteLabel, itemConstraints);

		headerPanel.add(actionsPanel, BorderLayout.SOUTH);
		add(headerPanel, BorderLayout.NORTH);

		pointsContainer = new JPanel();
		pointsContainer.setLayout(new BoxLayout(pointsContainer, BoxLayout.Y_AXIS));
		add(pointsContainer, BorderLayout.CENTER);
		reload();
	}

	private void configureNameEditActions(JPanel nameActions)
	{
		saveLabel.setVisible(false);
		saveLabel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		saveLabel.setFont(FontManager.getRunescapeSmallFont());
		saveLabel.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
		saveLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (!Helper.checkClick(mouseEvent))
				{
					return;
				}
				saveLabel.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.brighter());
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent)
			{
				if (!Helper.checkClick(mouseEvent))
				{
					return;
				}
				saveName();
				saveLabel.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.darker());
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				saveLabel.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.darker());
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				saveLabel.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
			}
		});
		nameActions.add(saveLabel, BorderLayout.EAST);

		cancelLabel.setVisible(false);
		cancelLabel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		cancelLabel.setFont(FontManager.getRunescapeSmallFont());
		cancelLabel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
		cancelLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (!Helper.checkClick(mouseEvent))
				{
					return;
				}
				cancelLabel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR.brighter());
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent)
			{
				if (!Helper.checkClick(mouseEvent))
				{
					return;
				}
				cancelRename();
				cancelLabel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR.darker());
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				cancelLabel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR.darker());
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				cancelLabel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
			}
		});
		nameActions.add(cancelLabel, BorderLayout.WEST);

		renameLabel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		renameLabel.setFont(FontManager.getRunescapeSmallFont());
		renameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
		renameLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (!Helper.checkClick(mouseEvent))
				{
					return;
				}
				renameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent)
			{
				if (!Helper.checkClick(mouseEvent))
				{
					return;
				}
				startRename();
				renameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker().darker());
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				renameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker().darker());
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				renameLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
			}
		});
		nameActions.add(renameLabel, BorderLayout.CENTER);
	}

	private interface KeybindSetter
	{
		void set(Keybind keybind);
	}

	private void configureKeybindButton(JButton button, Keybind keybind, String toolTip, KeybindSetter setter)
	{
		button.setPreferredSize(new Dimension(73, 22));
		button.setMinimumSize(new Dimension(73, 0));
		button.setMaximumSize(new Dimension(73, Integer.MAX_VALUE));
		button.setText(keybind.toString());
		button.setToolTipText(toolTip);
		button.setFont(FontManager.getDefaultFont().deriveFont(12f));
		button.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent mouseEvent)
			{
				button.setText(Keybind.NOT_SET.toString());
				setter.set(Keybind.NOT_SET);
				plugin.updateConfig();
			}
		});
		button.addKeyListener(new KeyAdapter()
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
				button.setText(hotkey.toString());
				setter.set(hotkey);
				plugin.updateConfig();
				requestFocusInWindow();
			}
		});
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
		group.setName(nameInput.getText());
		plugin.updateConfig();
		saveLabel.setVisible(false);
		cancelLabel.setVisible(false);
		renameLabel.setVisible(true);
		requestFocusInWindow();
	}

	private void cancelRename()
	{
		nameInput.setEditable(false);
		nameInput.setText(group.getName());
		saveLabel.setVisible(false);
		cancelLabel.setVisible(false);
		renameLabel.setVisible(true);
		requestFocusInWindow();
	}

	public void reload()
	{
		pointsContainer.removeAll();
		for (CameraPoint point : plugin.getCameraPointGroupManager().getAllPointsForGroup(group))
		{
			PointPanel pointPanel = new PointPanel(plugin, group, point, reloadFunction);
			pointsContainer.add(pointPanel);
			pointsContainer.add(Box.createVerticalStrut(5));
		}
		revalidate();
		repaint();
	}
}
