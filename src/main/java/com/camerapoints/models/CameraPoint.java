package com.camerapoints.models;

import net.runelite.client.config.Keybind;

import java.util.Objects;

public class CameraPoint
{
	private int id;
	private String name;
	private int pitch;
	private int yaw;
	private int zoom;
	private Keybind keybind;
	private boolean enabled;

	public CameraPoint(int id, String name, int pitch, int yaw, int zoom, Keybind keybind, boolean enabled)
	{
		this.id = id;
		this.name = name;
		this.pitch = pitch;
		this.yaw = yaw;
		this.zoom = zoom;
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

	public int getPitch()
	{
		return pitch;
	}

	public void setPitch(int pitch)
	{
		this.pitch = pitch;
	}

	public int getYaw()
	{
		return yaw;
	}

	public void setYaw(int yaw)
	{
		this.yaw = yaw;
	}

	public int getZoom()
	{
		return zoom;
	}

	public void setZoom(int zoom)
	{
		this.zoom = zoom;
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
			&& pitch == that.pitch
			&& yaw == that.yaw
			&& zoom == that.zoom
			&& enabled == that.enabled
			&& Objects.equals(name, that.name)
			&& Objects.equals(keybind, that.keybind);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, name, pitch, yaw, zoom, keybind, enabled);
	}

	@Override
	public String toString()
	{
		return "CameraPoint{" +
			"id=" + id +
			", name='" + name + '\'' +
			", pitch=" + pitch +
			", yaw=" + yaw +
			", zoom=" + zoom +
			", keybind=" + keybind +
			", enabled=" + enabled +
			'}';
	}
}
