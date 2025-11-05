package com.camerapoints

import com.camerapoints.models.CameraPoint
import com.camerapoints.models.CameraPointGroup
import net.runelite.client.config.Keybind

class CameraPointGroupManager {

    private val groups: MutableList<CameraPointGroup> = mutableListOf()

    private val currentPointIds = mutableMapOf<Int, Int>()

    fun setGroups(groups: List<CameraPointGroup>) {
        this.groups.clear()
        this.groups.addAll(groups)
    }

    fun addGroup(group: CameraPointGroup) {
        group.id = groups.maxOfOrNull { it.id }?.plus(1) ?: 0
        groups.add(group)
    }

    fun addGroup(): CameraPointGroup {
        val newGroup = CameraPointGroup(
            -1,
            "New Group",
            true,
            Keybind.NOT_SET,
            Keybind.NOT_SET,
            mutableListOf()
        )
        addGroup(newGroup)
        return newGroup
    }

    fun removeGroup(group: CameraPointGroup) {
        groups.remove(group)
        groups.forEachIndexed { index, cameraPointGroup -> cameraPointGroup.id = index }
    }

    fun moveGroup(group: CameraPointGroup, offset: Int) {
        val index = groups.indexOf(group)
        if (index == -1 || index + offset < 0 || index + offset >= groups.size)
            return
        groups.removeAt(index)
        groups.add(index + offset, group)
        groups.forEachIndexed { index, cameraPointGroup -> cameraPointGroup.id = index }
    }

    fun addPointToGroup(group: CameraPointGroup, point: CameraPoint) {
        point.id = group.points.maxOfOrNull { it.id }?.plus(1) ?: 0
        group.points.add(point)
    }

    fun addPointToGroup(group: CameraPointGroup): CameraPoint {
        val newPoint = CameraPoint(
            group.points.maxOfOrNull { it.id }?.plus(1) ?: 0,
            "New Camera Point",
            -1,
            -1,
            -1,
            Keybind.NOT_SET,
            true
        )
        addPointToGroup(group, newPoint)
        return newPoint
    }

    fun removePointFromGroup(group: CameraPointGroup, point: CameraPoint) {
        group.points.remove(point)
        group.points.forEachIndexed { index, cameraPoint -> cameraPoint.id = index }
    }

    fun getAllGroups(): List<CameraPointGroup> = groups.sortedBy { it.id }

    fun getEnabledGroups(): List<CameraPointGroup> = groups.filter { it.enabled }.sortedBy { it.id }

    fun getAllPointsForGroup(group: CameraPointGroup): List<CameraPoint> = group.points.sortedBy { it.id }

    fun getEnabledPointsForGroup(group: CameraPointGroup): List<CameraPoint> = group.points.filter { it.enabled }.sortedBy { it.id }

    fun updateCycleIndexToPoint(group: CameraPointGroup, point: CameraPoint) {
        currentPointIds[group.id] = point.id
    }

    fun cycleNext(group: CameraPointGroup): CameraPoint? {
        val enabledPoints = group.points.filter { it.enabled }

        if (enabledPoints.isEmpty())
            return null

        val currentPointId = currentPointIds[group.id]
        val currentIndex = enabledPoints.indexOfFirst { it.id == currentPointId }
        val nextIndex = if (currentIndex != -1) (currentIndex + 1) % enabledPoints.size  else 0

        val nextPoint = enabledPoints[nextIndex]
        currentPointIds[group.id] = nextPoint.id

        return nextPoint
    }

    fun cyclePrevious(group: CameraPointGroup): CameraPoint? {
        val enabledPoints = group.points.filter { it.enabled }

        if (enabledPoints.isEmpty())
            return null

        val currentPointId = currentPointIds[group.id]
        val currentIndex = enabledPoints.indexOfFirst { it.id == currentPointId }
        val previousIndex = if (currentIndex != -1) (currentIndex - 1 + enabledPoints.size) % enabledPoints.size else enabledPoints.size - 1

        val previousPoint = enabledPoints[previousIndex]
        currentPointIds[group.id] = previousPoint.id

        return previousPoint
    }
}