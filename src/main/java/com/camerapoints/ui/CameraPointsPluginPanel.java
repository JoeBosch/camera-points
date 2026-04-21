package com.camerapoints.ui;

import com.camerapoints.CameraPointsPlugin;
import com.camerapoints.models.CameraPointGroup;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CameraPointsPluginPanel extends PluginPanel
{
	private final CameraPointsPlugin plugin;
	private final ImageIcon addIcon = new ImageIcon(ImageUtil.loadImageResource(CameraPointsPlugin.class, "add_icon.png"));
	private final ImageIcon addHoverIcon = new ImageIcon(ImageUtil.alphaOffset(addIcon.getImage(), -100));
	private final ImageIcon addPressedIcon = new ImageIcon(ImageUtil.alphaOffset(addIcon.getImage(), -50));

	private final JLabel titleLabel = new JLabel("Camera Points");
	private final JLabel addGroupLabel = new JLabel(addIcon);
	private final JPanel groupsContainer;

	public CameraPointsPluginPanel(CameraPointsPlugin plugin)
	{
		super(false);
		this.plugin = plugin;
		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(6, 6, 6, 6));
		contentPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBorder(new EmptyBorder(1, 0, 10, 0));

		titleLabel.setForeground(Color.WHITE);
		headerPanel.add(titleLabel, BorderLayout.WEST);

		addGroupLabel.setToolTipText("Add a new camera point group");
		addGroupLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addGroupLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (!Helper.checkClick(e))
				{
					return;
				}
				addGroupLabel.setIcon(addPressedIcon);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (!Helper.checkClick(e))
				{
					return;
				}
				plugin.getCameraPointGroupManager().addGroup();
				plugin.updateConfig();
				reload();
				addGroupLabel.setIcon(addHoverIcon);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				addGroupLabel.setIcon(addHoverIcon);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				addGroupLabel.setIcon(addIcon);
			}
		});
		headerPanel.add(addGroupLabel, BorderLayout.EAST);
		contentPanel.add(headerPanel, BorderLayout.NORTH);

		groupsContainer = new JPanel();
		groupsContainer.setLayout(new BoxLayout(groupsContainer, BoxLayout.Y_AXIS));
		groupsContainer.setBorder(new EmptyBorder(0, 0, 0, 3));

		JPanel groupsWrapper = new JPanel(new BorderLayout());
		groupsWrapper.add(groupsContainer, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(groupsWrapper);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		add(contentPanel, BorderLayout.CENTER);
		reload();
	}

	public void reload()
	{
		groupsContainer.removeAll();
		for (CameraPointGroup group : plugin.getCameraPointGroupManager().getAllGroups())
		{
			GroupPanel groupPanel = new GroupPanel(plugin, group, this::reload);
			groupsContainer.add(groupPanel);
			groupsContainer.add(Box.createVerticalStrut(10));
		}
		revalidate();
		repaint();
	}
}
