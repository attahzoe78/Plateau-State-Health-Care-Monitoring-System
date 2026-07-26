package com.example.data.dao

import androidx.room.*
import com.example.data.entity.MapTileCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MapTileCacheDao {

    @Query("SELECT * FROM map_tile_cache ORDER BY cachedAtTimestamp DESC")
    fun getAllCachedTiles(): Flow<List<MapTileCacheEntity>>

    @Query("SELECT * FROM map_tile_cache WHERE lgaName = :lga")
    fun getTilesForLga(lga: String): Flow<List<MapTileCacheEntity>>

    @Query("SELECT COUNT(*) FROM map_tile_cache")
    fun getCachedTileCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM map_tile_cache")
    suspend fun getCachedTileCount(): Int

    @Query("SELECT SUM(cacheSizeBytes) FROM map_tile_cache")
    fun getTotalCacheSizeBytesFlow(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTile(tile: MapTileCacheEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTiles(tiles: List<MapTileCacheEntity>)

    @Query("DELETE FROM map_tile_cache")
    suspend fun clearAllTiles()
}
