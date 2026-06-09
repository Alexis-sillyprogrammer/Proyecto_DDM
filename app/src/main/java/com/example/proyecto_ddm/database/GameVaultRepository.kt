package com.example.proyecto_ddm.database

import android.content.Context
import com.example.proyecto_ddm.database.entities.ProductEntity
import com.example.proyecto_ddm.database.entities.UserEntity
import com.example.proyecto_ddm.models.Category
import com.example.proyecto_ddm.models.Product

class GameVaultRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val productDao = db.productDao()
    private val categoryDao = db.categoryDao()

    suspend fun login(email: String, password: String): UserEntity? = userDao.login(email, password)
    suspend fun register(name: String, email: String, password: String): Boolean {
        val exists = userDao.getByEmail(email) != null
        if(exists) return false

        userDao.insert(UserEntity(
            name = name,
            email = email,
            password = password,
            rol_id = 2
        ))

        return true
    }

    suspend fun isAdmin(userId: Int): Boolean {
        val user = userDao.getById(userId) ?: return false
        return user.rol_id == 1
    }

    suspend fun insertProduct(
        name: String, categoryId: Int, description: String, price: Float,
        stock: Int, imgPath: String?, adminId: Int
    ): Long {
        val existing = productDao.getByNameAndCategory(name, categoryId)

        return if(existing != null) {
            productDao.incrementStock(existing.product_id, stock)
            existing.product_id.toLong()
        } else
            productDao.insert(ProductEntity(
                name = name,
                category_id = categoryId,
                description = description,
                price = price,
                stock = stock,
                img_path = imgPath,
                id_admin = adminId
            ))
    }

    suspend fun getAllProducts(): List<Product> = productDao.getAll().mapToModel()
    suspend fun getAvailableProducts(): List<Product> = productDao.getAvailable().mapToModel()
    suspend fun getProductById(id: Int): Product? =
        productDao.getById(id)?.let { entity ->
            val cat = categoryDao.getById(entity.category_id)
            entity.toModel(cat?.name ?: "")
        }

    suspend fun getAllCategories(): List<Category> =
        categoryDao.getAll().map { Category(it.category_id, it.name) }

    private suspend fun List<ProductEntity>.mapToModel(): List<Product> =
        map { entity ->
            val cat = categoryDao.getById(entity.category_id)
            entity.toModel(cat?.name ?: "")
        }

    private fun ProductEntity.toModel(catName: String) = Product(
        id = product_id,
        name = name,
        category = Category(category_id, catName),
        description = description,
        price = price,
        img = img_path
    )
}