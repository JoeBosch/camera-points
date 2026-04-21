package com.camerapoints;

import com.google.common.base.Strings;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarClientID;
import net.runelite.client.input.KeyListener;

import javax.inject.Inject;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

public class KeyHandler implements KeyListener
{
	@Inject
	private CameraPointsPlugin plugin;

	@Inject
	private Client client;

	@Inject
	private CameraPointsConfig config;

	private boolean typing = false;

	private final KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher()
	{
		@Override
		public boolean dispatchKeyEvent(KeyEvent e)
		{
			if (!chatboxFocused())
			{
				return false;
			}

			if (typing && e != null && e.getKeyCode() == KeyEvent.VK_ESCAPE)
			{
				typing = false;
				return false;
			}

			return false;
		}
	};

	public KeyEventDispatcher getKeyEventDispatcher()
	{
		return keyEventDispatcher;
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if ((!typing && !isDialogOpen()) || !config.disableWhileTyping())
		{
			if (plugin.handleCameraPointChange(e))
			{
				return;
			}
		}

		if (!chatboxFocused())
		{
			return;
		}

		if (!typing)
		{
			if (e != null)
			{
				int code = e.getKeyCode();
				if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SLASH || code == KeyEvent.VK_COLON)
				{
					typing = true;
				}
			}
			return;
		}

		if (e == null)
		{
			return;
		}

		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			typing = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && Strings.isNullOrEmpty(client.getVarcStrValue(VarClientID.CHATINPUT)))
		{
			typing = false;
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	public boolean chatboxFocused()
	{
		net.runelite.api.widgets.Widget chatboxParent = client.getWidget(InterfaceID.Chatbox.UNIVERSE);
		if (chatboxParent == null || chatboxParent.getOnKeyListener() == null)
		{
			return false;
		}

		net.runelite.api.widgets.Widget worldMapSearch = client.getWidget(InterfaceID.Worldmap.MAPLIST_DISPLAY);
		if (worldMapSearch != null && client.getVarcIntValue(VarClientID.WORLDMAP_SEARCHING) == 1)
		{
			return false;
		}

		net.runelite.api.widgets.Widget report = client.getWidget(InterfaceID.Reportabuse.UNIVERSE);
		if (report != null)
		{
			return false;
		}

		return true;
	}

	public boolean isDialogOpen()
	{
		return isHidden(InterfaceID.Chatbox.MES_LAYER_HIDE)
			|| isHidden(InterfaceID.Chatbox.CHATDISPLAY)
			|| !isHidden(InterfaceID.BankpinKeypad.UNIVERSE);
	}

	private boolean isHidden(int component)
	{
		net.runelite.api.widgets.Widget w = client.getWidget(component);
		return w == null || w.isSelfHidden();
	}
}
