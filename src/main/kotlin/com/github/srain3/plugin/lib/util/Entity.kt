package com.github.srain3.plugin.lib.util

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector

object Entity {
    /**
     * listに対してlocを中心にxyzの箱の中に入っている物のみListで返す
     */
    fun getNearbyEntity(list: List<Entity>, loc: Location, x: Double, y: Double, z:Double): List<Entity> {
        val aabb = BoundingBox.of(loc, x, y, z)
        val reList = mutableListOf<Entity>()
        list.forEach { entity ->
            if (entity.world.uid == loc.world?.uid) {
                if (aabb.contains(entity.location.x, entity.location.y, entity.location.z)) {
                    reList.add(entity)
                }
            }
        }
        return reList
    }

    /**
     * listにあるEntityに対してのみレイトレースを試みてhitしたらtrueを返す。Distanceが0.0以下だとnullを返す。
     */
    fun rayTraceEntities(
        searchEntityList: List<Entity>,
        start: Location,
        direction: Vector,
        maxDistance: Double,
        raySize: Double
    ): Boolean? {
        if (maxDistance < 0.0) {
            return null
        } else {
            val startPos = start.toVector()
            val var17: Iterator<*> = searchEntityList.iterator()
            var hit = false

            while (var17.hasNext()) {
                val entity = var17.next() as Entity
                val boundingBox = entity.boundingBox.expand(raySize)
                val hitResult = boundingBox.rayTrace(startPos, direction, maxDistance)
                if (hitResult != null) {
                    hit = true
                    break
                }
            }

            return hit
        }
    }
}