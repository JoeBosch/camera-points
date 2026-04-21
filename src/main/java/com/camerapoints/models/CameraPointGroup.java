package com.camerapoints.models;

import net.runelite.client.config.Keybind;

import java.util.List;
import java.util.Objects;

public class CameraPointGroup
{
	private int id;
	private String name;
	private boolean enabled;
	private Keybind nextKeybind;
	private Keybind previousKeybind;
	private List<CameraPoint> points;

	public CameraPointGroup(int id, String name, boolean enabled, Keybind nextKeybind, Keybind previousKeybind, List<CameraPoint> points)
	{
		this.id = id;
		this.name = name;
		this.enabled = enabled;
		this.nextKeybind = nextKeybind;
		this.previousKeybind = previousKeybind;
		this.points = points;
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

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public Keybind getNextKeybind()
	{
		return nextKeybind;
	}

	public void setNextKeybind(Keybind nextKeybind)
	{
		this.nextKeybind = nextKeybind;
	}

	public Keybind getPreviousKeybind()
	{
		return previousKeybind;
	}

	public void setPreviousKeybind(Keybind previousKeybind)
	{
		this.previousKeybind = previousKeybind;
	}

	public List<CameraPoint> getPoints()
	{
		return points;
	}

	public void setPoints(List<CameraPoint> points)
	{
		this.points = points;
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
		CameraPointGroup that = (CameraPointGroup) o;
		return id == that.id
			&& enabled == that.enabled
			&& Objects.equals(name, that.name)
			&& Objects.equals(nextKeybind, that.nextKeybind)
			&& Objects.equals(previousKeybind, that.previousKeybind)
			&& Objects.equals(points, that.points);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, name, enabled, nextKeybind, previousKeybind, points);
	}

	@Override
	public String toString()
	{
		return "CameraPointGroup{" +
			"id=" + id +
			", name='" + name + '\'' +
			", enabled=" + enabled +
			", nextKeybind=" + nextKeybind +
			", previousKeybind=" + previousKeybind +
			", points=" + points +
			'}';
	}
}
