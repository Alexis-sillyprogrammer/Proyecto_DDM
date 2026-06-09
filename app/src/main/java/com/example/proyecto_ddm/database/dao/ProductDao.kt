package com.example.proyecto_ddm.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyecto_ddm.database.entities.ProductEntity

@Dao
interface ProductDao {
    @Insert
    suspend fun insert(product: ProductEntity): Long

    @Update
    suspend fun update(product: ProductEntity)

    @Delete
    suspend fun delete(product: ProductEntity)

    @Query("SELECT * FROM Products ORDER BY product_id DESC")
    suspend fun getAll(): List<ProductEntity>

    @Query("SELECT * FROM Products WHERE product_id = :id LIMIT 1")
    suspend fun getById(id: Int): ProductEntity

    @Query("SELECT * FROM Products WHERE category_id = :categoryId ORDER BY product_id DESC")
    suspend fun getByCategory(categoryId: Int): List<ProductEntity>

    @Query("SELECT * FROM Products WHERE stock > 0 ORDER BY product_id DESC")
    suspend fun getAvailable(): List<ProductEntity>

    @Query("UPDATE Products SET stock = stock - :qty WHERE product_id = :id AND stock >= :qty")
    suspend fun decrementStock(id: Int, qty: Int)

    @Query("""
        SELECT * FROM Products
        WHERE LOWER(name) = LOWER(:name) AND category_id = :categoryId
        LIMIT 1
    """)
    suspend fun getByNameAndCategory(name: String, categoryId: Int): ProductEntity?

    @Query("UPDATE Products SET stock = stock + :qty WHERE product_id = :id")
    suspend fun incrementStock(id: Int, qty: Int)
}