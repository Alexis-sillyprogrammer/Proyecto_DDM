package com.example.proyecto_ddm.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyecto_ddm.database.entities.CartDetailEntity
import com.example.proyecto_ddm.database.entities.CartEntity

@Dao
interface CartDao {
    @Insert
    suspend fun insertCart(cart: CartEntity): Long

    @Update
    suspend fun updateCart(cart: CartEntity)

    @Query("""
        SELECT * FROM Carts
        WHERE user_id = :userId AND state_id = 1
        LIMIT 1
    """)
    suspend fun getActiveCart(userId: Int): CartEntity?

    @Query("SELECT * FROM Carts WHERE cart_id = :id LIMIT 1")
    suspend fun getCartById(id: Int): CartEntity?

    @Query("""
        SELECT * FROM Carts
        WHERE user_id = :userId AND state_id = 2
        ORDER BY cart_id DESC
    """)
    suspend fun getCompletedCarts(userId: Int): List<CartEntity>

    @Query("""
        UPDATE Carts SET state_id = 2, completed_date = :completedDate
        WHERE cart_id = :cartId
    """)
    suspend fun completeCart(cartId: Int, completedDate: String)

    @Query("DELETE FROM Carts WHERE cart_id = :cartId")
    suspend fun deleteCart(cartId: Int)

    @Insert
    suspend fun insertDetail(detail: CartDetailEntity): Long

    @Update
    suspend fun updateDetail(detail: CartDetailEntity)

    @Query("SELECT * FROM Cart_Detail WHERE cart_id = :cartId")
    suspend fun getDetailsByCart(cartId: Int): List<CartDetailEntity>

    @Query("SELECT * FROM Cart_Detail WHERE cart_id = :cartId AND product_id = :productId LIMIT 1")
    suspend fun getDetailByProduct(cartId: Int, productId: Int): CartDetailEntity?

    @Query("DELETE FROM Cart_Detail WHERE cart_detail_id = :detailId")
    suspend fun deleteDetail(detailId: Int)

    @Query("DELETE FROM Cart_Detail WHERE cart_id = :cartId")
    suspend fun deleteAllDetails(cartId: Int)

    @Query("UPDATE Cart_Detail SET quantity = :qty WHERE cart_detail_id = :id")
    suspend fun updateQuantity(id: Int, qty: Int)
}