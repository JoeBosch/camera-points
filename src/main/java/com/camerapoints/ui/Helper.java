package com.camerapoints.ui;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;

public final class Helper
{
	private Helper()
	{
	}

	public static boolean checkClick(MouseEvent event)
	{
		if (event.getButton() != MouseEvent.BUTTON1)
		{
			return false;
		}
		if (!(event.getSource() instanceof JComponent))
		{
			return false;
		}
		Point point = event.getPoint();
		Dimension size = ((JComponent) event.getSource()).getSize();
		return point.x >= 0 && point.x <= size.width && point.y >= 0 && point.y <= size.height;
	}
}
