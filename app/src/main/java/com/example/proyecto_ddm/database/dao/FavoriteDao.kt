package com.example.proyecto_ddm.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyecto_ddm.database.entities.FavoriteEntity

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("DELETE FROM Favorites WHERE user_id = :userId AND product_id = :productId")
    suspend fun delete(userId: Int, productId: Int)

    @Query("SELECT * FROM Favorites WHERE user_id = :userId")
    suspend fun getByUser(userId: Int): List<FavoriteEntity>

    @Query("SELECT COUNT(*) FROM Favorites WHERE user_id = :userId")
    suspend fun countByUser(userId: Int): Int

    @Query("SELECT EXISTS(SELECT 1 FROM Favorites WHERE user_id = :userId AND product_id = :productId)")
    suspend fun isFavorite(userId: Int, productId: Int): Boolean
}