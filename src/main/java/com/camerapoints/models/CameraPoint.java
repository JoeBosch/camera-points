package com.camerapoints.models;

import net.runelite.client.config.Keybind;

import java.util.Objects;

public class CameraPoint
{
	public enum Direction
	{
		NONE(0, "Unchanged"),
		NORTH(1, "North"),
		EAST(2, "East"),
		SOUTH(3, "South"),
		WEST(4, "West");

		private final int scriptValue;
		private final String displayName;

		Direction(int scriptValue, String displayName)
		{
			this.scriptValue = scriptValue;
			this.displayName = displayName;
		}

		public int getScriptValue()
		{
			return scriptValue;
		}

		public static Direction fromCameraYaw(int yaw)
		{
			int normalized = ((yaw % 2048) + 2048) % 2048;
			if (normalized < 256 || normalized >= 1792)
			{
				return SOUTH;
			}
			if (normalized < 768)
			{
				return WEST;
			}
			if (normalized < 1280)
			{
				return NORTH;
			}
			return EAST;
		}

		@Override
		public String toString()
		{
			return displayName;
		}
	}

	private int id;
	private String name;
	private Direction direction;
	private int zoom;
	private boolean applyZoom;
	private Keybind keybind;
	private boolean enabled;

	public CameraPoint(int id, String name, Direction direction, int zoom, boolean applyZoom, Keybind keybind, boolean enabled)
	{
		this.id = id;
		this.name = name;
		this.direction = direction;
		this.zoom = zoom;
		this.applyZoom = applyZoom;
		this.keybind = keybind;
		this.enabled = enabled;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Direction getDirection()
	{
		return direction;
	}

	public void setDirection(Direction direction)
	{
		this.direction = direction;
	}

	public int getZoom()
	{
		return zoom;
	}

	public void setZoom(int zoom)
	{
		this.zoom = zoom;
	}

	public boolean isApplyZoom()
	{
		return applyZoom;
	}

	public void setApplyZoom(boolean applyZoom)
	{
		this.applyZoom = applyZoom;
	}

	public Keybind getKeybind()
	{
		return keybind;
	}

	public void setKeybind(Keybind keybind)
	{
		this.keybind = keybind;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		CameraPoint that = (CameraPoint) o;
		return id == that.id
			&& zoom == that.zoom
			&& applyZoom == that.applyZoom
			&& enabled == that.enabled
			&& Objects.equals(name, that.name)
			&& direction == that.direction
			&& Objects.equals(keybind, that.keybind);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, name, direction, zoom, applyZoom, keybind, enabled);
	}

	@Override
	public String toString()
	{
		return "CameraPoint{" +
			"id=" + id +
			", name='" + name + '\'' +
			", direction=" + direction +
			", zoom=" + zoom +
			", applyZoom=" + applyZoom +
			", keybind=" + keybind +
			", enabled=" + enabled +
			'}';
	}
}
