package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "map_tile_cache")
data class MapTileCacheEntity(
    @PrimaryKey
    val tileKey: String, // e.g. "plateau_z1_x1_y1_ROADMAP"
    val lgaName: String, // e.g. "Jos North" or "Plateau State Grid"
    val zoomLevel: Int,
    val gridX: Int,
    val gridY: Int,
    val mapType: String,
    val facilitiesCount: Int,
    val cacheSizeBytes: Long,
    val cachedAtTimestamp: Long = System.currentTimeMillis(),
    val isOfflineAvailable: Boolean = true,
    val localFilePath: String = "cache/map_tiles/$tileKey.tile"
)
