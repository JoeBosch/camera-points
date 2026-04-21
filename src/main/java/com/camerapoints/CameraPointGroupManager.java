package com.camerapoints;

import com.camerapoints.models.CameraPoint;
import com.camerapoints.models.CameraPointGroup;
import net.runelite.client.config.Keybind;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CameraPointGroupManager
{
	private final List<CameraPointGroup> groups = new ArrayList<>();
	private final Map<Integer, Integer> currentPointIds = new HashMap<>();

	public void setGroups(List<CameraPointGroup> groups)
	{
		this.groups.clear();
		this.groups.addAll(groups);
	}

	public void addGroup(CameraPointGroup group)
	{
		int nextId = groups.stream().map(CameraPointGroup::getId).max(Integer::compareTo).map(i -> i + 1).orElse(0);
		group.setId(nextId);
		groups.add(group);
	}

	public CameraPointGroup addGroup()
	{
		CameraPointGroup newGroup = new CameraPointGroup(-1, "New Group", true, Keybind.NOT_SET, Keybind.NOT_SET, new ArrayList<>());
		addGroup(newGroup);
		return newGroup;
	}

	public void removeGroup(CameraPointGroup group)
	{
		groups.remove(group);
		for (int i = 0; i < groups.size(); i++)
		{
			groups.get(i).setId(i);
		}
	}

	public void moveGroup(CameraPointGroup group, int offset)
	{
		int index = groups.indexOf(group);
		if (index == -1 || index + offset < 0 || index + offset >= groups.size())
		{
			return;
		}

		groups.remove(index);
		groups.add(index + offset, group);
		for (int i = 0; i < groups.size(); i++)
		{
			groups.get(i).setId(i);
		}
	}

	public void addPointToGroup(CameraPointGroup group, CameraPoint point)
	{
		int nextId = group.getPoints().stream().map(CameraPoint::getId).max(Integer::compareTo).map(i -> i + 1).orElse(0);
		point.setId(nextId);
		group.getPoints().add(point);
	}

	public CameraPoint addPointToGroup(CameraPointGroup group)
	{
		int nextId = group.getPoints().stream().map(CameraPoint::getId).max(Integer::compareTo).map(i -> i + 1).orElse(0);
		CameraPoint newPoint = new CameraPoint(nextId, "New Camera Point", CameraPoint.Direction.NORTH, -1, false, Keybind.NOT_SET, true);
		addPointToGroup(group, newPoint);
		return newPoint;
	}

	public void removePointFromGroup(CameraPointGroup group, CameraPoint point)
	{
		group.getPoints().remove(point);
		for (int i = 0; i < group.getPoints().size(); i++)
		{
			group.getPoints().get(i).setId(i);
		}
	}

	public List<CameraPointGroup> getAllGroups()
	{
		return groups.stream().sorted(Comparator.comparingInt(CameraPointGroup::getId)).collect(Collectors.toList());
	}

	public List<CameraPointGroup> getEnabledGroups()
	{
		return groups.stream()
			.filter(CameraPointGroup::isEnabled)
			.sorted(Comparator.comparingInt(CameraPointGroup::getId))
			.collect(Collectors.toList());
	}

	public List<CameraPoint> getAllPointsForGroup(CameraPointGroup group)
	{
		return group.getPoints().stream().sorted(Comparator.comparingInt(CameraPoint::getId)).collect(Collectors.toList());
	}

	public List<CameraPoint> getEnabledPointsForGroup(CameraPointGroup group)
	{
		return group.getPoints().stream()
			.filter(CameraPoint::isEnabled)
			.sorted(Comparator.comparingInt(CameraPoint::getId))
			.collect(Collectors.toList());
	}

	public void updateCycleIndexToPoint(CameraPointGroup group, CameraPoint point)
	{
		currentPointIds.put(group.getId(), point.getId());
	}

	public CameraPoint cycleNext(CameraPointGroup group)
	{
		List<CameraPoint> enabledPoints = group.getPoints().stream().filter(CameraPoint::isEnabled).collect(Collectors.toList());
		if (enabledPoints.isEmpty())
		{
			return null;
		}

		Integer currentPointId = currentPointIds.get(group.getId());
		int currentIndex = -1;
		for (int i = 0; i < enabledPoints.size(); i++)
		{
			if (enabledPoints.get(i).getId() == (currentPointId == null ? Integer.MIN_VALUE : currentPointId))
			{
				currentIndex = i;
				break;
			}
		}
		int nextIndex = currentIndex != -1 ? (currentIndex + 1) % enabledPoints.size() : 0;
		CameraPoint nextPoint = enabledPoints.get(nextIndex);
		currentPointIds.put(group.getId(), nextPoint.getId());
		return nextPoint;
	}

	public CameraPoint cyclePrevious(CameraPointGroup group)
	{
		List<CameraPoint> enabledPoints = group.getPoints().stream().filter(CameraPoint::isEnabled).collect(Collectors.toList());
		if (enabledPoints.isEmpty())
		{
			return null;
		}

		Integer currentPointId = currentPointIds.get(group.getId());
		int currentIndex = -1;
		for (int i = 0; i < enabledPoints.size(); i++)
		{
			if (enabledPoints.get(i).getId() == (currentPointId == null ? Integer.MIN_VALUE : currentPointId))
			{
				currentIndex = i;
				break;
			}
		}
		int previousIndex = currentIndex != -1 ? (currentIndex - 1 + enabledPoints.size()) % enabledPoints.size() : enabledPoints.size() - 1;
		CameraPoint previousPoint = enabledPoints.get(previousIndex);
		currentPointIds.put(group.getId(), previousPoint.getId());
		return previousPoint;
	}
}
