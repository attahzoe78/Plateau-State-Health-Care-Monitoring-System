package com.example.data.repository

import android.content.Context
import com.example.data.AppDatabase
import com.example.data.entity.FacilityEntity
import com.example.data.entity.MapTileCacheEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File

class MapTileCacheRepository(
    private val db: AppDatabase,
    private val context: Context
) {

    private val mapTileCacheDao = db.mapTileCacheDao()

    val allCachedTiles: Flow<List<MapTileCacheEntity>> = mapTileCacheDao.getAllCachedTiles()
    val cachedTileCount: Flow<Int> = mapTileCacheDao.getCachedTileCountFlow()
    val totalCacheSizeBytes: Flow<Long?> = mapTileCacheDao.getTotalCacheSizeBytesFlow()

    suspend fun initializeMapTileCacheIfNeeded(facilities: List<FacilityEntity>) = withContext(Dispatchers.IO) {
        val existingCount = mapTileCacheDao.getCachedTileCount()
        if (existingCount == 0 && facilities.isNotEmpty()) {
            buildAndSaveTileCache(facilities)
        }
    }

    suspend fun rebuildTileCache(facilities: List<FacilityEntity>) = withContext(Dispatchers.IO) {
        mapTileCacheDao.clearAllTiles()
        buildAndSaveTileCache(facilities)
    }

    private suspend fun buildAndSaveTileCache(facilities: List<FacilityEntity>) {
        val tileDir = File(context.cacheDir, "map_tiles")
        if (!tileDir.exists()) {
            tileDir.mkdirs()
        }

        val lgas = facilities.map { it.lga }.distinct()
        val mapTypes = listOf("ROADMAP", "SATELLITE", "TERRAIN")
        val tileEntities = mutableListOf<MapTileCacheEntity>()

        lgas.forEachIndexed { lgaIdx, lga ->
            val lgaFacilities = facilities.filter { it.lga.equals(lga, ignoreCase = true) }
            mapTypes.forEachIndexed { typeIdx, mapType ->
                for (zoom in 1..3) {
                    val tileKey = "plateau_${lga.lowercase().replace(" ", "_")}_z${zoom}_${mapType.lowercase()}"
                    val file = File(tileDir, "$tileKey.tile")
                    
                    // Create simulated offline vector tile file payload
                    if (!file.exists()) {
                        file.writeText("OFFLINE_MAP_TILE_DATA:${lga}:${mapType}:ZOOM_${zoom}:FACILITIES_${lgaFacilities.size}")
                    }

                    tileEntities.add(
                        MapTileCacheEntity(
                            tileKey = tileKey,
                            lgaName = lga,
                            zoomLevel = zoom,
                            gridX = (lgaIdx % 4) * zoom,
                            gridY = (lgaIdx / 4) * zoom,
                            mapType = mapType,
                            facilitiesCount = lgaFacilities.size,
                            cacheSizeBytes = file.length().coerceAtLeast(1024 * 64L), // ~64 KB per tile
                            isOfflineAvailable = true,
                            localFilePath = file.absolutePath
                        )
                    )
                }
            }
        }

        mapTileCacheDao.insertTiles(tileEntities)
    }
}
